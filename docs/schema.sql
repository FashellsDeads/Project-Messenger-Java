-- ═══════════════════════════════════════════════════════════════════════════
--  JavaFX Messenger — Database Schema
--  MySQL 8.0+
--  Запуск: mysql -u root -p < docs/schema.sql
-- ═══════════════════════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS messenger_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE messenger_db;

-- ─── 1. Пользователи ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,          -- SHA-256 hex
    status        ENUM('ONLINE', 'OFFLINE', 'AWAY') DEFAULT 'OFFLINE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_seen     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ─── 2. Серверы (пространства общения) ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS messenger_servers (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    owner_id     INT          NOT NULL,
    invite_code  VARCHAR(10)  NOT NULL UNIQUE,    -- уникальный код для вступления
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ─── 3. Участники серверов (с ролями) ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS server_members (
    server_id  INT NOT NULL,
    user_id    INT NOT NULL,
    role       ENUM('ADMIN', 'MODERATOR', 'MEMBER') DEFAULT 'MEMBER',
    joined_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (server_id, user_id),
    FOREIGN KEY (server_id) REFERENCES messenger_servers(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE
);

-- ─── 4. Каналы ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS channels (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    server_id  INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (server_id) REFERENCES messenger_servers(id) ON DELETE CASCADE
);

-- ─── 5. Сообщения ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS messages (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    channel_id INT          NOT NULL,
    sender_id  INT,                               -- NULL для системных сообщений
    content    TEXT,
    type       ENUM('TEXT', 'FILE', 'SYSTEM') DEFAULT 'TEXT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id)  REFERENCES users(id) ON DELETE SET NULL
);

-- ─── 6. Файлы ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS files (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    message_id INT          NOT NULL,
    file_name  VARCHAR(255) NOT NULL,
    file_path  VARCHAR(500) NOT NULL,             -- путь на сервере
    file_size  BIGINT       NOT NULL,             -- в байтах
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
);

-- ─── Индексы для производительности ─────────────────────────────────────────
CREATE INDEX idx_messages_channel ON messages(channel_id, created_at DESC);
CREATE INDEX idx_messages_sender  ON messages(sender_id);
CREATE INDEX idx_members_user     ON server_members(user_id);

-- ─── Тестовые данные (опционально — для разработки) ─────────────────────────
-- Пароль для всех тестовых пользователей: "password123"
-- SHA-256 от "password123" = ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f

INSERT INTO users (username, email, password_hash) VALUES
    ('admin',   'admin@messenger.com',   'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
    ('alice',   'alice@messenger.com',   'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
    ('bob',     'bob@messenger.com',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
    ('charlie', 'charlie@messenger.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f');

INSERT INTO messenger_servers (name, owner_id, invite_code) VALUES
    ('General Server', 1, 'ABC123');

INSERT INTO server_members (server_id, user_id, role) VALUES
    (1, 1, 'ADMIN'),
    (1, 2, 'MEMBER'),
    (1, 3, 'MEMBER'),
    (1, 4, 'MEMBER');

INSERT INTO channels (name, server_id) VALUES
    ('general', 1),
    ('random',  1),
    ('dev',     1);

INSERT INTO messages (channel_id, sender_id, content, type) VALUES
    (1, 1, 'Добро пожаловать в JavaFX Messenger!', 'TEXT'),
    (1, 2, 'Привет всем!', 'TEXT'),
    (1, 3, 'Отличный проект!', 'TEXT');
