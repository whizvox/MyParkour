package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.ExitGameMode;

import java.lang.reflect.Type;

public class ExitGameModeJsonCodec implements JsonCodec<ExitGameMode> {

    private ExitGameModeJsonCodec() {}

    @Override
    public ExitGameMode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String repr = json.getAsString();
        ExitGameMode gm = ExitGameMode.MAP.get(repr);
        if (gm != null) {
            return gm;
        }
        throw new JsonParseException("Invalid exit gamemode: " + repr);
    }

    @Override
    public JsonElement serialize(ExitGameMode src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.repr);
    }

    public static final ExitGameModeJsonCodec INSTANCE = new ExitGameModeJsonCodec();

}
