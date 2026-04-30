package test;

import model.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 9090);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream());

            // 1. Регистрация
            System.out.println("Регистрируемся...");
            out.writeObject(new AuthRequest("rina", "1234", true));
            out.flush();

            AuthResponse regResponse = (AuthResponse) in.readObject();
            System.out.println("Регистрация: " + regResponse.message);

            if (!regResponse.success) {
                // уже зарегана — просто логинимся
                System.out.println("Логинимся...");
                out.writeObject(new AuthRequest("rina", "1234", false));
                out.flush();

                AuthResponse loginResponse = (AuthResponse) in.readObject();
                System.out.println("Логин: " + loginResponse.message);

                if (!loginResponse.success) {
                    System.out.println("Не удалось войти, выход");
                    socket.close();
                    return;
                }
            }

            // 2. Отправляем сообщение себе (self chat)
            // предполагаем chatId == userId == 1 для теста
            int userId = -1;
            TextMessage msg = new TextMessage(userId, userId, userId, "Привет себе!");
            out.writeObject(msg);
            out.flush();
            System.out.println("Сообщение в self-chat отправлено");

            Thread.sleep(2000);
            System.out.println("Клиент завершил работу");

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}