package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.CourseFlag;

import java.lang.reflect.Type;

public class CourseFlagJsonCodec implements JsonSerializer<CourseFlag>, JsonDeserializer<CourseFlag> {

    private CourseFlagJsonCodec() {}

    @Override
    public CourseFlag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return CourseFlag.valueOf(json.getAsString());
    }

    @Override
    public JsonElement serialize(CourseFlag src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    public static final CourseFlagJsonCodec INSTANCE = new CourseFlagJsonCodec();

}
