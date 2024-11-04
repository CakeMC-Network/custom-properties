package net.cakemc.format.serial;

import net.cakemc.format.CakeProperties;

import java.io.IOException;
import java.net.InetSocketAddress;

public class InetAddressObjectTranslation extends ObjectTranslation<InetSocketAddress> {

    @Override
    public InetSocketAddress deserialize(String key, CakeProperties properties) throws IOException {
        return new InetSocketAddress(
                properties.getString("%s.%s".formatted(key, "host")),
                properties.getInt("%s.%s".formatted(key, "port"))
        );
    }

    @Override
    public void serialize(String key, CakeProperties properties, InetSocketAddress object) throws IOException {
        properties.appendString("%s.%s".formatted(key, "host"), object.getHostName());
        properties.appendInt("%s.%s".formatted(key, "port"), object.getPort());
    }

}
