package com.messenger.model;

/**
 * Системное сообщение (пользователь вошёл/вышел/создал канал).
 * OOP: Наследование от AbstractMessage.
 * OOP: Полиморфизм — реализует getDisplayContent() по-своему.
 */
public class SystemMessage extends AbstractMessage {

    private static final long serialVersionUID = 1L;

    public enum EventType {
        USER_JOINED,
        USER_LEFT,
        CHANNEL_CREATED,
        SERVER_CREATED
    }

    private EventType eventType;

    public SystemMessage(int channelId, String senderUsername, EventType eventType) {
        super(channelId, 0, senderUsername, MessageType.SYSTEM);
        this.eventType = eventType;
    }

    /**
     * Полиморфизм: SystemMessage показывает системное событие.
     */
    @Override
    public String getDisplayContent() {
        switch (eventType) {
            case USER_JOINED:  return "🟢 " + getSenderUsername() + " присоединился к каналу";
            case USER_LEFT:    return "🔴 " + getSenderUsername() + " покинул канал";
            case CHANNEL_CREATED: return "📢 Канал создан";
            case SERVER_CREATED:  return "🏠 Сервер создан";
            default: return "Системное событие";
        }
    }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
}
