package nl.gleb.runmissions;

import java.util.List;

/**
 * Created by Gleb on 26/10/14.
 */
public interface Comm {
    public void setTitle(String data);
    public void openSignup();
    public void handleSignup(String email, String password, String username);
    public void handleLogin(String email, String password);
    public void sendMessage(String message);
    public User getUser();

    void updateSteps(List<DirectionsStep> steps);

    long[] getPattern(String pattern);

    void setTarget(Place target);

    void updateUserAvatar(String avatar);
}
