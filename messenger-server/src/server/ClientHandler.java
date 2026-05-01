package server;

import managers.*;
import model.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket            socket;
    private final MessageDispatcher dispatcher;
    private final ConnectionManager connectionManager;
    private final AuthManager       authManager;
    private final CommandHandler    commandHandler;

    private ObjectInputStream  in;
    private ObjectOutputStream out;

    private User        currentUser = null;
    private ClientState state       = ClientState.CONNECTED;

    public ClientHandler(Socket socket,
                         MessageDispatcher dispatcher,
                         ConnectionManager connectionManager,
                         AuthManager authManager,
                         CommandHandler commandHandler) {
        this.socket            = socket;
        this.dispatcher        = dispatcher;
        this.connectionManager = connectionManager;
        this.authManager       = authManager;
        this.commandHandler    = commandHandler;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            Object obj;
            while ((obj = in.readObject()) != null) {
                if (state != ClientState.AUTHENTICATED) {
                    handleAuth(obj);
                } else if (obj instanceof Command cmd) {
                    send(commandHandler.handle(cmd, currentUser));
                } else if (obj instanceof AbstractMessage msg) {
                    dispatcher.dispatch(msg);
                }
            }

        } catch (EOFException ignored) {
        } catch (Exception e) {
            System.out.println("Клиент отключился: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void handleAuth(Object obj) {
        if (!(obj instanceof AuthRequest req)) {
            send(AuthResponse.error("Invalid request"));
            return;
        }

        if (req.isRegister) {
            currentUser = authManager.register(req.username, req.email, req.password);
            if (currentUser != null) {
                onAuthSuccess("Registered and logged in");
            } else {
                send(AuthResponse.error("Username already taken"));
            }
        } else {
            currentUser = authManager.login(req.username, req.password);
            if (currentUser != null) {
                onAuthSuccess("OK");
            } else {
                send(AuthResponse.error("Wrong credentials"));
            }
        }
    }

    private void onAuthSuccess(String message) {
        state = ClientState.AUTHENTICATED;
        connectionManager.addUser(currentUser.getId(), this, currentUser);
        send(new AuthResponse(currentUser.getId(),true, message));

        // Уведомить всех онлайн что юзер появился
        connectionManager.broadcastEvent(
                new ServerEvent(ServerEventType.USER_ONLINE,
                        currentUser.getUsername() + " онлайн",
                        currentUser.getId()),
                currentUser.getId()
        );

        System.out.println("Auth OK: " + currentUser.getUsername());
    }

    private void disconnect() {
        if (currentUser != null) {
            connectionManager.broadcastEvent(
                    new ServerEvent(ServerEventType.USER_OFFLINE,
                            currentUser.getUsername() + " офлайн",
                            currentUser.getId()),
                    currentUser.getId()
            );
            connectionManager.removeUser(currentUser.getId());
        }
        try { socket.close(); } catch (IOException ignored) {}
    }

    public void sendMessage(AbstractMessage msg) { send(msg); }
    public void sendEvent(ServerEvent event)     { send(event); }

    private void send(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.out.println("Ошибка отправки: " + e.getMessage());
        }
    }
}