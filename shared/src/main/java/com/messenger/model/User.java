package com.messenger.model;

import java.io.Serializable;

/**
 * Модель пользователя.
 * OOP: Класс с инкапсуляцией (private поля + геттеры/сеттеры).
 * Serializable — для передачи по сети через ObjectOutputStream.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private UserStatus status;
    private Role role;

    // ─── Конструкторы ────────────────────────────────────────────────────────

    public User() {
        this.status = UserStatus.OFFLINE;
        this.role = Role.MEMBER;
    }

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.status = UserStatus.OFFLINE;
        this.role = Role.MEMBER;
    }

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = UserStatus.OFFLINE;
        this.role = Role.MEMBER;
    }

    // ─── Геттеры и сеттеры ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    // ─── Утилиты ─────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', status=" + status + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
