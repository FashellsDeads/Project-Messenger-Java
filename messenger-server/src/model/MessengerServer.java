package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessengerServer implements Serializable {

    private int id;
    private String name;
    private int ownerId;
    private String inviteCode;
    private List<Channel> channels = new ArrayList<>();

    public MessengerServer() {}

    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public int getOwnerId()                  { return ownerId; }
    public void setOwnerId(int ownerId)      { this.ownerId = ownerId; }

    public String getInviteCode()            { return inviteCode; }
    public void setInviteCode(String code)   { this.inviteCode = code; }

    public List<Channel> getChannels()       { return channels; }
}