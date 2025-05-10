package server.services;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ResourceManagerTest {
    ResourceManager rm;

    @BeforeEach
    void setup() {
        rm = new ResourceManager();
    }

    @Test
    void shouldReturnAllResources() {
        try {
            List<String> resources = rm.getAllResources();  // list of CSV strings
            assertFalse(resources.isEmpty(), "Resource list should not be empty.");
        } catch (Exception e) {
            fail("Exception during getAllResources: " + e.getMessage());
        }
    }

    @Test
    void updateAvailabilityShouldChangeStatus() {
        try {
            rm.updateAvailability("R001", false);

            List<String> resources = rm.getAllResources();
            boolean found = false;

            for (String line : resources) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[0].equals("R001")) {
                    found = true;
                    assertEquals("false", parts[4], "Availability should be false for R001");
                    break;
                }
            }

            assertTrue(found, "Resource R001 not found in list.");

        } catch (Exception e) {
            fail("Exception during updateAvailability: " + e.getMessage());
        }
    }
}
