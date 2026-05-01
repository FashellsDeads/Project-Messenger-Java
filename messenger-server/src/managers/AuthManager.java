package managers;

import model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthManager {

    private final Map<String, User> usersByName = new HashMap<>();
    public final ChatManager chatManager;

    public AuthManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public User register(String username, String email, String password) {
        if (usersByName.containsKey(username)) return null; // ← убрали throw

        User user = new User(IdGenerator.generateId(), username, email, password);
        usersByName.put(username, user);
        chatManager.addChat(new SelfChat(IdGenerator.generateId(), user)); // ← исправлен id
        return user;
    }

    public User login(String username, String password) {
        User user = usersByName.get(username);
        if (user == null || !user.getPassword().equals(password)) return null;
        return user;
    }

    // Нужен для CREATE_PRIVATE_CHAT — ищем собеседника по имени
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByName.get(username));
    }
}