package me.whizvox.myparkour.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDJsonCodec implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    private UUIDJsonCodec() {}

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return UUID.fromString(json.getAsString());
    }

    @Override
    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(uuid.toString());
    }

    public static final UUIDJsonCodec INSTANCE = new UUIDJsonCodec();

}
