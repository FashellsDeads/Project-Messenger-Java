package com.messenger.db;

import java.sql.*;

public class FileDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    /**
     * Сохранить информацию о файле.
     * @param messageId  ID сообщения к которому прикреплён файл
     * @param fileName   оригинальное имя файла
     * @param filePath   путь на сервере где хранится файл
     * @param fileSize   размер в байтах
     * @return ID записи в БД или -1 при ошибке
     */
    public int save(int messageId, String fileName, String filePath, long fileSize) {
        String sql = "INSERT INTO files (message_id, file_name, file_path, file_size) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, messageId);
            ps.setString(2, fileName);
            ps.setString(3, filePath);
            ps.setLong(4, fileSize);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[FileDAO] Ошибка save: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return -1;
    }

    public String getFilePath(int messageId) {
        String sql = "SELECT file_path FROM files WHERE message_id = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("file_path");
        } catch (SQLException e) {
            System.err.println("[FileDAO] Ошибка getFilePath: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }
}
