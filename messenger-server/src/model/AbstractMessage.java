package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class AbstractMessage implements Serializable {

    private int id;
    private int senderId;
    private int chatId;
    private LocalDateTime timestamp;

    private MessageType type;


    public AbstractMessage(int id, int senderId, int chatId) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.timestamp = LocalDateTime.now();
        this.type = MessageType.TEXT; // дефолт
    }

    public MessageType getType()             { return type; }
    public void setType(MessageType type)    { this.type = type; }
    public void setId(int id)                { this.id = id; }
    public void setTimestamp(LocalDateTime t){ this.timestamp = t; }
    public String getDisplayContent()        { return ""; }

    public int getId() {
        return id;
    }

    public int getChannelId() { return chatId; }

    public int getSenderId() {
        return senderId;
    }

    public int getChatId() {
        return chatId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}