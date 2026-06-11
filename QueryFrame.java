/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Kullanıcı
 */
public class QueryFrame extends javax.swing.JFrame {

    private SocialMediaSystem system;
    private User currentUser;
    private DefaultTableModel tableModel;

    private UserDAO userDAO;
    private FriendshipDAO friendshipDAO;
    private FriendRequestDAO friendRequestDAO;
    private OldFriendDAO oldFriendDAO;
    private ActivityLogDAO activityLogDAO;

    /**
     * Creates new form QueryFrame
     */
    public QueryFrame(SocialMediaSystem system, User currentUser) {
        initComponents();

        this.system = system;
        this.currentUser = currentUser;

        this.userDAO = new UserDAO();
        this.friendshipDAO = new FriendshipDAO();
        this.friendRequestDAO = new FriendRequestDAO();
        this.oldFriendDAO = new OldFriendDAO();
        this.activityLogDAO = new ActivityLogDAO();

        tableModel = new DefaultTableModel();
        tblResults.setModel(tableModel);

        loadQueryTypes();

        setLocationRelativeTo(null);
    }

    private void loadQueryTypes() {
        cmbQueryType.removeAllItems();

        if (currentUser.isAdmin()) {
            cmbQueryType.addItem("All Users");
            cmbQueryType.addItem("Premium Users");
            cmbQueryType.addItem("All Friendships");
            cmbQueryType.addItem("All Pending Requests");
            cmbQueryType.addItem("All Old Friends");
            cmbQueryType.addItem("Activity Logs");
        } else {
            cmbQueryType.addItem("Search Users");
            cmbQueryType.addItem("My Friends");
            cmbQueryType.addItem("My Close Friends");
            cmbQueryType.addItem("My Pending Requests");
            cmbQueryType.addItem("My Old Friends");
        }
    }

    private void runUserQuery(String queryType, String searchText) {
        switch (queryType) {
            case "Search Users":
                showSearchUsers(searchText);
                break;

            case "My Friends":
                showMyFriends(false);
                break;

            case "My Close Friends":
                showMyFriends(true);
                break;

            case "My Pending Requests":
                showMyPendingRequests();
                break;

            case "My Old Friends":
                showMyOldFriends();
                break;

            default:
                JOptionPane.showMessageDialog(this, "Invalid query type.");
        }
    }

    private void runAdminQuery(String queryType, String searchText) {
        switch (queryType) {
            case "All Users":
                showAllUsers(false);
                break;

            case "Premium Users":
                showAllUsers(true);
                break;

            case "All Friendships":
                showAllFriendships();
                break;

            case "All Pending Requests":
                showAllPendingRequests();
                break;

            case "All Old Friends":
                showAllOldFriends();
                break;

            case "Activity Logs":
                showActivityLogs();
                break;

            default:
                JOptionPane.showMessageDialog(this, "Invalid query type.");
        }
    }

    private void showSearchUsers(String searchText) {
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter search text.");
            return;
        }

        setColumns("ID", "Username", "Full Name", "Role");

