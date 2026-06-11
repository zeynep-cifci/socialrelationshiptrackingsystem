/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Kullanıcı
 */
public class UserDAO {

    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, full_name, bio, role, is_premium, verified) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(5, user.getRole());

            if (user instanceof PremiumUser) {
                PremiumUser premiumUser = (PremiumUser) user;
                ps.setString(4, premiumUser.getBio());
                ps.setBoolean(6, true);
                ps.setBoolean(7, premiumUser.isVerified());
            } else {
                ps.setString(4, null);
                ps.setBoolean(6, false);
                ps.setBoolean(7, false);
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1)); // MySQL real ID
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Add user error: " + e.getMessage());
        }

        return false;
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return createUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Get users error: " + e.getMessage());
        }

        return users;
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

    public ArrayList<User> searchUsers(String searchText, User currentUser) {
        ArrayList<User> users = new ArrayList<>();

        if (searchText == null || searchText.trim().isEmpty()) {
            return users;
        }

        String sql = "SELECT * FROM users "
                + "WHERE user_id <> ? "
                + "AND role <> 'ADMIN' " //This part blocks admin users
                + "AND (username LIKE ? OR full_name LIKE ?)";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, currentUser.getId());
            ps.setString(2, searchText + "%");
            ps.setString(3, searchText + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Search users error: " + e.getMessage());
        }

        return users;
    }

    public boolean updateUserProfile(User user, String newUsername, String newFullName, String newPassword, String newBio) {
        if (user == null
                || newUsername == null || newUsername.trim().isEmpty()
                || newFullName == null || newFullName.trim().isEmpty()
                || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        String duplicateSql = "SELECT user_id FROM users WHERE username = ? AND user_id <> ?";
        String updateSql = "UPDATE users SET username = ?, full_name = ?, password = ?, bio = ? WHERE user_id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement duplicatePs = con.prepareStatement(duplicateSql)) {
                duplicatePs.setString(1, newUsername);
                duplicatePs.setInt(2, user.getId());

                try (ResultSet rs = duplicatePs.executeQuery()) {
                    if (rs.next()) {
                        return false;
                    }
                }
            }

            try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                updatePs.setString(1, newUsername);
                updatePs.setString(2, newFullName);
                updatePs.setString(3, newPassword);

                if (user instanceof PremiumUser) {
                    updatePs.setString(4, newBio);
                } else {
                    updatePs.setString(4, null);
                }

                updatePs.setInt(5, user.getId());

                boolean success = updatePs.executeUpdate() > 0;

                if (success) {
                    user.setUsername(newUsername);
                    user.setFullName(newFullName);
                    user.setPassword(newPassword);

                    if (user instanceof PremiumUser) {
                        ((PremiumUser) user).setBio(newBio);
                    }

                    LogManager.log(user, "UPDATE_PROFILE",
                            user.getUsername() + " updated profile information.");
                }

                return success;
            }

        } catch (SQLException e) {
            System.out.println("Update user profile error: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<User> getPremiumUsers() {
        ArrayList<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users WHERE is_premium = TRUE";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Get premium users error: " + e.getMessage());
        }

        return users;
    }
}
