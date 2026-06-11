package me.whizvox.myparkour.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.CourseFlag;
import me.whizvox.myparkour.course.edit.EditableCourse;
import me.whizvox.myparkour.util.ImmutableLocation;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EditableCourseJsonCodec implements JsonSerializer<EditableCourse>, JsonDeserializer<EditableCourse> {

    @Override
    public EditableCourse deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        EditableCourse course = new EditableCourse();
        course.setId(obj.get("id").getAsInt());
        if (obj.has("name")) {
            course.setName(obj.get("name").getAsString());
        }
        if (obj.has("displayName")) {
            course.setDisplayName(JSONComponentSerializer.json().deserialize(obj.get("displayName").getAsString()));
        }
        if (obj.has("checkpoints")) {
            List<Checkpoint> checkpoints = context.deserialize(obj.get("checkpoints"), new TypeToken<ArrayList<Checkpoint>>(){}.getType());
            checkpoints.forEach(course::addCheckpoint);
        }
        if (obj.has("start")) {
            course.setStart(context.deserialize(obj.get("start"), ImmutableLocation.class));
        }
        if (obj.has("flags")) {
            List<CourseFlag> flags = context.deserialize(obj.get("flags"), new TypeToken<ArrayList<CourseFlag>>(){}.getType());
            flags.forEach(course::addFlag);
        }
        if (obj.has("exit")) {
            course.setExit(context.deserialize(obj.get("exit"), ImmutableLocation.class));
        }
        course.setOpen(obj.get("open").getAsBoolean());
        return course;
    }

    @Override
    public JsonElement serialize(EditableCourse src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("id", src.getId());
        if (src.getName() != null) {
            root.addProperty("name", src.getName());
        }
        if (src.getDisplayName() != null) {
            root.addProperty("displayName", JSONComponentSerializer.json().serialize(src.getDisplayName()));
        }
        if (!src.getCheckpoints().isEmpty()) {
            root.add("checkpoints", context.serialize(src.getCheckpoints()));
        }
        if (src.getStart() != null) {
            root.add("start", context.serialize(src.getStart()));
        }
        if (!src.getFlags().isEmpty()) {
            root.add("flags", context.serialize(src.getFlags()));
        }
        if (src.getExit() != null) {
            root.add("exit", context.serialize(src.getExit()));
        }
        root.addProperty("open", src.isOpen());
        return root;
    }

}
