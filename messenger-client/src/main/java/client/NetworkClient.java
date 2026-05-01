package client;

import com.messenger.model.AbstractMessage;
import com.messenger.model.User;
import com.messenger.model.MessengerServer;
import com.messenger.model.Channel;
import com.messenger.protocol.LoginRequest;
import com.messenger.protocol.RegisterRequest;
import com.messenger.protocol.Packet;
import com.messenger.protocol.PacketType;
import javafx.application.Platform;
import model.Command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class NetworkClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenerThread;
    private NetworkListener listener;

    public void connect(String host, int port, NetworkListener listener) throws IOException {
        this.listener = listener;
        this.socket = new Socket(host, port);

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());

        listenerThread = new Thread(this::listenForPackets);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    @SuppressWarnings("unchecked")
    private void listenForPackets() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof Packet<?> packet) {
                    // Все обновления интерфейса делаем в потоке JavaFX
                    Platform.runLater(() -> processPacket(packet));
                }
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                if (listener != null) listener.onDisconnected(e.getMessage());
            });
        } finally {
            disconnect();
        }
    }

    @SuppressWarnings("unchecked")
    private void processPacket(Packet<?> packet) {
        if (listener == null) return;

        if (!packet.isSuccess()) {
            listener.onError(packet.getErrorMessage());
            return;
        }

        Object payload = packet.getPayload();

        switch (packet.getType()) {
            case LOGIN_RESPONSE:
            case REGISTER_RESPONSE:
                if (payload instanceof User user) {
                    listener.onLoginSuccess(user);
                }
                break;

            case MESSAGE_BROADCAST:
                if (payload instanceof AbstractMessage msg) {
                    listener.onMessageReceived(msg);
                }
                break;

            case CHANNEL_HISTORY:
                if (payload instanceof List<?> list) {
                    listener.onChannelHistoryReceived((List<AbstractMessage>) list);
                }
                break;

            case SERVERS_LIST:
                if (payload instanceof List<?> list) {
                    listener.onServersListReceived((List<MessengerServer>) list);
                }
                break;

            case CHANNELS_LIST:
                if (payload instanceof List<?> list) {
                    listener.onChannelsListReceived((List<Channel>) list);
                }
                break;

            case ERROR:
                if (payload instanceof String errorMsg) {
                    listener.onError(errorMsg);
                }
                break;

            default:
                listener.onError("Unknown packet type received: " + packet.getType());
        }
    }
    //da
    public void sendPacket(Packet<?> packet) {
        if (out != null && !socket.isClosed()) {
            try {
                out.writeObject(packet);
                out.flush();
            } catch (IOException e) {
                System.err.println("Ошибка отправки пакета: " + e.getMessage());
                disconnect();
            }
        }
    }

    private void sendEmptyPacket(PacketType type) {
        sendPacket(new Packet<>(type, ""));
    }

    // --- API Клиента ---
    public void sendCommand(Command command) {
        sendPacket(new Packet<>(PacketType.COMMAND, command));
    }
    public void login(String email, String passwordHash) {
        LoginRequest req = new LoginRequest(email, passwordHash);
        sendPacket(new Packet<>(PacketType.LOGIN_REQUEST, req));
    }

    public void register(String username, String email, String passwordHash) {
        RegisterRequest req = new RegisterRequest(username, email, passwordHash);
        sendPacket(new Packet<>(PacketType.REGISTER_REQUEST, req));
    }

    public void sendMessage(AbstractMessage message) {
        sendPacket(new Packet<>(PacketType.SEND_MESSAGE, message));
    }

    public void requestServers() {
        sendEmptyPacket(PacketType.GET_SERVERS);
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed() && out != null) {
                sendEmptyPacket(PacketType.DISCONNECT);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}