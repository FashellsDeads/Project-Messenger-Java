package com.messenger.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class DatabaseManager {

    private static DatabaseManager instance;

    private String url;
    private String user;
    private String password;

    private final List<Connection> pool = new ArrayList<>();
    private final List<Connection> usedConnections = new ArrayList<>();
    private int maxConnections;

    private DatabaseManager() {
        loadProperties();
        initPool();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("Файл db.properties не найден!");
            }
            props.load(is);

            String host     = props.getProperty("db.host", "localhost");
            String port     = props.getProperty("db.port", "3306");
            String name     = props.getProperty("db.name", "messenger_db");
            this.user       = props.getProperty("db.user", "root");
            this.password   = props.getProperty("db.password", "");
            this.maxConnections = Integer.parseInt(
                props.getProperty("db.pool.max", "10")
            );

            this.url = "jdbc:mysql://" + host + ":" + port + "/" + name
                     + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки db.properties: " + e.getMessage());
        }
    }

    private void initPool() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            int minConnections = 3;
            for (int i = 0; i < minConnections; i++) {
                pool.add(createConnection());
            }
            System.out.println("[DB] Пул инициализирован: " + minConnections + " соединений");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver не найден! " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка инициализации пула: " + e.getMessage());
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public synchronized Connection getConnection() throws SQLException {
        if (pool.isEmpty()) {
            if (usedConnections.size() < maxConnections) {
                pool.add(createConnection());
            } else {
                throw new SQLException("Пул соединений исчерпан! Максимум: " + maxConnections);
            }
        }

        Connection connection = pool.remove(pool.size() - 1);

        // Проверяем что соединение живое
        if (!connection.isValid(2)) {
            connection = createConnection();
        }

        usedConnections.add(connection);
        return connection;
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            usedConnections.remove(connection);
            pool.add(connection);
        }
    }

    public synchronized void shutdown() {
        for (Connection conn : pool) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
        for (Connection conn : usedConnections) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
        pool.clear();
        usedConnections.clear();
        System.out.println("[DB] Все соединения закрыты");
    }
}
