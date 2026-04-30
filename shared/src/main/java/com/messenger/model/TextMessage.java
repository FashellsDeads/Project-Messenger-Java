package com.messenger.model;

/**
 * Текстовое сообщение.
 * OOP: Наследование от AbstractMessage.
 * OOP: Полиморфизм — реализует getDisplayContent() по-своему.
 */
public class TextMessage extends AbstractMessage {

    private static final long serialVersionUID = 1L;

    private String text;

    public TextMessage(int channelId, int senderId, String senderUsername, String text) {
        super(channelId, senderId, senderUsername, MessageType.TEXT);
        this.text = text;
    }

    /**
     * Полиморфизм: TextMessage показывает просто текст.
     */
    @Override
    public String getDisplayContent() {
        return text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
