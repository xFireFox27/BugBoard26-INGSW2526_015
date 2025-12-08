package it.unina.frontend.util;

import it.unina.frontend.model.User;

public class SessionManager {
    private static SessionManager instance;
    private String token;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    // Getter e Setter
    public void setToken(String token) { this.token = token; }
    public String getToken() { return token; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
}