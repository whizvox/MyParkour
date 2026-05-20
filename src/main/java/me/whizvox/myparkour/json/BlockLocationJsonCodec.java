package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.util.BlockLocation;

import java.lang.reflect.Type;
import java.util.UUID;

public class BlockLocationJsonCodec implements JsonSerializer<BlockLocation>, JsonDeserializer<BlockLocation> {

    private BlockLocationJsonCodec() {}

    @Override
    public BlockLocation deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new BlockLocation(
            obj.get("x").getAsInt(),
            obj.get("y").getAsInt(),
            obj.get("z").getAsInt(),
            context.deserialize(obj.get("world"), UUID.class)
        );
    }

    @Override
    public JsonElement serialize(BlockLocation loc, Type type, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.add("world", context.serialize(loc.worldId()));
        obj.addProperty("x", loc.x());
        obj.addProperty("y", loc.y());
        obj.addProperty("z", loc.z());
        return obj;
    }

    public static final BlockLocationJsonCodec INSTANCE = new BlockLocationJsonCodec();

}
