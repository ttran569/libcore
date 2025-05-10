package server.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    // file path for storing user records
    private static final String USER_FILE = "data/users.txt";

    // reads and returns all users as strings
    public static List<String> getAllUsers() throws IOException {
        List<String> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(line);
            }
        }
        return users;
    }
}
