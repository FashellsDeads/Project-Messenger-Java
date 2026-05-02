package com.messenger.db;

import com.messenger.model.Channel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final ChannelMemberDAO channelMemberDAO;
    private final UserDAO userDAO = new UserDAO();

    public ChannelDAO(ChannelMemberDAO channelMemberDAO) {
        this.channelMemberDAO = channelMemberDAO;
    }

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

    public List<Channel> findByUser(int userId) {
        String sql = """
        SELECT c.id, c.name
        FROM channels c
        JOIN channel_members cm ON c.id = cm.channel_id
        WHERE cm.user_id = ?
    """;

        List<Channel> result = new ArrayList<>();
        Connection conn = null;

        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ChannelDAO] findByUser error: " + e.getMessage());
        } finally {
            db.releaseConnection(conn);
        }

        return result;
    }

    public Channel save(Channel channel) {
        String sql = "INSERT INTO channels (name) VALUES (?)";
        Connection conn = null;
        try {
            conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, channel.getName());
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

    public Channel loadFullChannel(int id) {
        Channel channel = findById(id);
        if (channel == null) return null;

        List<Integer> members = channelMemberDAO.getMembers(id);

        for (Integer userId : members) {
            channel.addMember(userDAO.findById(userId));
        }

        return channel;
    }

    private Channel mapRow(ResultSet rs) throws SQLException {
        Channel channel = new Channel();
        channel.setId(rs.getInt("id"));
        channel.setName(rs.getString("name"));
        return channel;
    }
}
