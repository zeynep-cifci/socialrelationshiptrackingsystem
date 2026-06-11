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

public class FriendshipDAO {

    public ArrayList<User> getFriendsOfUser(User user) {
        ArrayList<User> friends = new ArrayList<>();

        String sql = "SELECT u.* FROM friendships f "
                + "JOIN users u ON "
                + "CASE "
                + "WHEN f.user1_id = ? THEN f.user2_id = u.user_id "
                + "WHEN f.user2_id = ? THEN f.user1_id = u.user_id "
                + "END "
                + "WHERE f.user1_id = ? OR f.user2_id = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());
            ps.setInt(3, user.getId());
            ps.setInt(4, user.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                friends.add(createUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Get friends error: " + e.getMessage());
        }

        return friends;
    }

    public int getFriendCount(User user) {
        String sql = "SELECT COUNT(*) AS total FROM friendships "
                + "WHERE user1_id = ? OR user2_id = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Friend count error: " + e.getMessage());
        }

        return 0;
    }

    public int getCloseFriendCount(User user) {
        String sql = "SELECT COUNT(*) AS total FROM friendships "
                + "WHERE (user1_id = ? OR user2_id = ?) "
                + "AND close_friend = TRUE";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Close friend count error: " + e.getMessage());
        }

        return 0;
    }

    public boolean isCloseFriend(User user1, User user2) {
        String sql = "SELECT close_friend FROM friendships "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user1.getId());
            ps.setInt(2, user2.getId());
            ps.setInt(3, user2.getId());
            ps.setInt(4, user1.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("close_friend");
            }

        } catch (SQLException e) {
            System.out.println("Is close friend error: " + e.getMessage());
        }

        return false;
    }

    public boolean setCloseFriend(User user1, User user2, boolean closeFriend) {
        String sql = "UPDATE friendships SET close_friend = ? "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, closeFriend);
            ps.setInt(2, user1.getId());
            ps.setInt(3, user2.getId());
            ps.setInt(4, user2.getId());
            ps.setInt(5, user1.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Set close friend error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFriend(User user1, User user2) {
        String selectSql = "SELECT friendship_id, note FROM friendships "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        String insertOldSql = "INSERT INTO old_friends(user1_id, user2_id, note) VALUES (?, ?, ?)";

        String deleteSql = "DELETE FROM friendships "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement selectPs = con.prepareStatement(selectSql)) {

                selectPs.setInt(1, user1.getId());
                selectPs.setInt(2, user2.getId());
                selectPs.setInt(3, user2.getId());
                selectPs.setInt(4, user1.getId());

                ResultSet rs = selectPs.executeQuery();

                if (!rs.next()) {
                    con.rollback();
                    return false;
                }

                String note = rs.getString("note");

                try (PreparedStatement insertPs = con.prepareStatement(insertOldSql); PreparedStatement deletePs = con.prepareStatement(deleteSql)) {

                    insertPs.setInt(1, user1.getId());
                    insertPs.setInt(2, user2.getId());
                    insertPs.setString(3, note);
                    insertPs.executeUpdate();

                    deletePs.setInt(1, user1.getId());
                    deletePs.setInt(2, user2.getId());
                    deletePs.setInt(3, user2.getId());
                    deletePs.setInt(4, user1.getId());

                    int deletedRows = deletePs.executeUpdate();

                    if (deletedRows == 0) {
                        con.rollback();
                        return false;
                    }

                    LogManager.log(user1, "REMOVE_FRIEND",
                            user1.getUsername() + " removed " + user2.getUsername() + " from friends.");

                    con.commit();
                    return true;
                }

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Remove friend transaction error: " + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Remove friend error: " + e.getMessage());
            return false;
        }
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

    public String getFriendshipNote(User user1, User user2) {
        String sql = "SELECT note FROM friendships "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user1.getId());
            ps.setInt(2, user2.getId());
            ps.setInt(3, user2.getId());
            ps.setInt(4, user1.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String note = rs.getString("note");
                return note == null ? "" : note;
            }

        } catch (SQLException e) {
            System.out.println("Get friendship note error: " + e.getMessage());
        }

        return "";
    }

    public boolean updateFriendshipDetails(User user1, User user2, boolean closeFriend, String note) {
        String sql = "UPDATE friendships SET close_friend = ?, note = ? "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, closeFriend);
            ps.setString(2, note);
            ps.setInt(3, user1.getId());
            ps.setInt(4, user2.getId());
            ps.setInt(5, user2.getId());
            ps.setInt(6, user1.getId());

            boolean success = ps.executeUpdate() > 0;

            if (success) {
                LogManager.log(user1, "UPDATE_FRIENDSHIP",
                        user1.getUsername() + " updated friendship details with "
                        + user2.getUsername() + ".");
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Update friendship details error: " + e.getMessage());
            return false;
        }
    }
}
