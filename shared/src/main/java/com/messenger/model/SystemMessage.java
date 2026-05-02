package com.messenger.model;

public class SystemMessage extends AbstractMessage {

    public enum EventType { USER_JOINED, USER_LEFT, CHANNEL_CREATED }

    private final EventType eventType;

    public SystemMessage(int channelId, String actorName, EventType eventType) {
        super(0, 0, channelId); // senderId=0 означает система
        this.eventType = eventType;
        setType(MessageType.SYSTEM);
    }

    public EventType getEventType() { return eventType; }

    @Override
    public String getDisplayContent() { return eventType.name(); }
}