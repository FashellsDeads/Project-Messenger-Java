package model;

import java.io.Serializable;

public class AuthRequest implements Serializable {
    public String username;
    public String password;
    public  boolean isRegister;

    public AuthRequest(String username, String password, boolean isRegister) {
        this.username = username;
        this.password = password;
        this.isRegister = isRegister;
    }
}