package server.services;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class UserManagerTest {
    UserManager um;

    @BeforeEach
    void setup() {
        um = new UserManager();
    }

    @Test
    void shouldReturnAllUsers() {
        try {
            List<String> users = um.getAllUsers();
            assertNotNull(users, "User list should not be null.");
            assertFalse(users.isEmpty(), "User list should not be empty.");
        } catch (Exception e) {
            fail("Exception while loading users: " + e.getMessage());
        }
    }

    @Test
    void userFormatShouldBeCorrect() {
        try {
            List<String> users = um.getAllUsers();
            for (String user : users) {
                String[] parts = user.split(",");
                assertEquals(6, parts.length, "Each user should have 6 fields (ID, Name, Password, Address, Phone, Role).");
            }
        } catch (Exception e) {
            fail("Exception while validating user format: " + e.getMessage());
        }
    }
}
