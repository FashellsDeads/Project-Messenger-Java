package test;

import managers.ConnectionManager;
import managers.IdGenerator;
import model.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class TestClient {

    private static ObjectOutputStream out;
    private static volatile boolean running = true;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9090)) {

            out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Отдельный поток — слушает сервер и печатает ответы
            Thread receiver = new Thread(() -> {
                try {
                    while (running) {
                        Object obj = in.readObject();
                        printReceived(obj);
                    }
                } catch (EOFException ignored) {
                    System.out.println("\n[Сервер закрыл соединение]");
                } catch (Exception e) {
                    if (running) System.out.println("[Ошибка получения]: " + e.getMessage());
                }
            });
            receiver.setDaemon(true);
            receiver.start();

            // Главный поток — читает команды из консоли
            printHelp();
            Scanner scanner = new Scanner(System.in);

            while (running) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;

                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ", 3);
                String cmd = parts[0].toLowerCase();

                switch (cmd) {
                    case "register" -> {
                        // register <username> <email> <password>
                        if (parts.length < 3) { printUsage("register <username> <email> <password>"); break; }
                        String[] sub = line.split(" ", 4);
                        if (sub.length < 4) { printUsage("register <username> <email> <password>"); break; }
                        send(new AuthRequest(sub[1], sub[2], sub[3], true));
                    }
                    case "login" -> {
                        // login <username> <password>
                        if (parts.length < 3) { printUsage("login <username> <password>"); break; }
                        send(new AuthRequest(parts[1], null, parts[2], false));
                    }
                    case "chats" -> send(new Command(CommandType.GET_MY_CHATS));
                    case "history" -> {
                        if (parts.length < 2) { printUsage("history <chatId>"); break; }
                        send(new Command(CommandType.GET_HISTORY, parts[1]));
                    }
                    case "online" -> send(new Command(CommandType.GET_ONLINE_USERS));
                    case "msg" -> {
                        // msg <chatId> <text...>
                        if (parts.length < 3) { printUsage("msg <chatId> <text>"); break; }
                        int chatId = Integer.parseInt(parts[1]);
                        send(new TextMessage(IdGenerator.generateId(), -1, chatId, parts[2]));
                    }
                    case "private" -> {
                        // private <username>
                        if (parts.length < 2) { printUsage("private <username>"); break; }
                        send(new Command(CommandType.CREATE_PRIVATE_CHAT, parts[1]));
                    }
                    case "channel" -> {
                        // channel <name>
                        if (parts.length < 2) { printUsage("channel <name>"); break; }
                        send(new Command(CommandType.CREATE_CHANNEL, parts[1]));
                    }
                    case "join" -> {
                        // join <chatId>
                        if (parts.length < 2) { printUsage("join <chatId>"); break; }
                        send(new Command(CommandType.JOIN_CHANNEL, parts[1]));
                    }
                    case "help" -> printHelp();
                    case "exit", "quit" -> {
                        running = false;
                        System.out.println("Отключаемся...");
                    }
                    default -> System.out.println("Неизвестная команда. Введи help.");
                }
            }

        } catch (Exception e) {
            System.out.println("[Ошибка подключения]: " + e.getMessage());
        }
    }

    private static void send(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.out.println("[Ошибка отправки]: " + e.getMessage());
        }
    }

    private static void printReceived(Object obj) {
        String text = switch (obj) {
            case AuthResponse r     -> "[Auth] " + (r.success ? "✅ " : "❌ ") + r.message;
            case CommandResponse r -> {
                StringBuilder sb = new StringBuilder();
                sb.append("[Cmd] ").append(r.isSuccess() ? "✅ " : "❌ ").append(r.getMessage());
                if (r.getChatId() != -1) sb.append("  (chatId=").append(r.getChatId()).append(")");
                if (r.getPayload() instanceof List<?> list && !list.isEmpty()) {
                    list.forEach(item -> sb.append("\n  • ").append(item));
                }
                yield sb.toString();
            }
            case ServerEvent ev -> "[" + ev.getType() + "] " + ev.getMessage()
                    + (ev.getRelatedId() != -1 ? " (id=" + ev.getRelatedId() + ")" : "");
            case TextMessage m      -> "[Сообщение] chatId=" + m.getChatId()
                    + " Sender is: " + m.getSenderId()
                    + " | " + m.getContent();
            default                 -> "[?] " + obj.toString();
        };

        // Печатаем поверх строки "> " чтобы не мешало вводу
        System.out.println("\n" + text);
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