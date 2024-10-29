package net.cakemc.format.serial;

import net.cakemc.format.CakeProperties;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CakeObjectSerializer class provides functionality to serialize and deserialize objects
 * into a properties file format. Unsupported types are serialized using Base64 encoding.
 * <p>
 * This class utilizes reflection to handle object fields and supports primitive types as well as strings.
 * Complex objects are serialized to and deserialized from Base64 encoded strings.
 */
public class CakeObjectSerializer {

    private final Map<Class<?>, ObjectTranslation<?>> translatorMap;

    public CakeObjectSerializer() {
        translatorMap = new ConcurrentHashMap<>();

        this.register(UUID.class, new UUIDObjectSerializer());
        this.register(InetSocketAddress.class, new InetAddressObjectTranslation());
    }

    public <T> void register(Class<T> clazz, ObjectTranslation<T> translation) {
        translatorMap.put(clazz, translation);
    }

    /**
     * Serializes an object to a properties file. The object's fields are stored in the
     * provided {@link CakeProperties}. If a field type is unsupported, it is serialized using Base64 encoding.
     *
     * @param properties the {@link CakeProperties} object to store the serialized data
     * @param key        the base key under which the object data will be stored
     * @param object     the object to serialize
     * @param <T>        the type of the object to serialize
     * @throws IOException            if an I/O error occurs while saving properties
     * @throws IllegalAccessException if access to the object's fields is denied
     */
    @SuppressWarnings("unchecked")
    public <T> void serialize(CakeProperties properties, String key, T object) throws IOException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        if (translatorMap.containsKey(clazz)) {
            ObjectTranslation<T> translation = (ObjectTranslation<T>) translatorMap.get(clazz);
            translation.serialize(key, properties, object);
            return;
        }

        for (Field field : fields) {
            field.setAccessible(true); // Allows access to private fields
            String fieldName = field.getName();
            Object value = field.get(object);

            if (value != null) {
                serializeField(properties, "%s.%s".formatted(key, fieldName), value);
            }
        }

        properties.saveProperties();
    }

    /**
     * Deserializes an object from a properties file using its class and key.
     * This method creates a new instance of the object and populates its fields
     * using the data stored in the provided {@link CakeProperties}.
     *
     * @param properties the {@link CakeProperties} object containing the serialized data
     * @param key        the base key under which the object data is stored
     * @param clazz     the class of the object to deserialize
     * @param <T>        the type of the object to deserialize
     * @return the deserialized object with fields populated from properties
     * @throws IllegalAccessException   if access to the object's fields is denied
     * @throws IOException              if an I/O error occurs during deserialization
     * @throws ClassNotFoundException   if the class of a serialized object cannot be found
     * @throws NoSuchMethodException    if the object constructor is not available
     * @throws InvocationTargetException if the object instantiation fails
     * @throws InstantiationException   if the object cannot be instantiated
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(CakeProperties properties, String key, Class<T> clazz)
            throws IllegalAccessException, IOException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, InstantiationException {

        if (this.translatorMap.containsKey(clazz)) {
            return (T) this.translatorMap.get(clazz)
                    .deserialize(key, properties);
        }
        return deserialize(properties, key, clazz.getConstructor().newInstance());
    }

    /**
     * Deserializes an existing object from a properties file.
     * The object is populated by reading its fields from the {@link CakeProperties} provided.
     *
     * @param properties the {@link CakeProperties} object containing the serialized data
     * @param key        the base key under which the object data is stored
     * @param object     the object to deserialize into
     * @param <T>        the type of the object to deserialize
     * @return the deserialized object with fields populated from properties
     * @throws IllegalAccessException   if access to the object's fields is denied
     * @throws IOException              if an I/O error occurs during deserialization
     * @throws ClassNotFoundException   if the class of a serialized object cannot be found
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(CakeProperties properties, String key, T object)
            throws IllegalAccessException, IOException, ClassNotFoundException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        if (this.translatorMap.containsKey(clazz)) {
            return (T) this.translatorMap.get(clazz)
                    .deserialize(key, properties);
        }

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String value = properties.getString("%s.%s".formatted(key, fieldName));

            if (value != null) {
                deserializeField(field, object, value);
            }
        }

        return object;
    }

    /**
     * Serializes a single field to the {@link CakeProperties}. If the field type is unsupported,
     * it is serialized using Base64 encoding.
     *
     * @param properties the {@link CakeProperties} object to store the serialized field data
     * @param fieldName  the name of the field to serialize
     * @param value      the value of the field to serialize
     * @throws IOException if an I/O error occurs during serialization
     */
    private void serializeField(CakeProperties properties, String fieldName, Object value) throws IOException {
        switch (value) {
            case Integer i -> properties.appendInt(fieldName, i);
            case Long l -> properties.append(fieldName, l);
            case Double v -> properties.appendDouble(fieldName, v);
            case Boolean b -> properties.appendBoolean(fieldName, b);
            case Character c -> properties.appendChar(fieldName, c);
            case String s -> properties.appendString(fieldName, s);
            case null, default -> {
                // For unsupported types, serialize to Base64
                String serializedValue = serializeToBase64(value);
                properties.append(fieldName, serializedValue);
            }
        }
    }

    /**
     * Deserializes a single field from the {@link CakeProperties}.
     * If the field type is unsupported, it is deserialized from a Base64-encoded string.
     *
     * @param field  the field to set the deserialized value to
     * @param object the object to set the field value in
     * @param value  the serialized string value
     * @param <T>    the type of the object to deserialize into
     * @throws IllegalAccessException   if access to the field is denied
     * @throws IOException              if an I/O error occurs during deserialization
     * @throws ClassNotFoundException   if the class of a serialized object cannot be found
     */
    private <T> void deserializeField(Field field, T object, String value)
            throws IllegalAccessException, IOException, ClassNotFoundException {
        Class<?> fieldType = field.getType();

        if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            field.set(object, Integer.parseInt(value));
        } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
            field.set(object, Long.parseLong(value));
        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            field.set(object, Double.parseDouble(value));
        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            field.set(object, Boolean.parseBoolean(value));
        } else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
            field.set(object, value.charAt(0));
        } else if (fieldType.equals(String.class)) {
            field.set(object, value);
        } else {
            // Deserialize from Base64 for unsupported types
            Object deserializedValue = deserializeFromBase64(value);
            field.set(object, deserializedValue);
        }
    }

    /**
     * Serializes an object to a Base64-encoded string using Java object streams.
     *
     * @param value the object to serialize
     * @return the Base64-encoded string of the serialized object
     * @throws IOException if an I/O error occurs during serialization
     */
    private String serializeToBase64(Object value) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(value);
            byte[] objectBytes = byteStream.toByteArray();
            return Base64.getEncoder().encodeToString(objectBytes);
        }
    }

    /**
     * Deserializes a Base64-encoded string back to an object using Java object streams.
     *
     * @param base64String the Base64-encoded string to deserialize
     * @return the deserialized object
     * @throws IOException              if an I/O error occurs during deserialization
     * @throws ClassNotFoundException   if the class of a serialized object cannot be found
     */
    private Object deserializeFromBase64(String base64String) throws IOException, ClassNotFoundException {
        byte[] objectBytes = Base64.getDecoder().decode(base64String);
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(objectBytes);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
            return objectStream.readObject();
        }
    }
}
