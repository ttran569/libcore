package server.services;

import java.util.*;

public class SessionManager {
    // stores user id and the last active time in milliseconds
    private final Map<String, Long> activeSessions;
    // the session expiry time set to 10 seconds for testing
    private final long sessionExpiryTime = 10 * 1000;

    public SessionManager() {
        activeSessions = new HashMap<>();
    }

    // creates a session by adding the user and current time
    public void createSession(String userID) {
        activeSessions.put(userID, System.currentTimeMillis());
    }

    // checks whether a session is still active or has expired
    public boolean isSessionActive(String userID) {
        if (!activeSessions.containsKey(userID)) return false;
        long lastAccess = activeSessions.get(userID);
        if (System.currentTimeMillis() - lastAccess > sessionExpiryTime) {
            activeSessions.remove(userID);
            return false;
        }
        return true;
    }

    // refreshes a session's last active time
    public void refreshSession(String userID) {
        activeSessions.put(userID, System.currentTimeMillis());
    }

    // ends the session by removing the user
    public void endSession(String userID) {
        activeSessions.remove(userID);
    }
}
