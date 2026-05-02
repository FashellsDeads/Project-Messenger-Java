package client;

import com.messenger.model.*;
import com.messenger.protocol.*;

import java.util.List;
import java.util.Scanner;

public class TestClient implements NetworkListener {

    private static volatile boolean running = true;
    private final NetworkClient client = new NetworkClient();

    public static void main(String[] args) {
        new TestClient().start();
    }

    public void start() {
        try {
            client.connect("localhost", 9092, this, new DirectDispatcher());
        } catch (Exception e) {
            System.out.println("[Ошибка подключения]: " + e.getMessage());
            return;
        }

        printHelp();
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(" ");
            String cmd = parts[0].toLowerCase();

            switch (cmd) {
                case "register" -> {
                    if (parts.length < 4) {
                        printUsage("register <username> <email> <password>");
                        break;
                    }

                    client.sendPacket(new Packet<>(
                            PacketType.REGISTER_REQUEST,
                            new RegisterRequest(parts[1], parts[2], parts[3])
                    ));
                }

                case "login" -> {
                    if (parts.length < 3) {
                        printUsage("login <username> <password>");
                        break;
                    }

                    client.login(parts[1], parts[2]);
                }

                case "chats" -> client.sendPacket(new Packet<>(
                        PacketType.COMMAND,
                        new Command(CommandType.GET_MY_CHATS)
                ));

                case "history" -> {
                    if (parts.length < 2) {
                        printUsage("history <chatId>");
                        break;
                    }

                    client.sendPacket(new Packet<>(
                            PacketType.COMMAND,
                            new Command(CommandType.GET_HISTORY, parts[1])
                    ));
                }

                case "online" -> client.sendPacket(new Packet<>(
                        PacketType.COMMAND,
                        new Command(CommandType.GET_ONLINE_USERS)
                ));

                case "msg" -> {
                    if (parts.length < 3) {
                        printUsage("msg <chatId> <text>");
                        break;
                    }

                    int chatId = Integer.parseInt(parts[1]);
                    String text = line.substring(line.indexOf(parts[2]));

                    client.sendMessage(
                            new TextMessage(IdGenerator.generateId(), -1, chatId, text)
                    );
                }

                case "private" -> {
                    if (parts.length < 2) {
                        printUsage("private <username>");
                        break;
                    }

                    client.sendPacket(new Packet<>(
                            PacketType.COMMAND,
                            new Command(CommandType.CREATE_PRIVATE_CHAT, parts[1])
                    ));
                }

                case "channel" -> {
                    if (parts.length < 2) {
                        printUsage("channel <name>");
                        break;
                    }

                    client.sendPacket(new Packet<>(
                            PacketType.COMMAND,
                            new Command(CommandType.CREATE_CHANNEL, parts[1])
                    ));
                }

                case "join" -> {
                    if (parts.length < 2) {
                        printUsage("join <chatId>");
                        break;
                    }

                    client.sendPacket(new Packet<>(
                            PacketType.COMMAND,
                            new Command(CommandType.JOIN_CHANNEL, parts[1])
                    ));
                }

                case "help" -> printHelp();

                case "exit", "quit" -> {
                    running = false;
                    client.disconnect();
                    System.out.println("Отключаемся...");
                }

                default -> System.out.println("Неизвестная команда. Введи help.");
            }
        }
    }

    @Override
    public void onLoginSuccess(User user) {
        System.out.println("\n✅ Login: " + user.getUsername());
        prompt();
    }

    @Override
    public void onError(String errorMessage) {
        System.out.println("\n❌ Error: " + errorMessage);
        prompt();
    }

    @Override
    public void onMessageReceived(AbstractMessage message) {
        System.out.println("\n[MSG] chatId=" + message.getChatId()
                + " | " + message.getDisplayContent());
        prompt();
    }

    @Override
    public void onChannelHistoryReceived(List<AbstractMessage> history) {
        System.out.println("\n[History]");
        history.forEach(m -> System.out.println(" • " + m.getDisplayContent()));
        prompt();
    }

    @Override
    public void onServersListReceived(List<MessengerServer> servers) {
        System.out.println("\nServers: " + servers.size());
        prompt();
    }

    @Override
    public void onChannelsListReceived(List<Channel> channels) {
        System.out.println("\nChannels: " + channels.size());
        prompt();
    }

    @Override
    public void onDisconnected(String reason) {
        System.out.println("\n[Disconnected]: " + reason);
    }

    @Override
    public void onCommandResponse(CommandResponse response) {
        System.out.println("\n[CMD] " +
                (response.isSuccess() ? "✅ " : "❌ ") +
                response.getMessage());

        if (response.getChatId() != -1) {
            System.out.println("Chat created! ID = " + response.getChatId());
        }

        if (response.getPayload() instanceof List<?> list) {
            list.forEach(item -> System.out.println(" • " + item));
        }

        prompt();
    }

    @Override
    public void onServerEvent(ServerEvent event) {
        System.out.println("[EVENT] " + event.getType() + ": " + event.getMessage());
        prompt();
    }

    private void prompt() {
        System.out.print("> ");
    }

    private static void printHelp() {
        System.out.println("""
            ┌─────────────────────────────────────────────────┐
            │              Discord Test Client                │
            ├─────────────────────────────────────────────────┤
            │  register <username> <email> <password>         │
            │  login    <username> <password>                 │
            │  private  <username>   — создать личный чат     │
            │  channel  <name>       — создать канал          │
            │  join     <chatId>     — вступить в канал       │
            │  msg      <chatId> <текст>  — отправить         │
            │  help                  — это меню               │
            │  exit                  — выйти                  │
            └─────────────────────────────────────────────────┘
            """);
    }

    private static void printUsage(String usage) {
        System.out.println("Использование: " + usage);
    }
}