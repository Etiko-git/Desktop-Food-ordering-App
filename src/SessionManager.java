public class SessionManager {
    private static SessionManager instance;
    private int userId;
    private String username;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUser(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void clearSession() {
        userId = 0;
        username = null;
    }

    public boolean isLoggedIn() {
        return userId != 0;
    }
}
