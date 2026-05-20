package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.util.ImmutableLocation;

import java.lang.reflect.Type;
import java.util.UUID;

public class ImmutableLocationJsonCodec implements JsonSerializer<ImmutableLocation>, JsonDeserializer<ImmutableLocation> {

    private ImmutableLocationJsonCodec() {}

    @Override
    public ImmutableLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new ImmutableLocation(
            UUID.fromString(obj.get("world").getAsString()),
            obj.get("x").getAsDouble(),
            obj.get("y").getAsDouble(),
            obj.get("z").getAsDouble(),
            obj.get("pitch").getAsFloat(),
            obj.get("yaw").getAsFloat()
        );
    }

    @Override
    public JsonElement serialize(ImmutableLocation loc, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("world", loc.worldId().toString());
        obj.addProperty("x", loc.x());
        obj.addProperty("y", loc.y());
        obj.addProperty("z", loc.z());
        obj.addProperty("yaw", loc.yaw());
        obj.addProperty("pitch", loc.pitch());
        return obj;
    }

    public static final ImmutableLocationJsonCodec INSTANCE = new ImmutableLocationJsonCodec();

}
