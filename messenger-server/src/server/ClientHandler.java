package server;

import com.messenger.model.*;
import com.messenger.protocol.LoginRequest;
import com.messenger.protocol.Packet;
import com.messenger.protocol.PacketType;
import com.messenger.protocol.RegisterRequest;
import managers.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final MessageDispatcher dispatcher;
    private final ConnectionManager connectionManager;
    private final AuthManager authManager;
    private final CommandHandler commandHandler;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private User currentUser = null;
    private ClientState state = ClientState.CONNECTED;

    public ClientHandler(Socket socket,
                         MessageDispatcher dispatcher,
                         ConnectionManager connectionManager,
                         AuthManager authManager,
                         CommandHandler commandHandler) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        this.connectionManager = connectionManager;
        this.authManager = authManager;
        this.commandHandler = commandHandler;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof Packet<?> packet) {
                    handlePacket(packet);
                } else {
                    System.out.println("Получен неизвестный объект: " + obj.getClass());
                }
            }

        } catch (EOFException ignored) {
        } catch (Exception e) {
            System.out.println("Клиент отключился: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void handlePacket(Packet<?> packet) {

        if (state != ClientState.AUTHENTICATED) {
            handleAuth(packet);
            return;
        }

        switch (packet.getType()) {

            case SEND_MESSAGE: {
                AbstractMessage msg = (AbstractMessage) packet.getPayload();
                dispatcher.dispatch(msg);
                break;
            }

            case COMMAND: {
                Command cmd = (Command) packet.getPayload();
                Serializable response = commandHandler.handle(cmd, currentUser);
                sendPacket(new Packet<>(PacketType.COMMAND_RESPONSE, response));
                break;
            }

            case DISCONNECT:
                disconnect();
                break;

            default:
                System.out.println("Неизвестный пакет: " + packet.getType());
        }
    }

    private void handleAuth(Packet<?> packet) {

        switch (packet.getType()) {

            case LOGIN_REQUEST: {
                LoginRequest req = (LoginRequest) packet.getPayload();

                try {
                    currentUser = authManager.login(req.getEmail(), req.getPasswordHash());
                    onAuthSuccess();
                } catch (Validator.ValidationException e) {
                    sendPacket(Packet.error(PacketType.LOGIN_RESPONSE, e.getMessage()));
                }
                break;
            }

            case REGISTER_REQUEST: {
                RegisterRequest req = (RegisterRequest) packet.getPayload();

                try {
                    currentUser = authManager.register(
                            req.getUsername(),
                            req.getEmail(),
                            req.getPasswordHash()
                    );
                    onAuthSuccess();
                } catch (Validator.ValidationException e) {
                    sendPacket(Packet.error(PacketType.REGISTER_RESPONSE, e.getMessage()));
                }
                break;
            }

            default:
                sendPacket(Packet.error(PacketType.ERROR, "Требуется авторизация"));
        }
    }

    private void onAuthSuccess() {
        state = ClientState.AUTHENTICATED;

        connectionManager.addUser(currentUser.getId(), this, currentUser);

        sendPacket(new Packet<>(PacketType.LOGIN_RESPONSE, currentUser));

        ServerEvent event = new ServerEvent(
                ServerEventType.USER_ONLINE,
                currentUser.getUsername() + " онлайн",
                currentUser.getId()
        );

        connectionManager.broadcastEvent(event, currentUser.getId());

        System.out.println("Auth OK: " + currentUser.getUsername());
    }

    private void disconnect() {
        if (currentUser != null) {
            ServerEvent event = new ServerEvent(
                    ServerEventType.USER_OFFLINE,
                    currentUser.getUsername() + " офлайн",
                    currentUser.getId()
            );

            connectionManager.broadcastEvent(event, currentUser.getId());
            connectionManager.removeUser(currentUser.getId());
        }

        try { socket.close(); } catch (IOException ignored) {}
    }

    public void sendMessage(AbstractMessage msg) {
        sendPacket(new Packet<>(PacketType.MESSAGE_BROADCAST, msg));
    }

    public void sendEvent(ServerEvent event) {
        sendPacket(new Packet<>(PacketType.EVENT, event));
    }

    private void sendPacket(Packet<?> packet) {
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            System.out.println("Ошибка отправки: " + e.getMessage());
        }
    }
}