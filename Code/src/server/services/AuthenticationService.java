package server.services;

import server.models.User;
import java.io.*;
import java.util.*;

public class AuthenticationService {
    // this map holds user id as key and user object as value
    private Map<String, User> users;

    public AuthenticationService() {
        users = new HashMap<>();
        // load users from the users.txt file on initialization
        loadUsersFromFile("data/users.txt");
    }

    // validates user credentials by checking the user id and password
    public boolean validateCredentials(String userID, String password) {
        User user = users.get(userID);
        return user != null && user.validatePassword(password);
    }

    // returns the user object associated with the given user id
    public User getUser(String userID) {
        return users.get(userID);
    }

    // loads users from a text file and populates the users map
    private void loadUsersFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    User user = new User(
                        parts[0], parts[1], parts[2], parts[3], parts[4],
                        Enum.valueOf(server.models.enums.UserRole.class, parts[5])
                    );
                    users.put(user.getUserID(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("error loading users: " + e.getMessage());
        }
    }
}
