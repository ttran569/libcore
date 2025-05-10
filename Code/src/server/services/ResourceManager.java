package server.services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager {
    // the file path for storing resource records
    private static final String RESOURCE_FILE = "data/resources.txt";

    // adds a new resource to the resource file
    public static void addResource(String id, String title, String author, String category) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESOURCE_FILE, true))) {
            writer.write(id + "," + title + "," + author + "," + category + ",true");
            writer.newLine();
        }
    }

    // returns a list of all resource entries as strings
    public static List<String> getAllResources() throws IOException {
        List<String> resources = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                resources.add(line);
            }
        }
        return resources;
    }

    // updates the availability status of a specific resource
    public static void updateAvailability(String resourceId, boolean available) throws IOException {
        List<String> resources = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(resourceId)) {
                    parts[4] = String.valueOf(available);
                    resources.add(String.join(",", parts));
                } else {
                    resources.add(line);
                }
            }
        }

        // overwrite the file with updated resource list
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESOURCE_FILE))) {
            for (String res : resources) {
                writer.write(res);
                writer.newLine();
            }
        }
    }
}
