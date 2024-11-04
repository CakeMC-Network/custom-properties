package net.cakemc.format.serial;

import net.cakemc.format.CakeProperties;

import java.io.IOException;

/**
 * An abstract class that defines the serialization and deserialization logic for a specific object type.
 * Subclasses should implement the methods to handle converting objects to and from their serialized
 * form in {@link CakeProperties}.
 *
 * @param <T> the type of object this translation handles
 */
public abstract class ObjectTranslation<T> {

    /**
     * Deserializes an object of type {@code T} from the properties file using the provided key.
     *
     * @param key        the property key associated with the object
     * @param properties the {@link CakeProperties} instance from which the object will be deserialized
     * @return the deserialized object of type {@code T}
     * @throws IOException if an I/O error occurs during deserialization
     */
    public abstract T deserialize(String key, CakeProperties properties) throws IOException;

    /**
     * Serializes the given object of type {@code T} and stores it in the properties file under the provided key.
     *
     * @param key        the property key under which the object will be stored
     * @param properties the {@link CakeProperties} instance where the object will be serialized
     * @param object     the object of type {@code T} to be serialized
     * @throws IOException if an I/O error occurs during serialization
     */
    public abstract void serialize(String key, CakeProperties properties, T object) throws IOException;
}
