package client;

import com.messenger.model.*;

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

    void onCommandResponse(CommandResponse payload);

    // События с сервера (user online/offline, chat invite, etc.)
    void onServerEvent(ServerEvent event);
}