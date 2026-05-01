package model;

public class TextMessage extends AbstractMessage {

    private String content;

    // Старый конструктор — оставляем, сервер им пользуется
    public TextMessage(int id, int senderId, int chatId, String content) {
        super(id, senderId, chatId);
        this.content = content;
        setType(MessageType.TEXT);
    }

    // Новый конструктор — для DAO при чтении из БД
    public TextMessage(int channelId, int senderId, String senderUsername, String content) {
        super(0, senderId, channelId);
        this.content = content;
        setSenderUsername(senderUsername);
        setType(MessageType.TEXT);
    }

    public String getContent()  { return content; }

    @Override
    public String getDisplayContent() { return content; }
}