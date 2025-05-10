package server.services;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {
    SessionManager manager;

    @BeforeEach
    void init() {
        manager = new SessionManager();
    }

    @Test
    void sessionShouldBeActiveAfterCreation() {
        manager.createSession("SL");
        assertTrue(manager.isSessionActive("SL"));
    }

    @Test
    void sessionShouldExpireAfterTimeout() throws InterruptedException {
        manager.createSession("SL");
        Thread.sleep(31000); // 31 seconds
        assertFalse(manager.isSessionActive("SL"));
    }

    @Test
    void sessionShouldEndManually() {
        manager.createSession("SL");
        manager.endSession("SL");
        assertFalse(manager.isSessionActive("SL"));
    }
}