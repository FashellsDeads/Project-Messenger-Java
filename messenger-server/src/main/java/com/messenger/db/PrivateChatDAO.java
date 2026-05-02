package com.messenger.db;

import com.messenger.model.PrivateChat;
import com.messenger.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrivateChatDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final UserDAO userDAO = new UserDAO();

    public PrivateChat findById(int id) {
        String sql = "SELECT * FROM private_chats WHERE id = ?";

        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.err.println("[PrivateChatDAO] findById error: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }

        return null;
    }

    public PrivateChat loadFullPrivateChat(int id) {
        String sql = "SELECT * FROM private_chats WHERE id = ?";

        Connection conn = null;

        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            int u1 = rs.getInt("user1_id");
            int u2 = rs.getInt("user2_id");

            User user1 = userDAO.findById(u1);
            User user2 = userDAO.findById(u2);

            return new PrivateChat(id, user1, user2);

        } catch (SQLException e) {
            System.err.println("[PrivateChatDAO] loadFull error: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }

        return null;
    }

    public List<PrivateChat> findByUser(int userId) {
        String sql = """
            SELECT * FROM private_chats
            WHERE user1_id = ? OR user2_id = ?
        """;

        List<PrivateChat> result = new ArrayList<>();
        Connection conn = null;

        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[PrivateChatDAO] findByUser error: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }

        return result;
    }

    public PrivateChat save(int user1Id, int user2Id) {
        String sql = "INSERT INTO private_chats (user1_id, user2_id) VALUES (?, ?)";

        Connection conn = null;

        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                int id = keys.getInt(1);

                User u1 = userDAO.findById(user1Id);
                User u2 = userDAO.findById(user2Id);

                return new PrivateChat(id, u1, u2);
            }

        } catch (SQLException e) {
            System.err.println("[PrivateChatDAO] save error: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }

        return null;
    }

    private PrivateChat mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int u1 = rs.getInt("user1_id");
        int u2 = rs.getInt("user2_id");

        User user1 = userDAO.findById(u1);
        User user2 = userDAO.findById(u2);

        return new PrivateChat(id, user1, user2);
    }
}