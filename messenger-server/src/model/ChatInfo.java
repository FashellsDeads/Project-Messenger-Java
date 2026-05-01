package model;

import java.io.Serializable;

// Лёгкий DTO — клиент получает это вместо полного объекта Chat
public class ChatInfo implements Serializable {

    private final int    id;
    private final String type; // "SELF" | "PRIVATE" | "CHANNEL"
    private final String name; // для PRIVATE — имя собеседника, для CHANNEL — название

    public ChatInfo(int id, String type, String name) {
        this.id   = id;
        this.type = type;
        this.name = name;
    }

    public int    getId()   { return id; }
    public String getType() { return type; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "[" + type + "] " + name + " (id=" + id + ")";
    }
}