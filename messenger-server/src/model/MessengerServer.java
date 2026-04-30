package model;

import javax.management.relation.Role;
import java.util.List;
import java.util.Map;

public class MessengerServer {
    private int id;
    private String name;
    private List<Channel> channels;
    private Map<User, Role> roles;
}