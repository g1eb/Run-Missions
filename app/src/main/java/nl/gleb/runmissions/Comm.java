package nl.gleb.runmissions;

/**
 * Created by Gleb on 26/10/14.
 */
public interface Comm {
    public void setTitle(String data);
    public void handleLogin(String email, String password);
    public void sendMessage(String message);
}
