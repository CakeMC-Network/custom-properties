package net.cakemc.format;

import net.cakemc.format.serial.CakeObjectSerializer;
import net.cakemc.format.serial.ObjectTranslation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * The type Cake properties.
 */
public class CakeProperties {

    private final Path filePath;
    private final Map<String, String> properties = new LinkedHashMap<>();

    private final CakeObjectSerializer serializer;

    /**
     * Instantiates a new Cake properties.
     *
     * @param filePath the file path
     */
    public CakeProperties(String filePath) {
        this.filePath = Paths.get(filePath);
        this.serializer = new CakeObjectSerializer();

        if (Files.exists(this.filePath)) {
            loadProperties();
        }
    }

    /**
     * Register.
     *
     * @param <T>         the type parameter
     * @param clazz       the clazz
     * @param translation the translation
     */
    public <T> void register(Class<T> clazz, ObjectTranslation<T> translation) {
        serializer.register(clazz, translation);
    }

    /**
     * Load properties.
     */
    public void loadProperties() {
        try {
            this.loadProperties0();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load properties 0.
     *
     * @throws IOException the io exception
     */
    public void loadProperties0() throws IOException {
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
     * Append.
     *
     * @param <T>    the type parameter
     * @param key    the key
     * @param object the object
     */
    public <T> void append(String key, T object) {
        try {
            append0(key, object);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Append 0.
     *
     * @param <T>    the type parameter
     * @param key    the key
     * @param object the object
     * @throws IOException            the io exception
     * @throws IllegalAccessException the illegal access exception
     */
    public <T> void append0(String key, T object) throws IOException, IllegalAccessException {
        this.serializer.serialize(this, key, object);
    }

    /**
     * Append string.
     *
     * @param key   the key
     * @param value the value
     */
    public void appendString(String key, String value) {
        try {
            appendString0(key, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Append string 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    public void appendString0(String key, String value) throws IOException {
        if (!properties.containsKey(key)) {
            properties.put(key, value);
            saveProperties();
        } else {
            System.out.println("Key '" + key + "' already exists with value: " + properties.get(key));
        }
    }

    /**
     * Append int.
     *
     * @param key   the key
     * @param value the value
     */
    public void appendInt(String key, int value) {
        try {
            appendInt0(key, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Append int 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    public void appendInt0(String key, int value) throws IOException {
        appendString(key, Integer.toString(value));
    }

    /**
     * Append long.
     *
     * @param key   the key
     * @param value the value
     */
    public void appendLong(String key, long value) {
        try {
            appendLong0(key, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Append long 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    public void appendLong0(String key, long value) throws IOException {
        appendString(key, Long.toString(value));
    }

    /**
     * Append double.
     *
     * @param key   the key
     * @param value the value
     */
    public void appendDouble(String key, double value) {
        try {
            appendDouble0(key, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Append double 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    public void appendDouble0(String key, double value) throws IOException {
        appendString(key, Double.toString(value));
    }

    /**
     * Append boolean.
     *
     * @param key   the key
     * @param value the value
     */
    public void appendBoolean(String key, boolean value) {
        try {
            appendBoolean0(key, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Append boolean 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    public void appendBoolean0(String key, boolean value) throws IOException {
        appendString(key, Boolean.toString(value));
    }

    /**
     * Append char.
     *
     * @param key   the key
     * @param value the value
     */
    public void appendChar(String key, char value) {
        try {
            appendChar0(key, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Append char 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    public void appendChar0(String key, char value) throws IOException {
        appendString(key, Character.toString(value));
    }

    /**
     * Get t.
     *
     * @param <T>  the type parameter
     * @param key  the key
     * @param type the type
     * @return the t
     */
    public <T> T get(String key, Class<T> type) {
        try {
            return this.serializer.deserialize(this, key, type);
        } catch (Throwable throwable) {
            return null;
        }
    }

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     */
    public String getString(String key) {
        return properties.get(key);
    }

    /**
     * Gets int.
     *
     * @param key the key
     * @return the int
     */
    public Integer getInt(String key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets long.
     *
     * @param key the key
     * @return the long
     */
    public Long getLong(String key) {
        try {
            return Long.parseLong(getString(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets double.
     *
     * @param key the key
     * @return the double
     */
    public Double getDouble(String key) {
        try {
            return Double.parseDouble(getString(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public Boolean getBoolean(String key) {
        String value = getString(key);
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }

    /**
     * Gets char.
     *
     * @param key the key
     * @return the char
     */
    public Character getChar(String key) {
        String value = getString(key);
        return (value != null && value.length() == 1) ? value.charAt(0) : null;
    }

    /**
     * Gets or create.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the or create
     */
    public String getOrCreate(String key, String defaultValue) {
        try {
            return getOrCreate0(key, defaultValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets or create 0.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the or create 0
     * @throws IOException the io exception
     */
    public String getOrCreate0(String key, String defaultValue) throws IOException {
        if (!properties.containsKey(key)) {
            properties.put(key, defaultValue);
            saveProperties();
        }
        return properties.get(key);
    }

    /**
     * Save properties.
     */
    public void saveProperties() {
        try {
            this.saveProperties0();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save properties 0.
     *
     * @throws IOException the io exception
     */
    public void saveProperties0() throws IOException {
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

        Files.write(filePath, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
