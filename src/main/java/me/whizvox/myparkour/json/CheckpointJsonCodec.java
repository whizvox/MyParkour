package me.whizvox.myparkour.json;

import com.google.gson.*;
import it.unimi.dsi.fastutil.Pair;
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
        int id = root.get("id").getAsInt();
        String checkpointType = root.get("type").getAsString();
        return switch (checkpointType) {
            case "block" -> new BlockCheckpoint(id, context.deserialize(root.get("location"), BlockLocation.class));
            case "box" -> new BoxCheckpoint(id, context.deserialize(root.get("world"), UUID.class),
                context.deserialize(root.get("box"), ImmutableBoundingBox.class));
            case "split" -> {
                JsonArray blocksArr = root.getAsJsonArray("blocks");
                List<BlockLocation> blocks = new ArrayList<>(blocksArr.size());
                blocksArr.forEach(blockElem -> blocks.add(context.deserialize(blockElem, BlockLocation.class)));
                JsonArray boxesArr = root.getAsJsonArray("boxes");
                List<Pair<UUID, ImmutableBoundingBox>> boxes = new ArrayList<>(boxesArr.size());
                boxesArr.forEach(boxElem -> {
                    JsonObject boxObj = boxElem.getAsJsonObject();
                    boxes.add(Pair.of(
                        context.deserialize(boxObj.get("world"), UUID.class),
                        context.deserialize(boxObj.get("box"), ImmutableBoundingBox.class)
                    ));
                });
                yield new SplitCheckpoint(root.get("id").getAsInt(), blocks, boxes);
            }
            default -> throw new JsonParseException("Invalid checkpoint type: " + checkpointType);
        };
    }

    @Override
    public JsonElement serialize(Checkpoint src, Type type, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("id", src.id());
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
                JsonArray blocksArr = new JsonArray(split.blocks().size());
                split.blocks().forEach(loc -> blocksArr.add(context.serialize(loc)));
                root.add("blocks", blocksArr);
                JsonArray boxesArr = new JsonArray(split.boxes().size());
                split.boxes().forEach(pair -> {
                    JsonObject pairObj = new JsonObject();
                    pairObj.add("world", context.serialize(pair.left()));
                    pairObj.add("box", context.serialize(pair.right()));
                    boxesArr.add(pairObj);
                });
                root.add("boxes", boxesArr);
            }
            default -> throw new UnsupportedOperationException("Unsupported checkpoint type: " + src.getClass());
        }
        return root;
    }

    public static final CheckpointJsonCodec INSTANCE = new CheckpointJsonCodec();

}
