package server;

import managers.*;
import model.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final MessageDispatcher dispatcher;
    private final ConnectionManager connectionManager;
    private final AuthManager authManager;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private User currentUser = null;
    private ClientState state = ClientState.CONNECTED;

    public ClientHandler(Socket socket,
                         MessageDispatcher dispatcher,
                         ConnectionManager connectionManager,
                         AuthManager authManager) {
        this.socket = socket;
        this.dispatcher = dispatcher;
        this.connectionManager = connectionManager;
        this.authManager = authManager;
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
                } else if (obj instanceof AbstractMessage msg) {
                    dispatcher.dispatch(msg);
                }
            }

        } catch (EOFException ignored) {
            // клиент закрыл соединение нормально
        } catch (Exception e) {
            System.out.println("Клиент отключился: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void handleAuth(Object obj) {
        if (!(obj instanceof AuthRequest req)) {
            sendAuthResponse(new AuthResponse(false, "Invalid request"));
            return;
        }

        if (req.isRegister) {
            // регистрация
            currentUser = authManager.register(req.username,"email shold be here", req.password);
            if (currentUser != null) {
                state = ClientState.AUTHENTICATED;
                connectionManager.addUser(currentUser.getId(), this);
                sendAuthResponse(new AuthResponse(true, "Registered and logged in"));
                System.out.println("User registered: " + currentUser.getUsername());
            } else {
                sendAuthResponse(new AuthResponse(false, "Username already taken"));
            }
        } else {
            // логин
            currentUser = authManager.login(req.username, req.password);
            if (currentUser != null) {
                state = ClientState.AUTHENTICATED;
                connectionManager.addUser(currentUser.getId(), this);
                sendAuthResponse(new AuthResponse(true, "OK"));
                System.out.println("User logged in: " + currentUser.getUsername());
            } else {
                sendAuthResponse(new AuthResponse(false, "Wrong credentials"));
            }
        }
    }

    // отдельный метод — AuthResponse не AbstractMessage
    private void sendAuthResponse(AuthResponse response) {
        try {
            out.writeObject(response);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(AbstractMessage msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (currentUser != null) {
            connectionManager.removeUser(currentUser.getId());
        }
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}