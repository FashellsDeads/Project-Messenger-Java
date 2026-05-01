package com.messenger.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Абстрактный класс сообщения.
 * OOP: Абстракция — нельзя создать напрямую, только через наследников.
 * OOP: Наследование — TextMessage, FileMessage, SystemMessage расширяют этот класс.
 */
public abstract class AbstractMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int channelId;
    private int senderId;
    private String senderUsername;
    private LocalDateTime timestamp;
    private MessageType type;

    // ─── Конструктор ─────────────────────────────────────────────────────────

    public AbstractMessage(int channelId, int senderId, String senderUsername, MessageType type) {
        this.channelId = channelId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Абстрактный метод — каждый наследник возвращает текст для отображения в UI.
     * OOP: Полиморфизм — разное поведение у разных типов сообщений.
     */
    public abstract String getDisplayContent();

    // ─── Геттеры и сеттеры ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getChannelId() { return channelId; }
    public void setChannelId(int channelId) { this.channelId = channelId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
}
