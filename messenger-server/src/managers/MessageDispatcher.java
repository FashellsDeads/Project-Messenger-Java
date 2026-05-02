package managers;

import com.messenger.db.MessageDAO;
import com.messenger.model.AbstractMessage;
import com.messenger.model.Chat;
import com.messenger.model.User;
import server.ClientHandler;

public class MessageDispatcher {

    private final ChatManager chatManager;
    private final ConnectionManager connectionManager;
    private final MessageDAO messageDAO;

    public MessageDispatcher(ChatManager chatManager,
                             ConnectionManager connectionManager,
                             MessageDAO messageDAO) {
        this.chatManager       = chatManager;
        this.connectionManager = connectionManager;
        this.messageDAO        = messageDAO;
    }

    public void dispatch(AbstractMessage msg) {
        Chat chat = chatManager.getChat(msg.getChatId());
        if (chat == null) {
            System.out.println("❌ Чат не найден: " + msg.getChatId());
            return;
        }

        // Сохраняем в БД перед рассылкой
        messageDAO.save(msg);

        // Сохраняем в памяти
        chat.sendMessage(msg);

        // Рассылаем всем участникам онлайн
        for (User user : chat.getParticipants()) {
            ClientHandler handler = connectionManager.getHandler(user.getId());
            if (handler != null) {
                handler.sendMessage(msg);
            }
        }
    }
}