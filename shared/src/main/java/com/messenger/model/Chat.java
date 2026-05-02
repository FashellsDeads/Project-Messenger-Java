package com.messenger.model;

import java.util.List;
import java.util.Set;

public interface Chat {
    int getId();
    void sendMessage(AbstractMessage msg);
    List<AbstractMessage> getHistory();
    Set<User> getParticipants();
}