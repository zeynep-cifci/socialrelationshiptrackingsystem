/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

/**
 *
 * @author Kullanıcı
 */
public class main {

    public static void main(String[] args) {
        // SYSTEM
        SocialMediaSystem system = new SocialMediaSystem();

        // USERS
        User u1 = new User("ali123", "123", "Ali AK");
        User u2 = new User("ayse_dmr", "456", "Ayse Demir");
        User u3 = new User("mehmet", "789", "Mehmet Kaya");

        system.addUser(u1);
        system.addUser(u2);
        system.addUser(u3);

        // TEST 1 - USERS
        System.out.println("----- USERS -----");
        System.out.println(system.getUsers());

        // TEST 2 - SEND FRIEND REQUEST
        System.out.println("\n----- SEND REQUEST -----");
        boolean requestSent1 = system.sendFriendRequest(u1, u2);
        System.out.println("Request sent from Ali to Ayse: " + requestSent1);
        System.out.println(system.getRequests());

        // TEST 3 - ACCEPT REQUEST
        System.out.println("\n----- ACCEPT REQUEST -----");
        FriendRequest request1 = system.getRequests().get(0);
        boolean accepted = system.acceptFriendRequest(request1);

        System.out.println("Request accepted: " + accepted);
        System.out.println(system.getRequests());
        System.out.println(system.getFriendships());

        // TEST 4 - SEND ANOTHER REQUEST
        System.out.println("\n----- SECOND REQUEST -----");
        boolean requestSent2 = system.sendFriendRequest(u3, u2);

        System.out.println("Request sent from Mehmet to Ayse: " + requestSent2);
        System.out.println(system.getRequests());

        // TEST 5 - PENDING REQUESTS FOR USER
        System.out.println("\n----- PENDING REQUESTS FOR AYSE -----");
        System.out.println(system.getPendingRequestsForUser(u2));

        // TEST 6 - FRIEND COUNT
        System.out.println("\n----- FRIEND COUNT -----");
        System.out.println("Ali friend count: " + system.countFriends(u1));
        System.out.println("Ayse friend count: " + system.countFriends(u2));
        System.out.println("Mehmet friend count: " + system.countFriends(u3));

        // TEST 7 - REMOVE FRIEND
        System.out.println("\n----- REMOVE FRIEND -----");
        boolean removed = system.removeFriend(u1, u2);
        System.out.println("Ali and Ayse friendship removed: " + removed);
        System.out.println(system.getFriendships());

        // TEST 8 - GET FRIENDS OF USER
        System.out.println("\n----- FRIENDS OF USER -----");
        System.out.println("Ali's friends: " + system.getFriendsOfUser(u1));
        System.out.println("Ayse's friends: " + system.getFriendsOfUser(u2));
        System.out.println("Mehmet's friends: " + system.getFriendsOfUser(u3));

        // TEST 9 - LOGIN
        System.out.println("\n----- LOGIN -----");
        User loginUser1 = system.login("ali", "123");
        if (loginUser1 != null) {
            System.out.println("Login successful: " + loginUser1);
        } else {
            System.out.println("Login failed.");
        }

        User loginUser2 = system.login("ali", "wrongpassword");
        if (loginUser2 != null) {
            System.out.println("Login successful: " + loginUser2);
        } else {
            System.out.println("Login failed.");
        }

        // TEST 10 - FIND USER BY ID
        System.out.println("\n----- FIND USER BY ID -----");
        User foundUser = system.findUserById(1);
        System.out.println("User with ID 1: " + foundUser);

// TEST 11 - FIND REQUEST BY ID
        System.out.println("\n----- FIND REQUEST BY ID -----");
        if (!system.getRequests().isEmpty()) {
            int reqId = system.getRequests().get(0).getId();
            FriendRequest foundRequest = system.findRequestById(reqId);
            System.out.println("Request with ID " + reqId + ": " + foundRequest);
        }
    }
}
