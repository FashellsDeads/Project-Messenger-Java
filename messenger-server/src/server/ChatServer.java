package server;

import managers.*;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private final int port;

    private final ChatManager chatManager = new ChatManager();
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final MessageDispatcher dispatcher =
            new MessageDispatcher(chatManager, connectionManager);
    private final AuthManager authManager =
            new AuthManager(chatManager);

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Сервер запускается...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новый клиент подключился");

                ClientHandler handler = new ClientHandler(
                        clientSocket,
                        dispatcher,
                        connectionManager,
                        authManager
                );

                new Thread(handler).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}