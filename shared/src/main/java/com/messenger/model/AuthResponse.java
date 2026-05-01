package com.messenger.model;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    public boolean success;
    public String message;
    public int userId;

    public AuthResponse(int userID,boolean success, String message) {
        this.success = success;
        this.message = message;
        this.userId = userID;
    }
    public static AuthResponse error(String message) {
        return new AuthResponse(-1, false, message);
    }
}