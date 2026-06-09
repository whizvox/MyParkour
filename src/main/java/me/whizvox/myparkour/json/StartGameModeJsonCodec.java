package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.StartGameMode;

import java.lang.reflect.Type;

public class StartGameModeJsonCodec implements JsonCodec<StartGameMode> {

    private StartGameModeJsonCodec() {}

    @Override
    public StartGameMode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String repr = json.getAsString();
        StartGameMode gm = StartGameMode.MAP.get(repr);
        if (gm != null) {
            return gm;
        }
        throw new JsonParseException("Invalid start gamemode: " + repr);
    }

    @Override
    public JsonElement serialize(StartGameMode src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.repr);
    }

    public static final StartGameModeJsonCodec INSTANCE = new StartGameModeJsonCodec();

}
