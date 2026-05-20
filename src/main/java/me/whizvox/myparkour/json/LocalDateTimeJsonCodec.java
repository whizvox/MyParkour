package me.whizvox.myparkour.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeJsonCodec implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private LocalDateTimeJsonCodec() {}

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(json.getAsLong()), ZoneOffset.UTC);
    }

    @Override
    public JsonElement serialize(LocalDateTime ldt, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public static final LocalDateTimeJsonCodec INSTANCE = new LocalDateTimeJsonCodec();

}
