package nl.gleb.runmissions;

/**
 * Created by Gleb on 04/11/14.
 */
public class User {

    private String email;
    private String username;
    private int level;
    private int exp;
    private int missions;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private User() { }

    User(String email, String username) {
        this.email = email;
        this.username = username;
        this.level = 1;
        this.exp = 0;
        this.missions = 0;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getMissions() {
        return missions;
    }
}
