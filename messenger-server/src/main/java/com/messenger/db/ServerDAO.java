package com.messenger.db;

import model.MessengerServer;
import model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с таблицей messenger_servers и server_members.
 */
public class ServerDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    // ─── Найти сервер по invite-коду ─────────────────────────────────────────
    public MessengerServer findByInviteCode(String inviteCode) {
        String sql = "SELECT * FROM messenger_servers WHERE invite_code = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, inviteCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ServerDAO] Ошибка findByInviteCode: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    // ─── Найти сервер по ID ───────────────────────────────────────────────────
    public MessengerServer findById(int id) {
        String sql = "SELECT * FROM messenger_servers WHERE id = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ServerDAO] Ошибка findById: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    // ─── Получить все серверы пользователя ───────────────────────────────────
    public List<MessengerServer> findByUser(int userId) {
        String sql = "SELECT ms.* FROM messenger_servers ms " +
                     "JOIN server_members sm ON ms.id = sm.server_id " +
                     "WHERE sm.user_id = ?";
        List<MessengerServer> servers = new ArrayList<>();
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                servers.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ServerDAO] Ошибка findByUser: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return servers;
    }

    // ─── Создать сервер ───────────────────────────────────────────────────────
    public MessengerServer save(MessengerServer server) {
        String sql = "INSERT INTO messenger_servers (name, owner_id, invite_code) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, server.getName());
            ps.setInt(2, server.getOwnerId());
            ps.setString(3, server.getInviteCode());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) server.setId(keys.getInt(1));

            // Автоматически добавить создателя как ADMIN
            addMember(server.getId(), server.getOwnerId(), Role.ADMIN);
            return server;
        } catch (SQLException e) {
            System.err.println("[ServerDAO] Ошибка save: " + e.getMessage());
            return null;
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ─── Добавить участника на сервер ────────────────────────────────────────
    public void addMember(int serverId, int userId, Role role) {
        String sql = "INSERT IGNORE INTO server_members (server_id, user_id, role) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, serverId);
            ps.setInt(2, userId);
            ps.setString(3, role.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ServerDAO] Ошибка addMember: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
    }

    // ─── Проверить является ли пользователь участником ───────────────────────
    public boolean isMember(int serverId, int userId) {
        String sql = "SELECT COUNT(*) FROM server_members WHERE server_id = ? AND user_id = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, serverId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[ServerDAO] Ошибка isMember: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }

    // ─── Маппинг ─────────────────────────────────────────────────────────────
    private MessengerServer mapRow(ResultSet rs) throws SQLException {
        MessengerServer server = new MessengerServer();
        server.setId(rs.getInt("id"));
        server.setName(rs.getString("name"));
        server.setOwnerId(rs.getInt("owner_id"));
        server.setInviteCode(rs.getString("invite_code"));
        return server;
    }
}
