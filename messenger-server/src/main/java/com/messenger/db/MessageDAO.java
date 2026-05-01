package com.messenger.db;

import com.messenger.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public AbstractMessage save(AbstractMessage message) {
        String sql = "INSERT INTO messages (channel_id, sender_id, content, type) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message.getChannelId());

            if (message.getSenderId() == 0) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, message.getSenderId());
            }

            ps.setString(3, message.getDisplayContent());
            ps.setString(4, message.getType().name());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                message.setId(keys.getInt(1));
            }
            return message;
        } catch (SQLException e) {
            System.err.println("[MessageDAO] Ошибка save: " + e.getMessage());
            return null;
        } finally {
            db.releaseConnection(conn);
        }
    }

    public List<AbstractMessage> findByChannel(int channelId, int limit) {
        String sql = "SELECT m.*, u.username FROM messages m " +
                     "LEFT JOIN users u ON m.sender_id = u.id " +
                     "WHERE m.channel_id = ? " +
                     "ORDER BY m.created_at DESC LIMIT ?";
        List<AbstractMessage> messages = new ArrayList<>();
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, channelId);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                messages.add(0, mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MessageDAO] Ошибка findByChannel: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }
        return messages;
    }

    private AbstractMessage mapRow(ResultSet rs) throws SQLException {
        int id         = rs.getInt("id");
        int channelId  = rs.getInt("channel_id");
        int senderId   = rs.getInt("sender_id");
        String username = rs.getString("username");
        String content  = rs.getString("content");
        String typeStr  = rs.getString("type");
        Timestamp ts    = rs.getTimestamp("created_at");

        MessageType type = MessageType.valueOf(typeStr);
        AbstractMessage message;

        switch (type) {
            case FILE:
                message = new FileMessage(channelId, senderId,
                        username != null ? username : "System", content, 0);
                break;
            case SYSTEM:
                message = new SystemMessage(channelId,
                        username != null ? username : "System",
                        SystemMessage.EventType.USER_JOINED);
                break;
            default: // TEXT
                message = new TextMessage(channelId, senderId,
                        username != null ? username : "Unknown", content);
                break;
        }

        message.setId(id);
        if (ts != null) {
            message.setTimestamp(ts.toLocalDateTime());
        }
        return message;
    }
}
