/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

/**
 *
 * @author Kullanıcı
 */
public class User {

    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role;

    public User(String username, String password, String fullName) {
        this.id = 0; // MySQL will give the real id
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = "USER";
    }

    public User(int id, String username, String password, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    // Used by DAO after MySQL creates/loads the id
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public String getProfileSummary() {
        return getUsername() + " - " + getFullName();
    }

    @Override
    public String toString() {
        return username + " - " + fullName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User user = (User) obj;

        if (id > 0 && user.id > 0) {
            return id == user.id;
        }

        return username != null
                && user.username != null
                && username.equalsIgnoreCase(user.username);
    }

    @Override
    public int hashCode() {
        if (id > 0) {
            return Integer.hashCode(id);
        }

        return username == null ? 0 : username.toLowerCase().hashCode();
    }
}
