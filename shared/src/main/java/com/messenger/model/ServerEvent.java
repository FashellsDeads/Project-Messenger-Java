package com.messenger.model;

import java.io.Serializable;

public class ServerEvent implements Serializable {

    private final ServerEventType type;
    private final String message;
    private final int relatedId;

    public ServerEvent(ServerEventType type, String message, int relatedId) {
        this.type      = type;
        this.message   = message;
        this.relatedId = relatedId;
    }

    public ServerEventType getType()  { return type; }
    public String getMessage()        { return message; }
    public int getRelatedId()         { return relatedId; }
}