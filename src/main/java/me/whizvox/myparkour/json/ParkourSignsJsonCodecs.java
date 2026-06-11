package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.sign.*;

import java.lang.reflect.Type;

public class ParkourSignsJsonCodecs {

    public static final JsonCodec<ParkourCourseTimesSign> COURSE_TIMES = new JsonCodec<>() {
        @Override
        public ParkourCourseTimesSign deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            int courseId = root.get("course").getAsInt();
            Course course = MyParkour.inst().getCourses().get(courseId)
                .orElseThrow(() -> new JsonParseException("Unknown course ID: " + courseId));
            return new ParkourCourseTimesSign(course);
        }
        @Override
        public JsonElement serialize(ParkourCourseTimesSign src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "courseTimes");
            root.addProperty("course", src.course().id());
            return root;
        }
    };

    public static final JsonCodec<ParkourSelfTimesSign> SELF_TIMES = new JsonCodec<ParkourSelfTimesSign>() {
        @Override
        public ParkourSelfTimesSign deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ParkourSelfTimesSign();
        }
        @Override
        public JsonElement serialize(ParkourSelfTimesSign src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "selfTimes");
            return root;
        }
    };

    public static final JsonCodec<ParkourExitSign> EXIT = new JsonCodec<>() {
        @Override
        public ParkourExitSign deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new ParkourExitSign();
        }

        @Override
        public JsonElement serialize(ParkourExitSign src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "exit");
            return root;
        }
    };

    public static final JsonCodec<ParkourRunSign> RUN = new JsonCodec<>() {
        @Override
        public ParkourRunSign deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            int courseId = root.get("course").getAsInt();
            Course course = MyParkour.inst().getCourses().get(courseId)
                .orElseThrow(() -> new JsonParseException("Unknown course ID: " + courseId));
            return new ParkourRunSign(course);
        }
        @Override
        public JsonElement serialize(ParkourRunSign src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "run");
            root.addProperty("course", src.course().id());
            return root;
        }
    };

    public static final JsonCodec<ParkourSign> GENERIC = new JsonCodec<>() {
        @Override
        public ParkourSign deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            String type = root.get("type").getAsString();
            return switch (type) {
                case "exit" -> context.deserialize(json, ParkourExitSign.class);
                case "run" -> context.deserialize(json, ParkourRunSign.class);
                case "courseTimes" -> context.deserialize(json, ParkourCourseTimesSign.class);
                case "selfTimes" -> context.deserialize(json, ParkourSelfTimesSign.class);
                default -> throw new JsonParseException("Unknown parkour sign type: " + type);
            };
        }
        @Override
        public JsonElement serialize(ParkourSign src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src, src.getClass());
        }
    };

}
