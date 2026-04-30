package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class AbstractMessage implements Serializable {

    private int id;
    private int senderId;
    private int chatId;
    private LocalDateTime timestamp;

    public AbstractMessage(int id, int senderId, int chatId) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.timestamp = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

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