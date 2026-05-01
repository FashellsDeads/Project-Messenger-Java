package server;

public class Main {
    public static void main(String[] args) {
        ChatServer server = new ChatServer(9092);
        server.start();
    }
}