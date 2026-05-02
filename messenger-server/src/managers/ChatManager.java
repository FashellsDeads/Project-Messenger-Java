package managers;

import com.messenger.db.ChannelDAO;
import com.messenger.db.ChannelMemberDAO;
import com.messenger.db.MessageDAO;
import com.messenger.db.PrivateChatDAO;
import com.messenger.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {

    private final Map<Integer, Chat> chats = new ConcurrentHashMap<>();
    private final ChannelDAO channelDAO;
    private final MessageDAO messageDAO;
    private final ChannelMemberDAO channelMemberDAO;
    private final PrivateChatDAO privateChatDAO;

    public ChatManager(ChannelDAO channelDAO,
                       MessageDAO messageDAO,
                       ChannelMemberDAO channelMemberDAO, PrivateChatDAO privateChatDAO) {
        this.channelDAO = channelDAO;
        this.messageDAO = messageDAO;
        this.channelMemberDAO = channelMemberDAO;
        this.privateChatDAO = privateChatDAO;
    }

    public Chat getChat(int chatId) {
        Chat chat = chats.get(chatId);

        if (chat != null) return chat;

        // пробуем private chat
        PrivateChat pc = privateChatDAO.findById(chatId);
        if (pc != null) {
            chats.put(chatId, pc);
            return pc;
        }

        // channels
        Channel ch = channelDAO.loadFullChannel(chatId);
        if (ch != null) {
            chats.put(chatId, ch);
            return ch;
        }

        return null;
    }

    public PrivateChat createAndSavePrivateChat(int user1Id, int user2Id) {
        return privateChatDAO.save(user1Id, user2Id);
    }

    public void addChat(Chat chat) {
        chats.put(chat.getId(), chat);
    }

    // Создать канал и сразу сохранить в БД
    public Channel createAndSaveChannel(String name, User creator) {
        Channel channel = new Channel();
        channel.setName(name);

        Channel saved = channelDAO.save(channel);
        if (saved == null) return null;

        // сохраняем в БД участников
        channelMemberDAO.addMember(saved.getId(), creator.getId());

        // в память
        saved.addMember(creator);
        chats.put(saved.getId(), saved);

        return saved;
    }

    // История: сначала смотрим в памяти, если пусто — идём в БД
    public List<AbstractMessage> getHistory(int chatId) {
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

        // 🔥 сначала каналы из БД — грузим полностью (с участниками!)
        List<Channel> channels = channelDAO.findByUser(userId);

        for (Channel ch : channels) {
            result.add(new ChatInfo(ch.getId(), "CHANNEL", ch.getName()));
            // loadFullChannel загружает и членов канала, иначе getParticipants() вернёт пустой Set
            Channel full = channelDAO.loadFullChannel(ch.getId());
            chats.putIfAbsent(ch.getId(), full != null ? full : ch);
        }

        // 🔥 теперь ПРИВАТНЫЕ ЧАТЫ — ВАЖНО: не из chats, а из БД или DAO
        List<PrivateChat> privateChats = privateChatDAO.findByUser(userId);

        for (PrivateChat pc : privateChats) {
            User other = pc.getOtherUser(userId);

            result.add(new ChatInfo(
                    pc.getId(),
                    "PRIVATE",
                    other.getUsername()
            ));

            chats.putIfAbsent(pc.getId(), pc);
        }

        return result;
    }
}