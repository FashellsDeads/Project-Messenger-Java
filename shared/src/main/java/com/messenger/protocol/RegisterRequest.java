package com.messenger.protocol;

import java.io.Serializable;

public class RegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String passwordHash;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}