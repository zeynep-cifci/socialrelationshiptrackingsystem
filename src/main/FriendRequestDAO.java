/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Kullanıcı
 */
public class FriendRequestDAO {

    public boolean sendFriendRequest(User sender, User receiver) {

        if (sender == null || receiver == null) {
            return false;
        }

        if (sender.getId() == receiver.getId()) {
            return false;
        }

        if (areAlreadyFriends(sender.getId(), receiver.getId())) {
            return false;
        }

        if (hasPendingRequest(sender.getId(), receiver.getId())) {
            return false;
        }

        String sql = "INSERT INTO friend_requests(sender_id, receiver_id, status) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, sender.getId());
            ps.setInt(2, receiver.getId());
            ps.setString(3, "PENDING");

            boolean success = ps.executeUpdate() > 0;

            if (success) {
                LogManager.log(sender, "SEND_REQUEST",
                        sender.getUsername() + " sent a friend request to " + receiver.getUsername() + ".");
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Send friend request error: " + e.getMessage());
            return false;
        }
    }

    private boolean hasPendingRequest(int user1Id, int user2Id) {
        String sql = "SELECT request_id FROM friend_requests "
                + "WHERE status = 'PENDING' AND "
                + "((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?))";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            ps.setInt(3, user2Id);
            ps.setInt(4, user1Id);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Check pending request error: " + e.getMessage());
            return true;
        }
    }

    private boolean areAlreadyFriends(int user1Id, int user2Id) {
        String sql = "SELECT friendship_id FROM friendships "
                + "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            ps.setInt(3, user2Id);
            ps.setInt(4, user1Id);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Check friendship error: " + e.getMessage());
            return true;
        }
    }

    public ArrayList<FriendRequest> getPendingRequestsForUser(User receiver) {
        ArrayList<FriendRequest> requests = new ArrayList<>();

        String sql = "SELECT fr.request_id, fr.status, "
                + "u.user_id, u.username, u.password, u.full_name, u.role, u.bio, u.is_premium, u.verified "
                + "FROM friend_requests fr "
                + "JOIN users u ON fr.sender_id = u.user_id "
                + "WHERE fr.receiver_id = ? AND fr.status = 'PENDING'";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, receiver.getId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User sender = createUserFromResultSet(rs);

                FriendRequest request = new FriendRequest(
                        rs.getInt("request_id"),
                        sender,
                        receiver,
                        FriendRequest.RequestStatus.valueOf(rs.getString("status"))
                );

                requests.add(request);
            }

        } catch (SQLException e) {
            System.out.println("Get pending requests error: " + e.getMessage());
        }

        return requests;
    }

    public boolean acceptRequest(FriendRequest request) {

        if (request == null) {
            return false;
        }

        String checkFriendshipSql = "SELECT friendship_id FROM friendships "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        String updateRequestSql = "UPDATE friend_requests SET status = 'ACCEPTED' "
                + "WHERE request_id = ? AND status = 'PENDING'";
        
        String insertFriendshipSql = "INSERT INTO friendships(user1_id, user2_id, close_friend, note) "
                + "VALUES (?, ?, ?, ?)";

        String deleteOldFriendSql = "DELETE FROM old_friends "
                + "WHERE (user1_id = ? AND user2_id = ?) "
                + "OR (user1_id = ? AND user2_id = ?)";

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement checkPs = con.prepareStatement(checkFriendshipSql); PreparedStatement ps1 = con.prepareStatement(updateRequestSql); PreparedStatement ps2 = con.prepareStatement(insertFriendshipSql); PreparedStatement ps3 = con.prepareStatement(deleteOldFriendSql)) {

                checkPs.setInt(1, request.getSender().getId());
                checkPs.setInt(2, request.getReceiver().getId());
                checkPs.setInt(3, request.getReceiver().getId());
                checkPs.setInt(4, request.getSender().getId());

                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        con.rollback();
                        return false;
                    }
                }

                ps1.setInt(1, request.getId());
                int updatedRows = ps1.executeUpdate();

                if (updatedRows == 0) {
                    con.rollback();
                    return false;
                }

                ps2.setInt(1, request.getSender().getId());
                ps2.setInt(2, request.getReceiver().getId());
                ps2.setBoolean(3, false);
                ps2.setString(4, "");
                ps2.executeUpdate();

                ps3.setInt(1, request.getSender().getId());
                ps3.setInt(2, request.getReceiver().getId());
                ps3.setInt(3, request.getReceiver().getId());
                ps3.setInt(4, request.getSender().getId());
                ps3.executeUpdate();

                LogManager.log(request.getReceiver(), "ACCEPT_REQUEST",
                        request.getReceiver().getUsername() + " accepted friend request from "
                        + request.getSender().getUsername() + ".");

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Accept request transaction error: " + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Accept request error: " + e.getMessage());
            return false;
        }
    }

    public boolean rejectRequest(FriendRequest request) {

        if (request == null) {
            return false;
        }

        String sql = "UPDATE friend_requests SET status = 'REJECTED' WHERE request_id = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, request.getId());

            boolean success = ps.executeUpdate() > 0;

            if (success) {
                LogManager.log(request.getReceiver(), "REJECT_REQUEST",
                        request.getReceiver().getUsername() + " rejected friend request from "
                        + request.getSender().getUsername() + ".");
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Reject request error: " + e.getMessage());
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

    public int getPendingRequestCount(User receiver) {
        String sql = "SELECT COUNT(*) AS total FROM friend_requests "
                + "WHERE receiver_id = ? AND status = 'PENDING'";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, receiver.getId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Pending request count error: " + e.getMessage());
        }

        return 0;
    }
}
