package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.CourseFlag;

import java.lang.reflect.Type;

public class CourseFlagJsonCodec implements JsonSerializer<CourseFlag>, JsonDeserializer<CourseFlag> {

    private CourseFlagJsonCodec() {}

    @Override
    public CourseFlag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String flagName = json.getAsString();
        return CourseFlag.parse(flagName).orElseThrow(() -> new JsonParseException("Invalid course flag name: " + flagName));
    }

    @Override
    public JsonElement serialize(CourseFlag src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    public static final CourseFlagJsonCodec INSTANCE = new CourseFlagJsonCodec();

}
