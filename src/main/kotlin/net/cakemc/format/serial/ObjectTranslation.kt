package net.cakemc.format.serial

import net.cakemc.format.CakeProperties
import java.io.IOException

/**
 * An abstract class that defines the serialization and deserialization logic for a specific object type.
 * Subclasses should implement the methods to handle converting objects to and from their serialized
 * form in [CakeProperties].
 *
 * @param <T> the type of object this translation handles
</T> */
abstract class ObjectTranslation<T> {
    /**
     * Deserializes an object of type `T` from the properties file using the provided key.
     *
     * @param key        the property key associated with the object
     * @param properties the [CakeProperties] instance from which the object will be deserialized
     * @return the deserialized object of type `T`
     * @throws IOException if an I/O error occurs during deserialization
     */
    @Throws(IOException::class)
    abstract fun deserialize(key: String, properties: CakeProperties): T

    /**
     * Serializes the given object of type `T` and stores it in the properties file under the provided key.
     *
     * @param key        the property key under which the object will be stored
     * @param properties the [CakeProperties] instance where the object will be serialized
     * @param object     the object of type `T` to be serialized
     * @throws IOException if an I/O error occurs during serialization
     */
    @Throws(IOException::class)
    abstract fun serialize(key: String, properties: CakeProperties, `object`: T)
}
