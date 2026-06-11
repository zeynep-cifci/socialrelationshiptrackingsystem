/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Kullanıcı
 */
public class OldFriendDAO {

    public ArrayList<User> getOldFriendsOfUser(User user) {
        ArrayList<User> oldFriends = new ArrayList<>();

        String sql = "SELECT DISTINCT u.* FROM old_friends o "
                + "JOIN users u ON "
                + "CASE "
                + "WHEN o.user1_id = ? THEN o.user2_id = u.user_id "
                + "WHEN o.user2_id = ? THEN o.user1_id = u.user_id "
                + "END "
                + "WHERE o.user1_id = ? OR o.user2_id = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());
            ps.setInt(3, user.getId());
            ps.setInt(4, user.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                oldFriends.add(createUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Get old friends error: " + e.getMessage());
        }

        return oldFriends;
    }

    public int getOldFriendCount(User user) {
        String sql = "SELECT COUNT(*) AS total FROM old_friends "
                + "WHERE user1_id = ? OR user2_id = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Old friend count error: " + e.getMessage());
        }

        return 0;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        boolean premium = rs.getBoolean("is_premium");

        if (premium) {
            return new PremiumUser(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("role"),
                    rs.getString("bio"),
                    rs.getBoolean("verified")
            );
        }

        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("role")
        );
    }
}
