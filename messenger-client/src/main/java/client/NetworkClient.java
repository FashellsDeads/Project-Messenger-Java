package client;

import com.messenger.model.*;
import com.messenger.protocol.*;

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
    private EventDispatcher dispatcher;
    private final CommandAPI commands = new CommandAPI(this);

    private volatile boolean connected = false;

    private volatile boolean running = true;

    public CommandAPI commands() {
        return commands;
    }

    public void connect(String host, int port, NetworkListener listener, EventDispatcher dispatcher) throws IOException {
        try {
            this.listener = listener;
            this.dispatcher = dispatcher;
            running = true;
            this.socket = new Socket(host, port);

            System.out.println("Socket connected");


            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());

            connected = true;
            System.out.println("Streams ready");

            listenerThread = new Thread(this::listenForPackets);
            listenerThread.setDaemon(true);
            listenerThread.start();

        } catch (IOException e) {
            connected = false;
            System.err.println("CONNECT ERROR: " + e.getMessage());
            throw e;
        }
    }


    @SuppressWarnings("unchecked")
    private void listenForPackets() {
        try {
            Object obj;
            while (running && (obj = in.readObject()) != null) {
                if (obj instanceof Packet<?> packet) {

                    if (dispatcher != null) {
                        dispatcher.dispatch(() -> processPacket(packet));
                    } else {
                        processPacket(packet);
                    }
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                if (dispatcher != null) {
                    dispatcher.dispatch(() -> listener.onDisconnected(e.getMessage()));
                } else {
                    listener.onDisconnected(e.getMessage());
                }
            }
        } finally {
            disconnect();
        }
    }


    private void processPacket(Packet<?> packet) {
        if (listener == null) return;

        System.out.println("Получен пакет от сервера: " + packet.getType());

        if (!packet.isSuccess()) {
            listener.onError(packet.getErrorMessage());
            return;
        }

        switch (packet.getType()) {
            case LOGIN_RESPONSE:
            case REGISTER_RESPONSE:
                listener.onLoginSuccess((User) packet.getPayload());
                break;

            case MESSAGE_BROADCAST:
                safeCall(() -> listener.onMessageReceived((AbstractMessage) packet.getPayload()));
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
            case COMMAND_RESPONSE:
                listener.onCommandResponse((CommandResponse) packet.getPayload());
                break;

            case EVENT:
                safeCall(() -> listener.onServerEvent((ServerEvent) packet.getPayload()));
                break;

            default:
                System.out.println("Получен необрабатываемый пакет: " + packet.getType());
        }
    }
    private void safeCall(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet<?> packet) {

        if (!isConnected()) {
            System.err.println("Не подключено к серверу");
            return;
        }

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

    public void register(String username, String email, String password) {
        sendPacket(new Packet<>(PacketType.REGISTER_REQUEST,
                new RegisterRequest(username, email, password)));
    }


    public void reconnect(String host, int port) throws IOException {
        disconnect();
        connect(host, port, listener, dispatcher);
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
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

        connected = false;
        running = false;
        try {
            sendPacket(new Packet<>(PacketType.DISCONNECT, null));

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
    public void setListener(NetworkListener listener) {
        this.listener = listener;
    }
}