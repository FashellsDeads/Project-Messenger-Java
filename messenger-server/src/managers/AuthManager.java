package managers;

import model.*;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {

    private final Map<String, User> usersByName = new HashMap<>();

    public ChatManager chatManager;

    public  AuthManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public User register(String username,String email, String password) {
        if (usersByName.containsKey(username)) {
            throw new RuntimeException("User exists");
        }

        User user = new User(IdGenerator.generateId(), username,email,password);
        usersByName.put(username, user);
        chatManager.addChat(new SelfChat(-1,user));

        return user;
    }

    public User login(String username, String password) {

        User user = usersByName.get(username);

        if (user == null) return null;

        if (!user.getPasswordHash().equals(password)) return null;

        return user;
    }
}