        ArrayList<User> users = userDAO.searchUsers(searchText, currentUser);

        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole()
            });
        }
    }

    private void showMyFriends(boolean onlyCloseFriends) {
        setColumns("ID", "Username", "Full Name", "Friend Status");

        ArrayList<User> friends = friendshipDAO.getFriendsOfUser(currentUser);

        for (User friend : friends) {
            boolean close = friendshipDAO.isCloseFriend(currentUser, friend);

            if (onlyCloseFriends && !close) {
                continue;
            }

            String status = close ? "Close Friend" : "Normal Friend";

            tableModel.addRow(new Object[]{
                friend.getId(),
                friend.getUsername(),
                friend.getFullName(),
                status
            });
        }
    }

    private void showMyPendingRequests() {
        setColumns("Request ID", "Sender Username", "Sender Full Name", "Status");

        ArrayList<FriendRequest> requests = friendRequestDAO.getPendingRequestsForUser(currentUser);

        for (FriendRequest request : requests) {
            tableModel.addRow(new Object[]{
                request.getId(),
                request.getSender().getUsername(),
                request.getSender().getFullName(),
                request.getStatus().toString()
            });
        }
    }

    private void showMyOldFriends() {
        setColumns("ID", "Username", "Full Name");

        ArrayList<User> oldFriends = oldFriendDAO.getOldFriendsOfUser(currentUser);

        for (User oldFriend : oldFriends) {
            tableModel.addRow(new Object[]{
                oldFriend.getId(),
                oldFriend.getUsername(),
                oldFriend.getFullName()
            });
        }
    }

    private void showAllUsers(boolean premiumOnly) {
        setColumns("ID", "Username", "Full Name", "Role");

        ArrayList<User> users;

        if (premiumOnly) {
            users = userDAO.getPremiumUsers();
        } else {
            users = userDAO.getAllUsers();
        }

        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole()
            });
        }
    }

    private void showAllFriendships() {
        setColumns("Friendship ID", "User 1", "User 2", "Close Friend", "Note", "Date");

        String sql = "SELECT f.friendship_id, "
                + "u1.username AS user1_name, "
                + "u2.username AS user2_name, "
                + "f.close_friend, f.note, f.created_at "
                + "FROM friendships f "
                + "JOIN users u1 ON f.user1_id = u1.user_id "
                + "JOIN users u2 ON f.user2_id = u2.user_id "
                + "ORDER BY f.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("friendship_id"),
                    rs.getString("user1_name"),
                    rs.getString("user2_name"),
                    rs.getBoolean("close_friend"),
                    rs.getString("note"),
                    rs.getTimestamp("created_at")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load friendships.");
            System.out.println("All friendships query error: " + e.getMessage());
        }
    }

    private void showAllPendingRequests() {
        setColumns("Request ID", "Sender", "Receiver", "Status", "Date");

        String sql = "SELECT fr.request_id, "
                + "u1.username AS sender_name, "
                + "u2.username AS receiver_name, "
                + "fr.status, fr.request_date "
                + "FROM friend_requests fr "
                + "JOIN users u1 ON fr.sender_id = u1.user_id "
                + "JOIN users u2 ON fr.receiver_id = u2.user_id "
                + "WHERE fr.status = 'PENDING' "
                + "ORDER BY fr.request_date DESC";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("request_id"),
                    rs.getString("sender_name"),
                    rs.getString("receiver_name"),
                    rs.getString("status"),
                    rs.getTimestamp("request_date")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load pending requests.");
            System.out.println("All pending requests query error: " + e.getMessage());
        }
    }

    private void showAllOldFriends() {
        setColumns("Old Friend ID", "User 1", "User 2", "Removed Date");

        String sql = "SELECT o.old_friend_id, "
                + "u1.username AS user1_name, "
                + "u2.username AS user2_name, "
                + "o.removed_at "
                + "FROM old_friends o "
                + "JOIN users u1 ON o.user1_id = u1.user_id "
                + "JOIN users u2 ON o.user2_id = u2.user_id "
                + "ORDER BY o.removed_at DESC";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("old_friend_id"),
                    rs.getString("user1_name"),
                    rs.getString("user2_name"),
                    rs.getTimestamp("removed_at")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load old friends.");
            System.out.println("All old friends query error: " + e.getMessage());
        }
    }

    private void showActivityLogs() {
        setColumns("Log ID", "Username", "Action", "Description", "Date");

        try {
            ResultSet rs = activityLogDAO.getAllLogs();

            while (rs != null && rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("log_id"),
                    rs.getString("username"),
                    rs.getString("action_type"),
                    rs.getString("description"),
                    rs.getTimestamp("created_at")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load activity logs.");
            System.out.println("Activity logs query error: " + e.getMessage());
        }
    }

    private void setColumns(String... columns) {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        for (String column : columns) {
            tableModel.addColumn(column);
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblResults = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        cmbQueryType = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblResults);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 6, 210, 288));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Query Type: ");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 80, -1));

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 100, -1));

        cmbQueryType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(cmbQueryType, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 110, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Search:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, -1, -1));

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 180, 70, -1));

        jButton2.setText("Back");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 61, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String queryType = cmbQueryType.getSelectedItem().toString();
        String searchText = txtSearch.getText().trim();

        if (currentUser.isAdmin()) {
            runAdminQuery(queryType, searchText);
        } else {
            runUserQuery(queryType, searchText);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new MainMenuFrame(system, currentUser).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QueryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QueryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QueryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QueryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbQueryType;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblResults;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
