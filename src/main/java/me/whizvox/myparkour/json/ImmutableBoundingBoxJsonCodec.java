package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.util.ImmutableBoundingBox;

import java.lang.reflect.Type;

public class ImmutableBoundingBoxJsonCodec implements JsonSerializer<ImmutableBoundingBox>, JsonDeserializer<ImmutableBoundingBox> {

    private ImmutableBoundingBoxJsonCodec() {}

    @Override
    public ImmutableBoundingBox deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray arr = (JsonArray) jsonElement;
        return new ImmutableBoundingBox(
            arr.get(0).getAsDouble(),
            arr.get(1).getAsDouble(),
            arr.get(2).getAsDouble(),
            arr.get(3).getAsDouble(),
            arr.get(4).getAsDouble(),
            arr.get(5).getAsDouble()
        );
    }

    @Override
    public JsonElement serialize(ImmutableBoundingBox box, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonArray arr = new JsonArray(6);
        arr.add(box.x1());
        arr.add(box.y1());
        arr.add(box.z1());
        arr.add(box.x2());
        arr.add(box.y2());
        arr.add(box.z2());
        return arr;
    }

    public static final ImmutableBoundingBoxJsonCodec INSTANCE = new ImmutableBoundingBoxJsonCodec();

}
