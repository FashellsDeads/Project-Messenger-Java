package client;

import com.messenger.model.AbstractMessage;
import com.messenger.model.User;
import com.messenger.model.MessengerServer;
import com.messenger.model.Channel;
import com.messenger.protocol.LoginRequest;
import com.messenger.protocol.Packet;
import com.messenger.protocol.PacketType;
import javafx.application.Platform;

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
            Object obj;
            while ((obj = in.readObject()) != null) {
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


    private void processPacket(Packet<?> packet) {
        if (listener == null) return;

        if (!packet.isSuccess()) {
            listener.onError(packet.getErrorMessage());
            return;
        }

        // Маршрутизация в зависимости от типа пакета
        switch (packet.getType()) {
            case LOGIN_RESPONSE:
            case REGISTER_RESPONSE:
                listener.onLoginSuccess((User) packet.getPayload());
                break;

            case MESSAGE_BROADCAST:
                listener.onMessageReceived((AbstractMessage) packet.getPayload());
                break;

            case CHANNEL_HISTORY:
                listener.onChannelHistoryReceived((List<AbstractMessage>) packet.getPayload());
                break;

            case SERVERS_LIST:
                listener.onServersListReceived((List<MessengerServer>) packet.getPayload());
                break;

            case CHANNELS_LIST:
                listener.onChannelsListReceived((List<Channel>) packet.getPayload());
                break;

            case ERROR:
                listener.onError((String) packet.getPayload());
                break;

            default:
                System.out.println("Получен необрабатываемый пакет: " + packet.getType());
        }
    }

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


    public void login(String email, String passwordHash) {
        LoginRequest req = new LoginRequest(email, passwordHash);
        sendPacket(new Packet<>(PacketType.LOGIN_REQUEST, req));
    }

    public void sendMessage(AbstractMessage message) {
        sendPacket(new Packet<>(PacketType.SEND_MESSAGE, message));
    }

    public void requestServers() {
        sendPacket(new Packet<>(PacketType.GET_SERVERS, null));
    }

    public void disconnect() {
        try {
            sendPacket(new Packet<>(PacketType.DISCONNECT, null));

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}