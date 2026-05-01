package managers;

import model.*;
import server.ClientHandler;

public class MessageDispatcher {

    private final ChatManager chatManager;
    private final ConnectionManager connectionManager;

    public MessageDispatcher(ChatManager chatManager, ConnectionManager connectionManager) {
        this.chatManager = chatManager;
        this.connectionManager = connectionManager;
    }

    public void dispatch(AbstractMessage msg) {
        System.out.println("📩 Dispatch вызван");

        Chat chat = chatManager.getChat(msg.getChatId());

        if (chat == null) {
            System.out.println("❌ Чат не найден: " + msg.getChatId());
            return;
        }

        System.out.println("✅ Чат найден: " + chat.getId());

        chat.sendMessage(msg);

        System.out.println("👥 Участников: " + chat.getParticipants().size());

        for (User user : chat.getParticipants()) {
            ClientHandler handler = connectionManager.getHandler(user.getId());

            if (handler != null) {
                System.out.println("➡️ Отправляем пользователю: " + user.getId());
                handler.sendMessage(msg);
            } else {
                System.out.println("⚠️ Пользователь оффлайн: " + user.getId());
            }
        }
    }
}