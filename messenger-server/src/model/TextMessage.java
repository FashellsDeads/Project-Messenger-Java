package model;

public class TextMessage extends AbstractMessage {

    private String content;

    public TextMessage(int id, int senderId, int chatId, String content) {
        super(id, senderId, chatId);
        this.content = content;
    }

    public TextMessage(int channelId, int senderId, String username, String content) {
        super(0, senderId, channelId);
        this.content = content;
        setType(MessageType.TEXT);
    }

    @Override
    public String getDisplayContent() { return content; }


    public String getContent() {
        return content;
    }
}