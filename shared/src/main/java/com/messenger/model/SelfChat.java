package com.messenger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelfChat implements Chat {

    private final int id;
    private final User owner;

    private final List<AbstractMessage> history = new ArrayList<>();

    public SelfChat(int id, User owner) {
        this.id = id;
        this.owner = owner;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void sendMessage(AbstractMessage msg) {
        history.add(msg);
    }

    @Override
    public List<AbstractMessage> getHistory() {
        return history;
    }

    @Override
    public Set<User> getParticipants() {
        return Set.of(owner);
    }
}