package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String email;
    private String password;

    private model.UserStatus status;
    private LocalDateTime lastSeen;

    public User(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = model.UserStatus.OFFLINE;
        this.lastSeen = LocalDateTime.now();
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public model.UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    // setters (контролируемые)

    public void setStatus(model.UserStatus status) {
        this.status = status;
        this.lastSeen = LocalDateTime.now();
    }

    public void setUsername(String username) {
        this.username = username;
    }
}