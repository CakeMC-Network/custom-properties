package net.cakemc.format.serial

import net.cakemc.format.CakeProperties
import java.io.IOException
import java.util.*

class UUIDObjectSerializer : ObjectTranslation<UUID>() {

    @Throws(IOException::class)
    override fun deserialize(key: String, properties: CakeProperties): UUID {
        val most = properties.getLong("${key}.most")!!
        val least = properties.getLong("${key}.least")!!
        return UUID(most, least)
    }

    @Throws(IOException::class)
    override fun serialize(key: String, properties: CakeProperties, `object`: UUID) {
        properties.appendLong("${key}.most", `object`.mostSignificantBits)
        properties.appendLong("${key}.least", `object`.leastSignificantBits)
    }

}
