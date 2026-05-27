package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.BlockCheckpoint;
import me.whizvox.myparkour.course.BoxCheckpoint;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.SplitCheckpoint;
import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.ImmutableBoundingBox;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckpointJsonCodec implements JsonSerializer<Checkpoint>, JsonDeserializer<Checkpoint> {

    private CheckpointJsonCodec() {
    }

    @Override
    public Checkpoint deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        String checkpointType = root.get("type").getAsString();
        return switch (checkpointType) {
            case "block" -> new BlockCheckpoint(context.deserialize(root.get("location"), BlockLocation.class));
            case "box" -> new BoxCheckpoint(context.deserialize(root.get("world"), UUID.class),
                context.deserialize(root.get("box"), ImmutableBoundingBox.class));
            case "split" -> {
                JsonArray areasArr = root.getAsJsonArray("checkpoints");
                List<Checkpoint> checkpoints = new ArrayList<>(areasArr.size());
                areasArr.forEach(cpElem -> {
                    JsonObject cpObj = cpElem.getAsJsonObject();
                    String cpType = cpObj.get("type").getAsString();
                    switch (cpType) {
                        case "block" -> {
                            BlockLocation loc = context.deserialize(cpObj.get("block"), BlockLocation.class);
                            checkpoints.add(new BlockCheckpoint(loc));
                        }
                        case "box" -> {
                            ImmutableBoundingBox box = context.deserialize(cpObj.get("box"), ImmutableBoundingBox.class);
                            UUID worldId = context.deserialize(cpObj.get("world"), UUID.class);
                            checkpoints.add(new BoxCheckpoint(box, worldId));
                        }
                        default -> throw new JsonParseException("Unknown split checkpoint type: " + cpType);
                    }
                });
                yield new SplitCheckpoint(checkpoints);
            }
            default -> throw new JsonParseException("Invalid checkpoint type: " + checkpointType);
        };
    }

    @Override
    public JsonElement serialize(Checkpoint src, Type type, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        switch (src) {
            case BlockCheckpoint block -> {
                root.addProperty("type", "block");
                root.add("location", context.serialize(block.location()));
            }
            case BoxCheckpoint box -> {
                root.addProperty("type", "box");
                root.add("world", context.serialize(box.worldId()));
                root.add("box", context.serialize(box.box()));
            }
            case SplitCheckpoint split -> {
                root.addProperty("type", "split");
                JsonArray checkpointsArr = new JsonArray(split.checkpoints().size());
                split.checkpoints().forEach(checkpoint -> {
                    JsonObject areaObj = new JsonObject();
                    switch (checkpoint) {
                        case BlockCheckpoint block -> {
                            areaObj.addProperty("type", "block");
                            areaObj.add("location", context.serialize(block.location()));
                        }
                        case BoxCheckpoint box -> {
                            areaObj.addProperty("type", "box");
                            areaObj.add("world", context.serialize(box.worldId()));
                            areaObj.add("box", context.serialize(box.box()));
                        }
                        default -> throw new IllegalStateException("Invalid checkpoint type in split checkpoint: " + checkpoint.getClass() + "(" + checkpoint + ")");
                    }
                    checkpointsArr.add(areaObj);
                });
                root.add("checkpoints", checkpointsArr);
            }
            default -> throw new UnsupportedOperationException("Unsupported checkpoint type: " + src.getClass());
        }
        return root;
    }

    public static final CheckpointJsonCodec INSTANCE = new CheckpointJsonCodec();

}
