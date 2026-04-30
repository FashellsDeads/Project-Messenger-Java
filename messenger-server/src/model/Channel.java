package model;

import java.util.*;

public class Channel implements Chat{

    private int id;
    private String name;
    private Set<User> members = new HashSet<>();
    private List<AbstractMessage> history = new ArrayList<>();

    public Channel(int id, String name) {
        this.id = id;
        this.name = name;
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
        return Collections.unmodifiableList(history);
    }

    @Override
    public Set<User> getParticipants() {
        return Collections.unmodifiableSet(members);
    }


    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);
    }
}