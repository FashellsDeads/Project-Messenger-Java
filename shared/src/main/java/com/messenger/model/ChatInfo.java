package com.messenger.model;

import java.io.Serializable;

public class ChatInfo implements Serializable {

    private final int    id;
    private final String type;
    private final String name;

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