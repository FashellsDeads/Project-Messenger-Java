package managers;

import model.*;
import server.ClientHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private final AuthManager       authManager;
    private final ChatManager       chatManager;
    private final ConnectionManager connectionManager;

    public CommandHandler(AuthManager authManager,
                          ChatManager chatManager,
                          ConnectionManager connectionManager) {
        this.authManager       = authManager;
        this.chatManager       = chatManager;
        this.connectionManager = connectionManager;
    }

    public CommandResponse handle(Command cmd, User currentUser) {
        return switch (cmd.getType()) {
            case CREATE_PRIVATE_CHAT -> createPrivateChat(cmd, currentUser);
            case JOIN_CHANNEL        -> joinChannel(cmd, currentUser);
            case CREATE_CHANNEL      -> createChannel(cmd, currentUser);
            case GET_MY_CHATS        -> getMyChats(currentUser);
            case GET_HISTORY         -> getHistory(cmd, currentUser);
            case GET_ONLINE_USERS    -> getOnlineUsers(currentUser);
        };
    }

    // ── Чаты ─────────────────────────────────────────────────────────────────

    private CommandResponse createPrivateChat(Command cmd, User currentUser) {
        String targetName = cmd.getArg(0);

        if (targetName.equals(currentUser.getUsername()))
            return CommandResponse.error("Cannot create chat with yourself");

        User target = authManager.findByUsername(targetName).orElse(null);
        if (target == null)
            return CommandResponse.error("User not found: " + targetName);

        var existing = chatManager.findPrivateChat(currentUser.getId(), target.getId());
        if (existing.isPresent())
            return new CommandResponse(true, "Chat already exists", existing.get().getId());

        PrivateChat chat = new PrivateChat(IdGenerator.generateId(), currentUser, target);
        chatManager.addChat(chat);

        // Push второму участнику если онлайн
        ClientHandler targetHandler = connectionManager.getHandler(target.getId());
        if (targetHandler != null) {
            targetHandler.sendEvent(new ServerEvent(
                    ServerEventType.PRIVATE_CHAT_INVITE,
                    currentUser.getUsername() + " начал с тобой чат",
                    chat.getId()
            ));
        }

        System.out.println("Private chat: "
                + currentUser.getUsername() + " <-> " + target.getUsername()
                + " [chatId=" + chat.getId() + "]");

        return new CommandResponse(true, "Private chat created", chat.getId());
    }

    private CommandResponse joinChannel(Command cmd, User currentUser) {
        int chatId = Integer.parseInt(cmd.getArg(0));
        Chat chat  = chatManager.getChat(chatId);

        if (!(chat instanceof Channel channel))
            return CommandResponse.error("Channel not found: " + chatId);

        channel.addMember(currentUser);

        // Уведомить остальных участников канала
        for (User member : channel.getParticipants()) {
            if (member.getId() == currentUser.getId()) continue;
            ClientHandler h = connectionManager.getHandler(member.getId());
            if (h != null) {
                h.sendEvent(new ServerEvent(
                        ServerEventType.USER_JOINED_CHANNEL,
                        currentUser.getUsername() + " вступил в канал",
                        chatId
                ));
            }
        }

        System.out.println(currentUser.getUsername() + " joined channel " + chatId);
        return new CommandResponse(true, "Joined channel " + chatId, chatId);
    }

    private CommandResponse createChannel(Command cmd, User currentUser) {
        String name     = cmd.getArg(0);
        Channel channel = new Channel(IdGenerator.generateId(), name);
        channel.addMember(currentUser);
        chatManager.addChat(channel);

        System.out.println("Channel '" + name + "' created by " + currentUser.getUsername());
        return new CommandResponse(true, "Channel created: " + name, channel.getId());
    }

    // ── Запросы данных ────────────────────────────────────────────────────────

    private CommandResponse getMyChats(User currentUser) {
        List<ChatInfo> chatList = chatManager.getUserChats(currentUser.getId());
        return new CommandResponse(true, "OK", -1, (Serializable) chatList);
    }

    private CommandResponse getHistory(Command cmd, User currentUser) {
        int chatId = Integer.parseInt(cmd.getArg(0));
        Chat chat  = chatManager.getChat(chatId);

        if (chat == null)
            return CommandResponse.error("Chat not found: " + chatId);

        boolean isMember = chat.getParticipants().stream()
                .anyMatch(u -> u.getId() == currentUser.getId());
        if (!isMember)
            return CommandResponse.error("Access denied");

        List<AbstractMessage> history = new ArrayList<>(chat.getHistory());
        return new CommandResponse(true, "OK", chatId, (Serializable) history);
    }

    private CommandResponse getOnlineUsers(User currentUser) {
        List<String> names = connectionManager.getOnlineUsers().stream()
                .map(User::getUsername)
                .filter(name -> !name.equals(currentUser.getUsername()))
                .toList();
        return new CommandResponse(true, "OK", -1, (Serializable) names);
    }
}