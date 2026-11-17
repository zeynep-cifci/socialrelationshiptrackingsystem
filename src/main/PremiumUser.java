package socialrelationshiptrackingsystem;

/**
 * PremiumUser extends User with extra profile fields:
 * a short bio and a verified badge.
 * Demonstrates inheritance and polymorphism over the base User class.
 */
public class PremiumUser extends User {

    private String bio;
    private boolean verified;

    public PremiumUser(String username, String password, String fullName, String bio) {
        super(username, password, fullName);
        this.bio = bio;
        this.verified = true;
    }

    public PremiumUser(int id, String username, String password, String fullName,
                       String role, String bio, boolean verified) {
        super(id, username, password, fullName, role);
        this.bio = bio;
        this.verified = verified;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String getProfileSummary() {
        return "[Premium] " + getUsername() + " - " + getFullName()
                + " | Bio: " + bio
                + " | Verified: " + verified;
    }

    @Override
    public String toString() {
        return "[Premium] " + getUsername() + " - " + getFullName();
    }
}
