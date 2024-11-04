package net.cakemc.format.serial

import net.cakemc.format.CakeProperties
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.net.InetSocketAddress
import java.util.Base64
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * CakeObjectSerializer class provides functionality to serialize and deserialize objects
 * into a properties file format. Unsupported types are serialized using Base64 encoding.
 *
 * This class utilizes reflection to handle object fields and supports primitive types as well as strings.
 * Complex objects are serialized to and deserialized from Base64 encoded strings.
 */
class CakeObjectSerializer {

    private val translatorMap = ConcurrentHashMap<Class<*>, ObjectTranslation<*>>()

    init {
        register(UUID::class.java, UUIDObjectSerializer())
        register(InetSocketAddress::class.java, InetAddressObjectTranslation())
    }

    fun <T> register(clazz: Class<T>, translation: ObjectTranslation<T>) {
        translatorMap[clazz] = translation
    }

    /**
     * Serializes an object to a properties file. The object's fields are stored in the
     * provided [CakeProperties]. If a field type is unsupported, it is serialized using Base64 encoding.
     */
    @Throws(IOException::class, IllegalAccessException::class)
    fun <T> serialize(properties: CakeProperties, key: String, obj: T) {
        val clazz = obj!!::class.java
        val fields = clazz.declaredFields

        if (translatorMap.containsKey(clazz)) {
            val translation = translatorMap[clazz] as ObjectTranslation<T>
            translation.serialize(key, properties, obj)
            return
        }

        for (field in fields) {
            field.isAccessible = true
            val fieldName = field.name
            val value = field.get(obj)

            if (value != null) {
                serializeField(properties, "$key.$fieldName", value)
            }
        }

        properties.saveProperties()
    }

    /**
     * Deserializes an object from a properties file using its class and key.
     */
    @Throws(
        IllegalAccessException::class,
        IOException::class,
        ClassNotFoundException::class,
        NoSuchMethodException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    fun <T> deserialize(properties: CakeProperties, key: String, clazz: Class<T>): T {
        return if (translatorMap.containsKey(clazz)) {
            (translatorMap[clazz] as ObjectTranslation<T>).deserialize(key, properties)
        } else {
            deserialize(properties, key, clazz.getConstructor().newInstance())
        }
    }

    /**
     * Deserializes an existing object from a properties file.
     */
    @Throws(IllegalAccessException::class, IOException::class, ClassNotFoundException::class)
    fun <T> deserialize(properties: CakeProperties, key: String, obj: T): T {
        val clazz = obj!!::class.java
        val fields = clazz.declaredFields

        if (translatorMap.containsKey(clazz)) {
            return (translatorMap[clazz] as ObjectTranslation<T>).deserialize(key, properties)
        }

        for (field in fields) {
            field.isAccessible = true
            val fieldName = field.name
            val value = properties.getString("$key.$fieldName")

            if (value != null) {
                deserializeField(field, obj, value)
            }
        }

        return obj
    }

    /**
     * Serializes a single field to the [CakeProperties]. If the field type is unsupported,
     * it is serialized using Base64 encoding.
     */
    @Throws(IOException::class)
    private fun serializeField(properties: CakeProperties, fieldName: String, value: Any) {
        when (value) {
            is Int -> properties.appendInt(fieldName, value)
            is Long -> properties.append(fieldName, value)
            is Double -> properties.appendDouble(fieldName, value)
            is Boolean -> properties.appendBoolean(fieldName, value)
            is Char -> properties.appendChar(fieldName, value)
            is String -> properties.appendString(fieldName, value)
            else -> {
                // For unsupported types, serialize to Base64
                val serializedValue = serializeToBase64(value)
                properties.append(fieldName, serializedValue)
            }
        }
    }

    /**
     * Deserializes a single field from the [CakeProperties].
     * If the field type is unsupported, it is deserialized from a Base64-encoded string.
     */
    @Throws(IllegalAccessException::class, IOException::class, ClassNotFoundException::class)
    private fun <T> deserializeField(field: Field, obj: T, value: String) {
        val fieldType = field.type

        when (fieldType) {
            Int::class.java, Int::class.javaObjectType -> field.set(obj, value.toInt())
            Long::class.java, Long::class.javaObjectType -> field.set(obj, value.toLong())
            Double::class.java, Double::class.javaObjectType -> field.set(obj, value.toDouble())
            Boolean::class.java, Boolean::class.javaObjectType -> field.set(obj, value.toBoolean())
            Char::class.java, Char::class.javaObjectType -> field.set(obj, value[0])
            String::class.java -> field.set(obj, value)
            else -> {
                // Deserialize from Base64 for unsupported types
                val deserializedValue = deserializeFromBase64(value)
                field.set(obj, deserializedValue)
            }
        }
    }

    /**
     * Serializes an object to a Base64-encoded string using Java object streams.
     */
    @Throws(IOException::class)
    private fun serializeToBase64(value: Any): String {
        ByteArrayOutputStream().use { byteStream ->
            ObjectOutputStream(byteStream).use { objectStream ->
                objectStream.writeObject(value)
            }
            return Base64.getEncoder().encodeToString(byteStream.toByteArray())
        }
    }

    /**
     * Deserializes a Base64-encoded string back to an object using Java object streams.
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun deserializeFromBase64(base64String: String): Any {
        val objectBytes = Base64.getDecoder().decode(base64String)
        ByteArrayInputStream(objectBytes).use { byteStream ->
            ObjectInputStream(byteStream).use { objectStream ->
                return objectStream.readObject()
            }
        }
    }
}
