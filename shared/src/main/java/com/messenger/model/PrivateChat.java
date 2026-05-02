package com.messenger.model;

import java.util.*;

public class PrivateChat implements Chat {

    private final int id;
    private final User user1;
    private final User user2;
    private final List<AbstractMessage> history = new ArrayList<>();

    public PrivateChat(int id, User user1, User user2) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }
    @Override
    public String getDisplayName() {
        User other = user2;
        return "💬 " + other.getUsername();
    }

    @Override
    public int getId() { return id; }

    @Override
    public void sendMessage(AbstractMessage msg) {
        history.add(msg);
    }

    @Override
    public List<AbstractMessage> getHistory() {
        return Collections.unmodifiableList(history);
    }

    @Override
    public Set<User> getParticipants() {
        return Set.of(user1, user2);
    }

    public boolean hasUser(int userId) {
        return user1.getId() == userId || user2.getId() == userId;
    }
}