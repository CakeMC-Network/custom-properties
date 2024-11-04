package net.cakemc.format.serial

import net.cakemc.format.CakeProperties
import java.io.IOException
import java.net.InetSocketAddress

class InetAddressObjectTranslation : ObjectTranslation<InetSocketAddress>() {

    @Throws(IOException::class)
    override fun deserialize(key: String, properties: CakeProperties): InetSocketAddress {
        val host = properties.getString("$key.host")
        val port = properties.getInt("$key.port")
        return port?.let { InetSocketAddress(host, it) }!!
    }

    @Throws(IOException::class)
    override fun serialize(key: String, properties: CakeProperties, obj: InetSocketAddress) {
        properties.appendString("$key.host", obj.hostName)
        properties.appendInt("$key.port", obj.port)
    }

}
