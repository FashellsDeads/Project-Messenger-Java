package com.messenger.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class AbstractMessage implements Serializable {

    private int id;
    private int senderId;
    private int chatId;
    private String senderUsername;
    private LocalDateTime timestamp;
    private MessageType type;

    public AbstractMessage(int id, int senderId, int chatId) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.timestamp = LocalDateTime.now();
    }

    public abstract String getDisplayContent();

    public int getId()                   { return id; }
    public int getSenderId()             { return senderId; }
    public int getChatId()               { return chatId; }
    public LocalDateTime getTimestamp()  { return timestamp; }

    public int getChannelId()            { return chatId; }

    public void setId(int id)                        { this.id = id; }
    public void setTimestamp(LocalDateTime timestamp){ this.timestamp = timestamp; }
    public void setType(MessageType type)            { this.type = type; }
    public MessageType getType()                     { return type; }
    public String getSenderUsername()                { return senderUsername; }
    public void setSenderUsername(String name)       { this.senderUsername = name; }
}