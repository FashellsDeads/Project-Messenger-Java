# 💬 JavaFX Messenger

> Десктопный мессенджер в стиле Discord — без голосового чата, только текст и файлы.  
> Учебный проект по курсу **Java Programming** | Final Project Exam

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=flat-square&logo=apachemaven)

---

## Описание

JavaFX Messenger — многопользовательский десктопный мессенджер с поддержкой:
-  Серверов (пространств для общения)
-  Каналов внутри серверов
-  Ролей: Admin, Moderator, Member
-  Передачи файлов
-  Истории сообщений из базы данных
-  Аутентификации с хэшированием паролей

---

## 🏗️ Архитектура

```
┌─────────────────┐        TCP Sockets        ┌──────────────────────┐
│  Client (JavaFX)│ ◄────────────────────────► │   Server (Java)      │
│                 │    Serialized Packets       │                      │
│  - GUI (FXML)   │                             │  - ServerSocket      │
│  - Controllers  │                             │  - ClientHandler     │
│  - NetworkClient│                             │  - ThreadPool        │
└─────────────────┘                             └──────────┬───────────┘
                                                           │ JDBC
                                                           ▼
                                                 ┌──────────────────────┐
                                                 │     MySQL Database   │
                                                 │  users, channels,    │
                                                 │  messages, files...  │
                                                 └──────────────────────┘
```

---

## 🧩 Структура репозитория

```
javafx-messenger/
├── messenger-server/       # Серверная часть
│   └── src/main/java/com/messenger/
│       ├── server/         # ServerApp, ClientHandler, ThreadPoolManager
│       ├── db/             # DAOs, DatabaseManager
│       ├── model/          # (ссылается на shared)
│       └── protocol/       # PacketType, обработка команд
│
├── messenger-client/       # Клиентская часть (JavaFX)
│   └── src/main/java/com/messenger/
│       ├── client/         # ClientApp, NetworkClient
│       ├── controller/     # FXML-контроллеры
│       └── model/          # (ссылается на shared)
│   └── src/main/resources/
│       ├── fxml/           # login.fxml, main.fxml, ...
│       └── css/            # styles.css
│
├── shared/                 # Общие классы (модели + протокол)
│   └── src/main/java/com/messenger/
│       ├── model/          # User, Channel, Message, ...
│       └── protocol/       # Packet, PacketType
│
├── docs/                   # Документация
│   ├── schema.sql          # Схема базы данных
│   ├── architecture.md     # Описание архитектуры
│   └── diagrams/           # UML-диаграммы
│
└── README.md
```

---

## 🚀 Быстрый старт

### Требования

| Инструмент | Версия |
|------------|--------|
| JDK | 17+ |
| JavaFX SDK | 17+ |
| MySQL | 8.0+ |
| Maven | 3.8+ |
| IntelliJ IDEA | 2022+ (рекомендуется) |

### 1. Клонировать репозиторий

```bash
git clone https://github.com/YOUR_USERNAME/javafx-messenger.git
cd javafx-messenger
```

### 2. Настроить базу данных

```bash
# Создать БД и таблицы
mysql -u root -p < docs/schema.sql
```

Создать файл конфигурации (не попадает в Git):

```bash
cp messenger-server/src/main/resources/db.properties.example \
   messenger-server/src/main/resources/db.properties
```

Заполнить `db.properties`:

```properties
db.host=localhost
db.port=3306
db.name=messenger_db
db.user=root
db.password=YOUR_PASSWORD
```

### 3. Запустить сервер

```bash
cd messenger-server
mvn clean package
java -jar target/messenger-server.jar
# Сервер запустится на порту 8080
```

### 4. Запустить клиент

```bash
cd messenger-client
mvn clean package
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/messenger-client.jar
```

---




| Критерий | % | Где реализовано |
|----------|----|-----------------|
| OOP: Классы и объекты | 10% | `shared/model/` — User, Message, Channel и др. |
| OOP: Полиморфизм и наследование | 10% | `AbstractMessage` → TextMessage, FileMessage, SystemMessage |
| OOP: Абстракции и интерфейсы | 5% | `MessageHandler`, `UserRepository`, `Serializable` |
| Collections | 5% | `Map<Integer, ClientHandler>`, `List<Message>`, `Set<String>` |
| Сериализация / File I/O | 10% | Packet по сети + сохранение файлов + конфиг |
| Threads | 10% | ClientHandler thread per user, ExecutorService, Platform.runLater |
| Socket programming | 15% | ServerSocket + ObjectStreams |
| JDBC + MySQL | 15% | DAOs + DatabaseManager + Connection Pool |
| GUI – JavaFX | 20% | 6 экранов, кастомные ячейки, CSS тема |

---
