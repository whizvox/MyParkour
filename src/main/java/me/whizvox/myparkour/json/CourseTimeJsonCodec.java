package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.leaderboard.MutableCourseTime;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.UUID;

public class CourseTimeJsonCodec implements JsonSerializer<MutableCourseTime>, JsonDeserializer<MutableCourseTime> {

    private CourseTimeJsonCodec() {}

    @Override
    public MutableCourseTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        return new MutableCourseTime(
            obj.get("id").getAsInt(),
            context.deserialize(obj.get("player"), UUID.class),
            obj.get("course").getAsInt(),
            context.deserialize(obj.get("when"), LocalDateTime.class),
            obj.get("time").getAsInt(),
            0
        );
    }

    @Override
    public JsonElement serialize(MutableCourseTime src, Type type, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", src.id());
        obj.add("player", context.serialize(src.playerId()));
        obj.addProperty("course", src.courseId());
        obj.addProperty("time", src.time());
        obj.add("when", context.serialize(src.when()));
        return obj;
    }

    public static final CourseTimeJsonCodec INSTANCE = new CourseTimeJsonCodec();

}
