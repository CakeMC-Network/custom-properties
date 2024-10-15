package net.cakemc.format;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * CakeProperties class to manage properties in a file with automatic appending, grouping, and retrieving key-value pairs.
 * It formats the file with nested grouping and handles file operations using NIO.
 */
public class CakeProperties {

    private final Path filePath;
    private final Map<String, String> properties = new LinkedHashMap<>();

    /**
     * Constructs a CakeProperties instance and loads the file into memory if it exists.
     *
     * @param filePath the path to the properties file
     * @throws IOException if an I/O error occurs while loading the file
     */
    public CakeProperties(String filePath) throws IOException {
        this.filePath = Paths.get(filePath);
        if (Files.exists(this.filePath)) {
            loadProperties();
        }
    }

    /**
     * Loads properties from the file into a map with proper formatting and groups.
     *
     * @throws IOException if an I/O error occurs while reading the file
     */
    private void loadProperties() throws IOException {
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        String currentGroup = null;

        for (String line : lines) {
            if (line.startsWith("#"))
                continue;

            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (!line.contains("=")) {
                currentGroup = line;
            } else {
                String[] parts = line.split("=", 2);
                if (parts.length == 2 && currentGroup != null) {
                    String key = currentGroup + "." + parts[0].trim();
                    String value = parts[1].trim().replace("\"", "");
                    properties.put(key, value);
                }
            }
        }
    }

    /**
     * Appends a key-value pair to the properties if it does not already exist.
     *
     * @param key   the key to append
     * @param value the value to append
     * @throws IOException if an I/O error occurs while saving the properties
     */
    public void append(String key, String value) throws IOException {
        if (!properties.containsKey(key)) {
            properties.put(key, value);
            saveProperties();
        } else {
            System.out.println("Key '" + key + "' already exists with value: " + properties.get(key));
        }
    }

    /**
     * Retrieves the value for the specified key.
     *
     * @param key the key to retrieve
     * @return the value associated with the key, or null if the key does not exist
     */
    public String get(String key) {
        return properties.get(key);
    }

    /**
     * Retrieves the value for the specified key, or creates a new key with the default value if it doesn't exist.
     *
     * @param key          the key to retrieve or create
     * @param defaultValue the default value if the key does not exist
     * @return the value associated with the key
     * @throws IOException if an I/O error occurs while saving the properties
     */
    public String getOrCreate(String key, String defaultValue) throws IOException {
        if (!properties.containsKey(key)) {
            properties.put(key, defaultValue);
            saveProperties();
        }
        return properties.get(key);
    }

    /**
     * Saves the properties back to the file with fancy formatting.
     * The properties are grouped by their base group, and each subkey is indented.
     *
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public void saveProperties() throws IOException {
        Map<String, Map<String, String>> groupedProperties = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String fullKey = entry.getKey();
            String[] keyParts = fullKey.split("\\.");
            String group = keyParts[0] + "." + keyParts[1]; // e.g., test.message
            String subKey = keyParts[2]; // e.g., first, second
            groupedProperties.computeIfAbsent(group, k -> new LinkedHashMap<>()).put(subKey, entry.getValue());
        }

        List<String> lines = new ArrayList<>();

        for (Map.Entry<String, Map<String, String>> groupEntry : groupedProperties.entrySet()) {
            lines.add(groupEntry.getKey());
            for (Map.Entry<String, String> subEntry : groupEntry.getValue().entrySet()) {
                lines.add("    " + subEntry.getKey() + " = \"" + subEntry.getValue() + "\"");
            }
            lines.add(""); // Extra line between groups
        }

        Files.write(filePath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
