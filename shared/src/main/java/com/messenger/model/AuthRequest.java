package com.messenger.model;

import java.io.Serializable;

public class AuthRequest implements Serializable {
    public String username;
    public String email;
    public String password;
    public  boolean isRegister;

    public AuthRequest(String username,String email, String password, boolean isRegister) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isRegister = isRegister;
    }
}