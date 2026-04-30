package com.messenger.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель сервера (пространства для общения, как в Discord).
 * Collections: хранит список каналов в List.
 */
public class MessengerServer implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int ownerId;
    private String inviteCode;
    private LocalDateTime createdAt;

    // Collections — список каналов этого сервера
    private List<Channel> channels = new ArrayList<>();

    public MessengerServer() {}

    public MessengerServer(int id, String name, int ownerId, String inviteCode) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.inviteCode = inviteCode;
        this.createdAt = LocalDateTime.now();
    }

    public MessengerServer(String name, int ownerId, String inviteCode) {
        this.name = name;
        this.ownerId = ownerId;
        this.inviteCode = inviteCode;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Channel> getChannels() { return channels; }
    public void setChannels(List<Channel> channels) { this.channels = channels; }

    public void addChannel(Channel channel) { this.channels.add(channel); }

    @Override
    public String toString() {
        return "MessengerServer{id=" + id + ", name='" + name + "'}";
    }
}
