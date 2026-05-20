package me.whizvox.myparkour.course;

import it.unimi.dsi.fastutil.Pair;
import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.ImmutableBoundingBox;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record SplitCheckpoint(int id, List<BlockLocation> blocks,
                              List<Pair<UUID, ImmutableBoundingBox>> boxes) implements Checkpoint {

    public SplitCheckpoint(int id, List<BlockLocation> blocks, List<Pair<UUID, ImmutableBoundingBox>> boxes) {
        this.id = id;
        this.blocks = Collections.unmodifiableList(blocks);
        this.boxes = Collections.unmodifiableList(boxes);
    }

    @Override
    public boolean isCollidingWith(Player player) {
        for (BlockLocation loc : blocks) {
            if (loc.worldId().equals(player.getWorld().getUID()) && loc.getBoundingBox().overlaps(player.getBoundingBox())) {
                return true;
            }
        }
        for (Pair<UUID, ImmutableBoundingBox> pair : boxes) {
            if (pair.left().equals(player.getWorld().getUID()) && pair.right().overlaps(player.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

}
