package net.cakemc.format

import net.cakemc.format.serial.CakeObjectSerializer
import net.cakemc.format.serial.ObjectTranslation
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

/**
 * The type Cake properties.
 */
class CakeProperties(filePath: String) {
    private val filePath: Path = Paths.get(filePath)
    private val properties: MutableMap<String, String> = LinkedHashMap()

    private val serializer = CakeObjectSerializer()

    /**
     * Instantiates a new Cake properties.
     *
     * @param filePath the file path
     */
    init {
        if (Files.exists(this.filePath)) {
            loadProperties()
        }
    }

    fun exists(): Boolean {
        return Files.exists(this.filePath)
    }

    /**
     * Register.
     *
     * @param <T>         the type parameter
     * @param clazz       the clazz
     * @param translation the translation
    </T> */
    fun <T> register(clazz: Class<T>, translation: ObjectTranslation<T>) {
        serializer.register(clazz, translation)
    }

    /**
     * Load properties.
     */
    fun loadProperties() {
        try {
            this.loadProperties0()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Load properties 0.
     *
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun loadProperties0() {
        val lines = Files.readAllLines(filePath, StandardCharsets.UTF_8)
        var currentGroup: String? = null

        for (line in lines) {
            var line = line
            if (line.startsWith("#")) continue

            line = line.trim { it <= ' ' }
            if (line.isEmpty()) {
                continue
            }
            if (!line.contains("=")) {
                currentGroup = line
            } else {
                val parts = line.split("=".toRegex(), limit = 2).toTypedArray()
                if (parts.size == 2 && currentGroup != null) {
                    val key = currentGroup + "." + parts[0].trim { it <= ' ' }
                    val value = parts[1].trim { it <= ' ' }.replace("\"", "")
                    properties[key] = value
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
    </T> */
    fun <T> append(key: String, `object`: T) {
        try {
            append0(key, `object`)
        } catch (throwable: Throwable) {
            throw RuntimeException(throwable)
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
    </T> */
    @Throws(IOException::class, IllegalAccessException::class)
    fun <T> append0(key: String, `object`: T) {
        serializer.serialize(this, key, `object`)
    }

    /**
     * Append string.
     *
     * @param key   the key
     * @param value the value
     */
    fun appendString(key: String, value: String) {
        try {
            appendString0(key, value)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Append string 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun appendString0(key: String, value: String) {
        if (!properties.containsKey(key)) {
            properties[key] = value
            saveProperties()
        }
    }

    /**
     * Append int.
     *
     * @param key   the key
     * @param value the value
     */
    fun appendInt(key: String, value: Int) {
        try {
            appendInt0(key, value)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Append int 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun appendInt0(key: String, value: Int) {
        appendString(key, value.toString())
    }

    /**
     * Append long.
     *
     * @param key   the key
     * @param value the value
     */
    fun appendLong(key: String, value: Long) {
        try {
            appendLong0(key, value)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Append long 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun appendLong0(key: String, value: Long) {
        appendString(key, value.toString())
    }

    /**
     * Append double.
     *
     * @param key   the key
     * @param value the value
     */
    fun appendDouble(key: String, value: Double) {
        try {
            appendDouble0(key, value)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Append double 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun appendDouble0(key: String, value: Double) {
        appendString(key, value.toString())
    }

    /**
     * Append boolean.
     *
     * @param key   the key
     * @param value the value
     */
    fun appendBoolean(key: String, value: Boolean) {
        try {
            appendBoolean0(key, value)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Append boolean 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun appendBoolean0(key: String, value: Boolean) {
        appendString(key, value.toString())
    }

    /**
     * Append char.
     *
     * @param key   the key
     * @param value the value
     */
    fun appendChar(key: String, value: Char) {
        try {
            appendChar0(key, value)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Append char 0.
     *
     * @param key   the key
     * @param value the value
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun appendChar0(key: String, value: Char) {
        appendString(key, value.toString())
    }

    /**
     * Get t.
     *
     * @param <T>  the type parameter
     * @param key  the key
     * @param type the type
     * @return the t
    </T> */
    fun <T> get(key: String, type: Class<T>): T? {
        return try {
            serializer.deserialize(this, key, type)
        } catch (throwable: Throwable) {
            null
        }
    }

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     */
    fun getString(key: String): String? {
        return properties[key]
    }

    /**
     * Gets int.
     *
     * @param key the key
     * @return the int
     */
    fun getInt(key: String): Int? {
        return try {
            getString(key)!!.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Gets long.
     *
     * @param key the key
     * @return the long
     */
    fun getLong(key: String): Long? {
        return try {
            getString(key)!!.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Gets double.
     *
     * @param key the key
     * @return the double
     */
    fun getDouble(key: String): Double? {
        return try {
            getString(key)!!.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Gets boolean.
     *
     * @param key the key
     * @return the boolean
     */
    fun getBoolean(key: String): Boolean? {
        val value = getString(key)
        if ("true".equals(value, ignoreCase = true) || "false".equals(value, ignoreCase = true)) {
            return value.toBoolean()
        }
        return null
    }

    /**
     * Gets char.
     *
     * @param key the key
     * @return the char
     */
    fun getChar(key: String): Char? {
        val value = getString(key)
        return if ((value != null && value.length == 1)) value[0] else null
    }

    /**
     * Gets or create.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the or create
     */
    fun getOrCreate(key: String, defaultValue: String): String? {
        try {
            return getOrCreate0(key, defaultValue)
        } catch (e: IOException) {
            throw RuntimeException(e)
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
    @Throws(IOException::class)
    fun getOrCreate0(key: String, defaultValue: String): String? {
        if (!properties.containsKey(key)) {
            properties[key] = defaultValue
            saveProperties()
        }
        return properties[key]
    }

    /**
     * Save properties.
     */
    fun saveProperties() {
        try {
            this.saveProperties0()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Save properties 0.
     *
     * @throws IOException the io exception
     */
    @Throws(IOException::class)
    fun saveProperties0() {
        val groupedProperties: MutableMap<String, MutableMap<String, String>> = LinkedHashMap()

        for ((fullKey, value) in properties) {
            val keyParts = fullKey.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val group = keyParts[0] + "." + keyParts[1] // e.g., test.message
            val subKey = keyParts[2] // e.g., first, second
            groupedProperties.computeIfAbsent(group) { k: String? -> LinkedHashMap() }[subKey] =
                value
        }

        val lines: MutableList<String> = ArrayList()

        for ((key, value) in groupedProperties) {
            lines.add(key)
            for ((key1, value1) in value) {
                lines.add("    $key1 = \"$value1\"")
            }
            lines.add("") // Extra line between groups
        }

        Files.write(
            filePath, lines, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        )
    }
}
