/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 *
 * @author Kullanıcı
 */
public class ActivityLogDAO {

    public boolean addLog(User user, String actionType, String description) {
        String sql = "INSERT INTO activity_logs(user_id, action_type, description) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            if (user != null) {
                ps.setInt(1, user.getId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            ps.setString(2, actionType);
            ps.setString(3, description);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Add activity log error: " + e.getMessage());
            return false;
        }
    }

    public ResultSet getAllLogs() {
        String sql = "SELECT l.log_id, u.username, l.action_type, l.description, l.created_at "
                + "FROM activity_logs l "
                + "LEFT JOIN users u ON l.user_id = u.user_id "
                + "ORDER BY l.created_at DESC";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Get logs error: " + e.getMessage());
            return null;
        }
    }
}
