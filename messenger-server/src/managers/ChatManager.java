package managers;

import model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {

    private final Map<Integer, Chat> chats = new ConcurrentHashMap<>();

    public Chat getChat(int chatId) {
        return chats.get(chatId);
    }

    public void addChat(Chat chat) {
        chats.put(chat.getId(), chat);
    }

    public Optional<PrivateChat> findPrivateChat(int userId1, int userId2) {
        return chats.values().stream()
                .filter(c -> c instanceof PrivateChat)
                .map(c -> (PrivateChat) c)
                .filter(pc -> pc.hasUser(userId1) && pc.hasUser(userId2))
                .findFirst();
    }

    // Все чаты где участвует пользователь — для GET_MY_CHATS
    public List<ChatInfo> getUserChats(int userId) {
        List<ChatInfo> result = new ArrayList<>();

        for (Chat chat : chats.values()) {
            switch (chat) {
                case SelfChat sc -> {
                    boolean isOwner = sc.getParticipants().stream()
                            .anyMatch(u -> u.getId() == userId);
                    if (isOwner)
                        result.add(new ChatInfo(sc.getId(), "SELF", "Избранное"));
                }
                case PrivateChat pc -> {
                    if (pc.hasUser(userId)) {
                        String otherName = pc.getParticipants().stream()
                                .filter(u -> u.getId() != userId)
                                .map(User::getUsername)
                                .findFirst().orElse("Unknown");
                        result.add(new ChatInfo(pc.getId(), "PRIVATE", otherName));
                    }
                }
                case Channel ch -> {
                    boolean isMember = ch.getParticipants().stream()
                            .anyMatch(u -> u.getId() == userId);
                    if (isMember)
                        result.add(new ChatInfo(ch.getId(), "CHANNEL", ch.getName()));
                }
                default -> {}
            }
        }
        return result;
    }
}