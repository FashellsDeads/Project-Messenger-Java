package managers;

import model.ServerEvent;
import model.User;
import server.ClientHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<Integer, ClientHandler> activeHandlers = new ConcurrentHashMap<>();
    private final Map<Integer, User>          activeUsers    = new ConcurrentHashMap<>();

    public void addUser(int userId, ClientHandler handler, User user) {
        activeHandlers.put(userId, handler);
        activeUsers.put(userId, user);
    }

    public void removeUser(int userId) {
        activeHandlers.remove(userId);
        activeUsers.remove(userId);
    }

    public ClientHandler getHandler(int userId) {
        return activeHandlers.get(userId);
    }

    public List<User> getOnlineUsers() {
        return new ArrayList<>(activeUsers.values());
    }

    // Рассылка события всем онлайн, кроме одного юзера
    public void broadcastEvent(ServerEvent event, int excludeUserId) {
        activeHandlers.forEach((userId, handler) -> {
            if (userId != excludeUserId)
                handler.sendEvent(event);
        });
    }
}