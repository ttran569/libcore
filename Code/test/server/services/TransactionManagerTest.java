package server.services;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TransactionManagerTest {
    TransactionManager tm;

    @BeforeEach
    void setup() {
        tm = new TransactionManager();
    }

    @Test
    void shouldLoadTransactions() {
        try {
            List<String> txns = tm.getAllTransactions();
            assertNotNull(txns, "Transaction list should not be null.");
            assertTrue(txns.size() >= 0, "Transaction list should be valid.");
        } catch (Exception e) {
            fail("Exception while loading transactions: " + e.getMessage());
        }
    }

    @Test
    void transactionFormatShouldBeCorrect() {
        try {
            List<String> txns = tm.getAllTransactions();
            for (String txn : txns) {
                String[] parts = txn.split(",");
                assertTrue(parts.length >= 4, "Each transaction should have at least 4 fields.");
            }
        } catch (Exception e) {
            fail("Exception during transaction format check: " + e.getMessage());
        }
    }
}
