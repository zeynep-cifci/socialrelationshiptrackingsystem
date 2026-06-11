/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socialrelationshiptrackingsystem;

import java.util.ArrayList;

/**
 *
 * @author Kullanıcı
 */
public class SocialMediaSystem {

    private ArrayList<User> users;
    private ArrayList<FriendRequest> requests;
    private ArrayList<Friendship> friendships;
    private ArrayList<Friendship> oldFriendships;

    public SocialMediaSystem() {
        users = new ArrayList<>();
        requests = new ArrayList<>();
        friendships = new ArrayList<>();
        oldFriendships = new ArrayList<>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<FriendRequest> getRequests() {
        return requests;
    }

    public ArrayList<Friendship> getFriendships() {
        return friendships;
    }

    public boolean addUser(User user) {

        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
                return false;
            }
        }
        users.add(user);
        return true;
    }

    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean sendFriendRequest(User sender, User receiver) {
        if (sender == null || receiver == null) {
            return false;
        }

        if (sender.equals(receiver)) {
            return false;
        }

        for (Friendship f : friendships) {
            boolean sameDirection = f.getUser1().equals(sender) && f.getUser2().equals(receiver);
            boolean oppositeDirection = f.getUser1().equals(receiver) && f.getUser2().equals(sender);

            if (sameDirection || oppositeDirection) {
                return false;
            }
        }

        for (FriendRequest r : requests) {
            boolean sameDirection = r.getSender().equals(sender) && r.getReceiver().equals(receiver);
            boolean oppositeDirection = r.getSender().equals(receiver) && r.getReceiver().equals(sender);

            if ((sameDirection || oppositeDirection) && r.getStatus() == FriendRequest.RequestStatus.PENDING) {
                return false;
            }
        }

        FriendRequest request = new FriendRequest(sender, receiver);
        requests.add(request);
        return true;
    }

    public boolean acceptFriendRequest(FriendRequest request) {

        if (request == null) {
            return false;
        }

        if (request.getStatus() != FriendRequest.RequestStatus.PENDING) {
            return false;
        }

        // change status
        request.setStatus(FriendRequest.RequestStatus.ACCEPTED);

        // create friendship
        Friendship friendship = new Friendship(
                request.getSender(),
                request.getReceiver()
        );

        friendships.add(friendship);

        return true;
    }

    public boolean rejectFriendRequest(FriendRequest request) {

        if (request == null) {
            return false;
        }

        if (request.getStatus() != FriendRequest.RequestStatus.PENDING) {
            return false;
        }

        request.setStatus(FriendRequest.RequestStatus.REJECTED);

        return true;
    }

    public boolean removeFriend(User user1, User user2) {
        for (int i = 0; i < friendships.size(); i++) {
            Friendship f = friendships.get(i);

            boolean sameDirection = f.getUser1().equals(user1) && f.getUser2().equals(user2);
            boolean oppositeDirection = f.getUser1().equals(user2) && f.getUser2().equals(user1);

            if (sameDirection || oppositeDirection) {
                oldFriendships.add(f);
                friendships.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<FriendRequest> getPendingRequestsForUser(User user) {
        ArrayList<FriendRequest> pendingRequests = new ArrayList<>();

        for (FriendRequest r : requests) {
            if (r.getReceiver().equals(user)
                    && r.getStatus() == FriendRequest.RequestStatus.PENDING) {
                pendingRequests.add(r);
            }
        }

        return pendingRequests;
    }

    public int countFriends(User user) {
        int count = 0;

        for (Friendship f : friendships) {
            if (f.getUser1().equals(user) || f.getUser2().equals(user)) {
                count++;
            }
        }

        return count;
    }

    public ArrayList<User> getFriendsOfUser(User user) {
        ArrayList<User> friendList = new ArrayList<>();

        for (Friendship f : friendships) {
            if (f.getUser1().equals(user)) {
                friendList.add(f.getUser2());
            } else if (f.getUser2().equals(user)) {
                friendList.add(f.getUser1());
            }
        }

        return friendList;
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User findUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public FriendRequest findRequestById(int id) {
        for (FriendRequest r : requests) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public int countCloseFriends(User user) {
        int count = 0;

        for (Friendship f : friendships) {
            boolean relatedToUser
                    = f.getUser1().equals(user) || f.getUser2().equals(user);

            if (relatedToUser && f.isCloseFriend()) {
                count++;
            }
        }

        return count;
    }

    public boolean setCloseFriend(User user1, User user2, boolean isCloseFriend) {
        for (Friendship f : friendships) {
            boolean sameDirection
                    = f.getUser1().equals(user1) && f.getUser2().equals(user2);

            boolean oppositeDirection
                    = f.getUser1().equals(user2) && f.getUser2().equals(user1);

            if (sameDirection || oppositeDirection) {
                f.setCloseFriend(isCloseFriend);
                return true;
            }
        }
        return false;
    }

    public boolean isCloseFriend(User user1, User user2) {
        for (Friendship f : friendships) {
            boolean sameDirection
                    = f.getUser1().equals(user1) && f.getUser2().equals(user2);

            boolean oppositeDirection
                    = f.getUser1().equals(user2) && f.getUser2().equals(user1);

            if (sameDirection || oppositeDirection) {
                return f.isCloseFriend();
            }
        }
        return false;
    }

    public ArrayList<User> searchUsers(String searchText, User currentUser) {
        ArrayList<User> result = new ArrayList<>();

        if (searchText == null || searchText.trim().isEmpty()) {
            return result;
        }

        String search = searchText.toLowerCase().trim();

        for (User user : users) {
            if (!user.equals(currentUser)) {
                boolean usernameMatch = user.getUsername().toLowerCase().contains(search);
                boolean fullNameMatch = user.getFullName().toLowerCase().contains(search);

                if (usernameMatch || fullNameMatch) {
                    result.add(user);
                }
            }
        }

        return result;
    }

    public boolean updateUserProfile(User currentUser, String newUsername, String newFullName, String newPassword) {
        if (newUsername == null || newUsername.trim().isEmpty()
                || newFullName == null || newFullName.trim().isEmpty()
                || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        for (User user : users) {
            if (!user.equals(currentUser) && user.getUsername().equalsIgnoreCase(newUsername)) {
                return false;
            }
        }

        currentUser.setUsername(newUsername);
        currentUser.setFullName(newFullName);
        currentUser.setPassword(newPassword);

        return true;
    }

    public ArrayList<User> getOldFriendsOfUser(User user) {
        ArrayList<User> oldFriends = new ArrayList<>();

        for (Friendship f : oldFriendships) {
            if (f.getUser1().equals(user)) {
                oldFriends.add(f.getUser2());
            } else if (f.getUser2().equals(user)) {
                oldFriends.add(f.getUser1());
            }
        }

        return oldFriends;
    }

}
