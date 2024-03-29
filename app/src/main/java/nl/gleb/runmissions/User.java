package nl.gleb.runmissions;

import android.location.Location;

/**
 * Created by Gleb on 04/11/14.
 */
public class User {

    private String email;
    private String username;
    private String avatar;
    private String target;
    private int level;
    private int exp;
    private int missions;
    private double lat;
    private double lng;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private User() {
    }

    User(String email, String username, Double lat, Double lng) {
        this.email = email;
        this.username = username;
        this.avatar = "a_1";
        this.target = "";
        this.level = 1;
        this.exp = 0;
        this.missions = 0;
        this.lat = lat;
        this.lng = lng;
    }

    public User(String email, String username, String avatar, String target, Integer level, Integer exp, Integer missions, Double lat, Double lng) {
        this.email = email;
        this.username = username;
        this.avatar = avatar;
        this.target = target;
        this.level = level;
        this.exp = exp;
        this.missions = missions;
        this.lat = lat;
        this.lng = lng;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getTarget() {
        return target;
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

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLocation(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
    }
}
