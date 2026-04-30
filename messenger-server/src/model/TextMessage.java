package model;

public class TextMessage extends AbstractMessage {

    private String content;

    public TextMessage(int id, int senderId, int chatId, String content) {
        super(id, senderId, chatId);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}