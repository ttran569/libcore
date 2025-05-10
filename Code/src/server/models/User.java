package server.models;

import server.models.enums.UserRole;
import java.io.Serializable;

public class User implements Serializable {
    // unique id for the user
    private String userID;
    // display name of the user
    private String username;
    // user's password
    private String password;
    // user's address
    private String address;
    // user's contact number
    private String contactNumber;
    // role of the user (e.g. staff or member)
    private UserRole role;

    // constructor initializes a user object with all required details
    public User(String userID, String username, String password, String address, String contactNumber, UserRole role) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.address = address;
        this.contactNumber = contactNumber;
        this.role = role;
    }

    // checks if the entered password matches the stored password
    public boolean validatePassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    // getter methods for all fields
    public String getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
    public String getContactNumber() { return contactNumber; }
    public UserRole getRole() { return role; }

    // setter methods for editable fields
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setAddress(String address) { this.address = address; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setRole(UserRole role) { this.role = role; }
}
