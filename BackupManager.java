/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Kullanıcı
 */
public class BackupManager {

    private static final String FILE_NAME = "relationship_backup.dat";

    public boolean createBackup(User currentUser) {
        ArrayList<String> backupLines = new ArrayList<>();

        String sql = "SELECT f.friendship_id, "
                + "u1.username AS user1_name, "
                + "u2.username AS user2_name, "
                + "f.close_friend, f.note, f.created_at "
                + "FROM friendships f "
                + "JOIN users u1 ON f.user1_id = u1.user_id "
                + "JOIN users u2 ON f.user2_id = u2.user_id "
                + "WHERE f.user1_id = ? OR f.user2_id = ? "
                + "ORDER BY f.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, currentUser.getId());
            ps.setInt(2, currentUser.getId());

            try (ResultSet rs = ps.executeQuery()) {
                backupLines.add("SOCIAL RELATIONSHIP BACKUP");
                backupLines.add("User: " + currentUser.getUsername());
                backupLines.add("--------------------------------");

                while (rs.next()) {
                    String line = "Friendship ID: " + rs.getInt("friendship_id")
                            + " | User 1: " + rs.getString("user1_name")
                            + " | User 2: " + rs.getString("user2_name")
                            + " | Close Friend: " + rs.getBoolean("close_friend")
                            + " | Note: " + rs.getString("note")
                            + " | Date: " + rs.getTimestamp("created_at");

                    backupLines.add(line);
                }
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                out.writeObject(backupLines);
            }

            LogManager.log(currentUser, "BACKUP_CREATED",
                    currentUser.getUsername() + " created a binary backup file.");

            return true;

        } catch (Exception e) {
            System.out.println("Create backup error: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String> readBackup() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (ArrayList<String>) in.readObject();
        } catch (Exception e) {
            System.out.println("Read backup error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
