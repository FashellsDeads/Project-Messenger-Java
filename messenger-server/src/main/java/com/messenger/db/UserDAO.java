package com.messenger.db;

import com.messenger.model.Role;
import com.messenger.model.User;
import com.messenger.model.UserStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Ошибка findByEmail: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
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
            System.err.println("[UserDAO] Ошибка findById: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Ошибка findByUsername: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return null;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }
            System.out.println("[UserDAO] Пользователь сохранён: " + user.getUsername());
            return user;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Ошибка save: " + e.getMessage());
            return null;
        } finally {
            db.releaseConnection(conn);
        }
    }

    public void updateStatus(int userId, UserStatus status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] Ошибка updateStatus: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
    }

    public List<User> findByServer(int serverId) {
        String sql = "SELECT u.*, sm.role FROM users u " +
                     "JOIN server_members sm ON u.id = sm.user_id " +
                     "WHERE sm.server_id = ?";
        List<User> users = new ArrayList<>();
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, serverId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = mapRow(rs);
                try {
                    user.setRole(Role.valueOf(rs.getString("role")));
                } catch (Exception ignored) {}
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Ошибка findByServer: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return users;
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Ошибка existsByEmail: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Ошибка existsByUsername: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return false;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        try {
            user.setStatus(UserStatus.valueOf(rs.getString("status")));
        } catch (Exception e) {
            user.setStatus(UserStatus.OFFLINE);
        }
        return user;
    }
}