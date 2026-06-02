package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.BlockCheckpoint;
import me.whizvox.myparkour.course.BoxCheckpoint;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.SplitCheckpoint;
import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.ImmutableBoundingBox;
import me.whizvox.myparkour.util.ImmutableLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckpointJsonCodecs {

    public static final JsonCodec<Checkpoint> GENERIC = new JsonCodec<>() {
        @Override
        public Checkpoint deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            String checkpointType = root.get("type").getAsString();
            return switch (checkpointType) {
                case "block" -> context.deserialize(json, BlockCheckpoint.class);
                case "box" -> context.deserialize(json, BoxCheckpoint.class);
                case "split" -> context.deserialize(json, SplitCheckpoint.class);
                default -> throw new JsonParseException("Invalid checkpoint type: " + checkpointType);
            };
        }
        @Override
        public JsonElement serialize(Checkpoint src, Type type, JsonSerializationContext context) {
            return switch (src) {
                case BlockCheckpoint block -> context.serialize(block, BlockCheckpoint.class);
                case BoxCheckpoint box -> context.serialize(box, BoxCheckpoint.class);
                case SplitCheckpoint split -> context.serialize(split, SplitCheckpoint.class);
            };
        }
    };

    public static final JsonCodec<BlockCheckpoint> BLOCK = new JsonCodec<>() {
        @Override
        public BlockCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            return new BlockCheckpoint(
                context.deserialize(obj.get("location"), BlockLocation.class),
                context.deserialize(obj.get("respawn"), ImmutableLocation.class)
            );
        }
        @Override
        public JsonElement serialize(BlockCheckpoint src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "block");
            root.add("location", context.serialize(src.location()));
            root.add("respawn", context.serialize(src.respawn()));
            return root;
        }
    };

    public static final JsonCodec<BoxCheckpoint> BOX = new JsonCodec<>() {
        @Override
        public BoxCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            return new BoxCheckpoint(
                context.deserialize(root.get("world"), UUID.class),
                context.deserialize(root.get("box"), ImmutableBoundingBox.class),
                context.deserialize(root.get("respawn"), ImmutableLocation.class)
            );
        }
        @Override
        public JsonElement serialize(BoxCheckpoint src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "box");
            root.add("respawn", context.serialize(src.respawn()));
            root.add("world", context.serialize(src.worldId()));
            root.add("box", context.serialize(src.box()));
            return root;
        }
    };

    public static final JsonCodec<SplitCheckpoint> SPLIT = new JsonCodec<>() {
        @Override
        public SplitCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject root = json.getAsJsonObject();
            JsonArray areasArr = root.getAsJsonArray("checkpoints");
            List<Checkpoint> checkpoints = new ArrayList<>(areasArr.size());
            areasArr.forEach(localElem -> {
                JsonObject localObj = localElem.getAsJsonObject();
                String type = localObj.get("type").getAsString();
                Checkpoint checkpoint = switch (type) {
                    case "block" -> context.deserialize(localElem, BlockCheckpoint.class);
                    case "box" -> context.deserialize(localElem, BoxCheckpoint.class);
                    default -> throw new JsonParseException("Invalid split checkpoint type: " + type);
                };
                checkpoints.add(checkpoint);
            });
            return new SplitCheckpoint(checkpoints);
        }
        @Override
        public JsonElement serialize(SplitCheckpoint src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "split");
            JsonArray checkpointsArr = new JsonArray(src.checkpoints().size());
            src.checkpoints().forEach(checkpoint -> {
                JsonElement localCheckpointObj;
                if (checkpoint instanceof BlockCheckpoint block) {
                    localCheckpointObj = context.serialize(block);
                } else if (checkpoint instanceof BoxCheckpoint box) {
                    localCheckpointObj = context.serialize(box);
                } else {
                    throw new IllegalStateException("Invalid checkpoint type in split checkpoint: " + checkpoint.getClass() + "(" + checkpoint + ")");
                }
                checkpointsArr.add(localCheckpointObj);
            });
            root.add("checkpoints", checkpointsArr);
            return root;
        }
    };

}
