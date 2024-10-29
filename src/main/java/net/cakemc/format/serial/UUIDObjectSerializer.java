package net.cakemc.format.serial;

import net.cakemc.format.CakeProperties;

import java.io.IOException;
import java.util.UUID;

public class UUIDObjectSerializer extends ObjectTranslation<UUID> {

    @Override
    public UUID deserialize(String key, CakeProperties properties) throws IOException {
        long most = properties.getLong("%s.%s".formatted(key, "most"));
        long least = properties.getLong("%s.%s".formatted(key, "least"));
        return new UUID(most, least);
    }

    @Override
    public void serialize(String key, CakeProperties properties, UUID object) throws IOException {
        properties.appendLong("%s.%s".formatted(key, "most"), object.getMostSignificantBits());
        properties.appendLong("%s.%s".formatted(key, "least"), object.getLeastSignificantBits());
    }

}
