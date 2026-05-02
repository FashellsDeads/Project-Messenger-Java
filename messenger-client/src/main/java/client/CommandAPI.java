package client;

import com.messenger.model.Command;
import com.messenger.model.CommandType;
import com.messenger.protocol.Packet;
import com.messenger.protocol.PacketType;

public class CommandAPI {

    private final NetworkClient client;

    public CommandAPI(NetworkClient client) {
        this.client = client;
    }
    public void createPrivateChat(String username) {
        send(PacketType.COMMAND,
                new Command(CommandType.CREATE_PRIVATE_CHAT, username));
    }

    public void joinChannel(int chatId) {
        send(PacketType.COMMAND,
                new Command(CommandType.JOIN_CHANNEL, String.valueOf(chatId)));
    }

    public void createChannel(String name) {
        send(PacketType.COMMAND,
                new Command(CommandType.CREATE_CHANNEL, name));
    }

    public void getMyChats() {
        send(PacketType.COMMAND,
                new Command(CommandType.GET_MY_CHATS));
    }

    public void getHistory(int chatId) {
        send(PacketType.COMMAND,
                new Command(CommandType.GET_HISTORY, String.valueOf(chatId)));
    }

    public void getOnlineUsers() {
        send(PacketType.COMMAND,
                new Command(CommandType.GET_ONLINE_USERS));
    }

    private void send(PacketType type, Command cmd) {
        client.sendPacket(new Packet<>(type, cmd));
    }
}