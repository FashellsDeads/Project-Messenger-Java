package client;

import com.messenger.model.*;

import java.util.List;

public interface NetworkListener {
    void onLoginSuccess(User user);
    void onError(String errorMessage);

    void onMessageReceived(AbstractMessage message);
    void onChannelHistoryReceived(List<AbstractMessage> history);

    void onServersListReceived(List<MessengerServer> servers);
    void onChannelsListReceived(List<Channel> channels);

    void onDisconnected(String reason);

    void onCommandResponse(CommandResponse payload);
}