package managers;

import com.messenger.db.UserDAO;
import com.messenger.model.SelfChat;
import com.messenger.model.User;
import com.messenger.model.UserStatus;
import com.messenger.protocol.IdGenerator;

import java.util.Optional;

public class AuthManager {

    private final ChatManager chatManager;
    private final UserDAO userDAO;

    public AuthManager(ChatManager chatManager, UserDAO userDAO) {
        this.chatManager = chatManager;
        this.userDAO     = userDAO;
    }

    public User register(String username, String email, String password) {
        // Проверяем БД, а не HashMap
        if (userDAO.existsByUsername(username)) return null;

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password); // позже сюда придёт BCrypt
        user.setStatus(UserStatus.OFFLINE);

        User saved = userDAO.save(user); // id присваивает БД
        if (saved == null) return null;

        chatManager.addChat(new SelfChat(IdGenerator.generateId(), saved));
        return saved;
    }

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) return null;
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userDAO.findByUsername(username));
    }
}