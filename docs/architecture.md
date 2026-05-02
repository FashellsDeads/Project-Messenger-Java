# Архитектура JavaFX Messenger

## Обзор

Проект построен по трёхзвенной клиент-серверной архитектуре:

```
[Client JavaFX] ←──TCP Sockets──→ [Java Server] ←──JDBC──→ [MySQL]
```

## Модули

### shared/
Общие классы, которые используются и клиентом, и сервером.
- **model/** — User, Channel, MessengerServer, AbstractMessage, TextMessage, FileMessage, SystemMessage, Role
- **protocol/** — Packet\<T\>, PacketType (enum)

> Эти классы реализуют `Serializable` — они передаются по сети через ObjectStreams.

### messenger-server/
- **ServerApp** — точка входа, запускает `ServerSocket` на порту 8080
- **ClientHandler** — поток для каждого подключённого клиента, читает/пишет Packet
- **ThreadPoolManager** — `ExecutorService` с пулом потоков
- **BroadcastService** — рассылка сообщений всем подписчикам канала
- **db/** — DatabaseManager, UserDAO, MessageDAO, ChannelDAO, ServerDAO, FileDAO

### messenger-client/
- **ClientApp** — точка входа JavaFX (`extends Application`)
- **NetworkClient** — подключение к серверу, фоновый поток приёма пакетов
- **controller/** — LoginController, MainController, SettingsController и др.
- **resources/fxml/** — login.fxml, main.fxml, ...
- **resources/css/** — styles.css (тёмная тема)

## Протокол

Каждое взаимодействие — это объект `Packet<T>`:
```java
public class Packet<T extends Serializable> implements Serializable {
    private PacketType type;   // тип команды
    private T payload;         // данные
    private long timestamp;    // время
}
```

## Потоки (Threads)

```
Server Main Thread
    └── принимает ServerSocket.accept()
        └── для каждого клиента → new ClientHandler (в ThreadPool)
                ├── читает Packet из ObjectInputStream
                ├── обрабатывает команду
                └── пишет ответ в ObjectOutputStream

Client Main Thread (JavaFX)
    └── запускает NetworkClient
        └── Network Listener Thread (daemon)
                ├── читает Packet от сервера
                └── Platform.runLater() → обновляет UI
```

## Git Flow

| Ветка | Назначение |
|-------|-----------|
| `main` | Стабильный релиз |
| `dev` | Основная разработка |
| `feature/server-core` | Участник 1: сервер |
| `feature/database` | Участник 2: БД |
| `feature/client-network` | Участник 3: сеть клиента |
| `feature/javafx-gui` | Участник 4: GUI |

**Правило:** merge только через Pull Request в `dev`. В `main` — только финальная версия.
