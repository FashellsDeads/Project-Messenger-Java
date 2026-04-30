package managers;

import model.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {

    private final Map<Integer, Chat> chats = new ConcurrentHashMap<>();

    public Chat getChat(int chatId) {
        return chats.get(chatId);
    }

    public void addChat(Chat chat) {
        chats.put(chat.getId(), chat);
    }
}