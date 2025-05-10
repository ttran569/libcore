package server.services;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.models.User;

public class AuthenticationServiceTest {
    AuthenticationService auth;

    @BeforeEach
    void setUp() {
        auth = new AuthenticationService();
    }

    @Test
    void validLoginShouldSucceed() {
        assertTrue(auth.validateCredentials("SL", "securepass32"));
    }

    @Test
    void invalidLoginShouldFail() {
        assertFalse(auth.validateCredentials("SL", "wrongpass"));
    }

    @Test
    void nonExistentUserShouldFail() {
        assertFalse(auth.validateCredentials("XX", "password"));
    }

    @Test
    void getUserShouldReturnCorrectUser() {
        User user = auth.getUser("SL");
        assertNotNull(user);
        assertEquals("steven le", user.getUsername().toLowerCase());
    }
}