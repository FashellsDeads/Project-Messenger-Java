package client;

import com.messenger.model.AbstractMessage;
import com.messenger.model.Channel;
import com.messenger.model.MessengerServer;
import com.messenger.model.User;

import java.util.List;

public interface NetworkListener {
    // Авторизация
    void onLoginSuccess(User user);
    void onError(String errorMessage);

    // Сообщения
    void onMessageReceived(AbstractMessage message);
    void onChannelHistoryReceived(List<AbstractMessage> history);

    // Списки
    void onServersListReceived(List<MessengerServer> servers);
    void onChannelsListReceived(List<Channel> channels);

    // Системное
    void onDisconnected(String reason);
}