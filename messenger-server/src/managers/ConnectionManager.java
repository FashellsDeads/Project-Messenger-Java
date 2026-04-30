package managers;

import model.User;
import server.ClientHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<Integer, ClientHandler> activeUsers = new ConcurrentHashMap<>();

    private Map<Integer, User> users = new ConcurrentHashMap<>();

    public void addUser(int userId, ClientHandler handler) {
        activeUsers.put(userId, handler);
    }

    public void removeUser(int userId) {
        activeUsers.remove(userId);
    }

    public ClientHandler getUser(int userId) {
        return activeUsers.get(userId);
    }
}