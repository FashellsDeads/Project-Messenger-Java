package model;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    public boolean success;
    public String message;

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}