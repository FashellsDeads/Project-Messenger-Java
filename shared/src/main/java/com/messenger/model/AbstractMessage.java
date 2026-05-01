package com.messenger.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class AbstractMessage implements Serializable {

    private int id;
    private int senderId;
    private int chatId;           // твоё поле — оставляем
    private String senderUsername; // новое — нужно DAO
    private LocalDateTime timestamp;
    private MessageType type;     // новое — нужно DAO

    public AbstractMessage(int id, int senderId, int chatId) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.timestamp = LocalDateTime.now();
    }

    // Абстрактный метод — каждый наследник реализует
    public abstract String getDisplayContent();

    // Старые геттеры — оставляем как есть
    public int getId()                   { return id; }
    public int getSenderId()             { return senderId; }
    public int getChatId()               { return chatId; }
    public LocalDateTime getTimestamp()  { return timestamp; }

    // Алиас для совместимости с DAO (они используют getChannelId)
    public int getChannelId()            { return chatId; }

    // Новые сеттеры для DAO
    public void setId(int id)                        { this.id = id; }
    public void setTimestamp(LocalDateTime timestamp){ this.timestamp = timestamp; }
    public void setType(MessageType type)            { this.type = type; }
    public MessageType getType()                     { return type; }
    public String getSenderUsername()                { return senderUsername; }
    public void setSenderUsername(String name)       { this.senderUsername = name; }
}