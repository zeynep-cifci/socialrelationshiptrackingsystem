/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

/**
 *
 * @author Kullanıcı
 */
public class Friendship {

    private int id;
    private User user1;
    private User user2;
    private boolean closeFriend;
    private String note;

    public Friendship(User user1, User user2) {
        this.id = 0; // MySQL will give real friendship_id
        this.user1 = user1;
        this.user2 = user2;
        this.closeFriend = false;
        this.note = "";
    }

    public Friendship(int id, User user1, User user2, boolean closeFriend, String note) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.closeFriend = closeFriend;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    // DAO uses this after MySQL gives friendship_id
    public void setId(int id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public boolean isCloseFriend() {
        return closeFriend;
    }

    public void setCloseFriend(boolean closeFriend) {
        this.closeFriend = closeFriend;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Friendship #" + id + ": "
                + user1.getUsername() + " <-> "
                + user2.getUsername();
    }
}
