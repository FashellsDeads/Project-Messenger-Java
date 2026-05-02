package server;

import com.messenger.db.ChannelDAO;
import com.messenger.db.ChannelMemberDAO;
import com.messenger.db.MessageDAO;
import com.messenger.db.UserDAO;
import managers.*;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private final int port;

    // DAO слой
    private final UserDAO    userDAO    = new UserDAO();
    private final ChannelMemberDAO channelMemberDAO = new ChannelMemberDAO();
    private final ChannelDAO channelDAO = new ChannelDAO(channelMemberDAO);
    private final MessageDAO messageDAO = new MessageDAO();

    // Менеджеры
    private final ChatManager       chatManager       = new ChatManager(channelDAO, messageDAO,channelMemberDAO);
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final MessageDispatcher dispatcher        =
            new MessageDispatcher(chatManager, connectionManager, messageDAO);
    private final AuthManager       authManager       =
            new AuthManager(chatManager, userDAO);
    private final CommandHandler    commandHandler    =
            new CommandHandler(authManager, chatManager, connectionManager);

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Сервер запускается на порту " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("✅ Сервер запущен");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новый клиент подключился");
                new Thread(new ClientHandler(
                        clientSocket, dispatcher,
                        connectionManager, authManager, commandHandler
                )).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}