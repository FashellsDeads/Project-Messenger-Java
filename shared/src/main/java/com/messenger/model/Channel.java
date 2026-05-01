package com.messenger.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Модель канала внутри сервера.
 */
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int serverId;
    private LocalDateTime createdAt;

    public Channel() {}

    public Channel(int id, String name, int serverId) {
        this.id = id;
        this.name = name;
        this.serverId = serverId;
        this.createdAt = LocalDateTime.now();
    }

    public Channel(String name, int serverId) {
        this.name = name;
        this.serverId = serverId;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getServerId() { return serverId; }
    public void setServerId(int serverId) { this.serverId = serverId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "# " + name;
    }
}
