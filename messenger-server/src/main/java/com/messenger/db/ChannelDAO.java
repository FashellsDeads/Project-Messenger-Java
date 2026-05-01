package com.messenger.db;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с таблицей channels.
 */
public class ChannelDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    // ─── Получить все каналы сервера ─────────────────────────────────────────
    public List<Channel> findByServer(int serverId) {
        String sql = "SELECT * FROM channels WHERE server_id = ? ORDER BY created_at ASC";
        List<Channel> channels = new ArrayList<>();
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, serverId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                channels.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ChannelDAO] Ошибка findByServer: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return channels;
    }

    // ─── Найти канал по ID ────────────────────────────────────────────────────
    public Channel findById(int id) {
        String sql = "SELECT * FROM channels WHERE id = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ChannelDAO] Ошибка findById: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    // ─── Создать канал ────────────────────────────────────────────────────────
    public Channel save(Channel channel) {
        String sql = "INSERT INTO channels (name, server_id) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, channel.getName());
            ps.setInt(2, channel.getServerId());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) channel.setId(keys.getInt(1));
            return channel;
        } catch (SQLException e) {
            System.err.println("[ChannelDAO] Ошибка save: " + e.getMessage());
            return null;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ─── Маппинг ─────────────────────────────────────────────────────────────
    private Channel mapRow(ResultSet rs) throws SQLException {
        Channel channel = new Channel();
        channel.setId(rs.getInt("id"));
        channel.setName(rs.getString("name"));
        channel.setServerId(rs.getInt("server_id"));
        return channel;
    }
}
