package com.messenger.model;

import java.io.Serializable;

public class CommandResponse implements Serializable {

    private final boolean    success;
    private final String     message;
    private final int        chatId;
    private final Serializable payload;

    public CommandResponse(boolean success, String message, int chatId, Serializable payload) {
        this.success = success;
        this.message = message;
        this.chatId  = chatId;
        this.payload = payload;
    }


    public CommandResponse(boolean success, String message, int chatId) {
        this(success, message, chatId, null);
    }

    public static CommandResponse error(String message) {
        return new CommandResponse(false, message, -1, null);
    }

    public boolean    isSuccess() { return success; }
    public String     getMessage(){ return message; }
    public int        getChatId() { return chatId; }
    public Serializable getPayload() { return payload; }
}