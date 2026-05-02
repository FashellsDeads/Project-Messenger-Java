package com.messenger.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChannelMemberDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public void addMember(int channelId, int userId) {
        String sql = "INSERT INTO channel_members (channel_id, user_id) VALUES (?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, channelId);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[ChannelMemberDAO] addMember error: " + e.getMessage());
        }
    }

    public void removeMember(int channelId, int userId) {
        String sql = "DELETE FROM channel_members WHERE channel_id = ? AND user_id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, channelId);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[ChannelMemberDAO] removeMember error: " + e.getMessage());
        }
    }

    public List<Integer> getMembers(int channelId) {
        String sql = "SELECT user_id FROM channel_members WHERE channel_id = ?";
        List<Integer> users = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, channelId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("[ChannelMemberDAO] getMembers error: " + e.getMessage());
        }

        return users;
    }
}