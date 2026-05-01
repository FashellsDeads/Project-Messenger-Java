package client;

import com.messenger.model.AbstractMessage;
import com.messenger.model.Channel;
import com.messenger.model.MessengerServer;
import com.messenger.model.User;

import java.util.List;
import java.util.Scanner;

public class ConsoleTestClient {
    public static void main(String[] args) {
        javafx.application.Platform.startup(() -> {});
        NetworkClient client = new NetworkClient();

        NetworkListener consoleListener = new NetworkListener() {
            @Override
            public void onLoginSuccess(User user) {
                System.out.println("[СЕРВЕР ОТВЕТИЛ] Успешный вход! Твой юзер: " + user.getUsername());
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println("[СЕРВЕР ОТВЕТИЛ] Ошибка: " + errorMessage);
            }

            @Override
            public void onMessageReceived(AbstractMessage message) {
                System.out.println("[СЕРВЕР ОТВЕТИЛ] Новое сообщение: " + message.getDisplayContent());
            }

            @Override
            public void onChannelHistoryReceived(List<AbstractMessage> history) {
                System.out.println("[СЕРВЕР ОТВЕТИЛ] Пришла история канала. Сообщений: " + history.size());
            }

            @Override
            public void onServersListReceived(List<MessengerServer> servers) {
                System.out.println("[СЕРВЕР ОТВЕТИЛ] Пришел список серверов.");
            }

            @Override
            public void onChannelsListReceived(List<Channel> channels) {
                System.out.println("[СЕРВЕР ОТВЕТИЛ] Пришел список каналов.");
            }

            @Override
            public void onDisconnected(String reason) {
                System.out.println("[СЕТЬ] Отключено: " + reason);
            }
        };

        try {
            System.out.println(" Подключаемся к серверу на localhost:9090");
            client.connect("localhost", 9090, consoleListener);
            System.out.println("Подключено! Потоки запущены.");

            Thread.sleep(1000);

            System.out.println("Отправляем тестовый запрос на авторизацию");
            client.login("test_user", "password_hash_123");

            System.out.println("\n[Нажми ENTER в консоли для отключения и выхода]\n");
            new Scanner(System.in).nextLine();

            client.disconnect();
            System.out.println("Тест завершен.");

        } catch (Exception e) {
            System.err.println("Упс, всё сломалось: " + e.getMessage());
            e.printStackTrace();
        }
    }
}