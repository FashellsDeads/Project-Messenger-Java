package managers;

import com.messenger.db.ChannelDAO;
import com.messenger.db.MessageDAO;
import com.messenger.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {

    private final Map<Integer, Chat> chats = new ConcurrentHashMap<>();
    private final ChannelDAO channelDAO;
    private final MessageDAO messageDAO;

    public ChatManager(ChannelDAO channelDAO, MessageDAO messageDAO) {
        this.channelDAO = channelDAO;
        this.messageDAO = messageDAO;
    }

    public Chat getChat(int chatId) {
        return chats.get(chatId);
    }

    public void addChat(Chat chat) {
        chats.put(chat.getId(), chat);
    }

    // Создать канал и сразу сохранить в БД
    public Channel createAndSaveChannel(String name, int serverId, User creator) {
        Channel channel = new Channel();
        channel.setName(name);
        channel.setServerId(serverId);

        Channel saved = channelDAO.save(channel); // id приходит из БД
        if (saved == null) return null;

        saved.addMember(creator);
        chats.put(saved.getId(), saved);
        return saved;
    }

    // История: сначала смотрим в памяти, если пусто — идём в БД
    public List<AbstractMessage> getHistory(int chatId) {
        Chat chat = chats.get(chatId);
        if (chat != null && !chat.getHistory().isEmpty()) {
            return new ArrayList<>(chat.getHistory());
        }
        // Загружаем из БД (последние 50 сообщений)
        return messageDAO.findByChannel(chatId, 50);
    }

    public Optional<PrivateChat> findPrivateChat(int userId1, int userId2) {
        return chats.values().stream()
                .filter(c -> c instanceof PrivateChat)
                .map(c -> (PrivateChat) c)
                .filter(pc -> pc.hasUser(userId1) && pc.hasUser(userId2))
                .findFirst();
    }

    public List<ChatInfo> getUserChats(int userId) {
        List<ChatInfo> result = new ArrayList<>();
        for (Chat chat : chats.values()) {
            switch (chat) {
                case SelfChat sc -> {
                    if (sc.getParticipants().stream().anyMatch(u -> u.getId() == userId))
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
                    if (ch.getParticipants().stream().anyMatch(u -> u.getId() == userId))
                        result.add(new ChatInfo(ch.getId(), "CHANNEL", ch.getName()));
                }
                default -> {}
            }
        }
        return result;
    }
}