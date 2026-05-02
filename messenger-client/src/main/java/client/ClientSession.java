package client;

import com.messenger.model.User;

public class ClientSession {
    private static ClientSession instance;
    private User currentUser;

    private ClientSession() {}

    public static ClientSession getInstance() {
        if (instance == null) {
            instance = new ClientSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void clearSession() {
        this.currentUser = null;
    }
}