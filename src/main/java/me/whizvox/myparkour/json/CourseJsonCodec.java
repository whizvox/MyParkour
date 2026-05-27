package me.whizvox.myparkour.json;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.CourseFlag;
import me.whizvox.myparkour.util.ImmutableLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CourseJsonCodec implements JsonSerializer<Course>, JsonDeserializer<Course> {

    private CourseJsonCodec() {}

    @Override
    public Course deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        JsonArray checkpointsArr = root.getAsJsonArray("checkpoints");
        List<Checkpoint> checkpoints = new ArrayList<>(checkpointsArr.size());
        checkpointsArr.forEach(checkpointElem -> checkpoints.add(context.deserialize(checkpointElem, Checkpoint.class)));
        JsonArray flagsArr = root.getAsJsonArray("flags");
        Set<CourseFlag> flags = new ObjectArraySet<>(flagsArr.size());
        flagsArr.forEach(flagElem -> flags.add(context.deserialize(flagElem, CourseFlag.class)));
        return new Course(
            root.get("id").getAsInt(),
            root.get("name").getAsString(),
            root.get("displayName").getAsString(),
            context.deserialize(root.get("start"), ImmutableLocation.class),
            checkpoints,
            flags,
            context.deserialize(root.get("exit"), ImmutableLocation.class),
            root.get("open").getAsBoolean()
        );
    }

    @Override
    public JsonElement serialize(Course src, Type type, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("id", src.id());
        root.addProperty("name", src.name());
        root.addProperty("displayName", src.displayName());
        JsonArray checkpointsArr = new JsonArray(src.checkpoints().size());
        src.checkpoints().forEach(checkpoint -> checkpointsArr.add(context.serialize(checkpoint, Checkpoint.class)));
        root.add("checkpoints", checkpointsArr);
        root.add("start", context.serialize(src.start()));
        JsonArray flagsArr = new JsonArray(src.flags().size());
        src.flags().forEach(flag -> flagsArr.add(context.serialize(flag)));
        root.add("flags", flagsArr);
        root.add("exit", context.serialize(src.exit()));
        root.addProperty("open", src.open());
        return root;
    }

    public static final CourseJsonCodec INSTANCE = new CourseJsonCodec();

}
