package me.whizvox.myparkour.course;

import me.whizvox.myparkour.util.ImmutableBoundingBox;
import me.whizvox.myparkour.util.ImmutableLocation;
import org.bukkit.entity.Player;

import java.util.UUID;

public record BoxCheckpoint(ImmutableBoundingBox box, UUID worldId, ImmutableLocation respawn) implements Checkpoint {

    @Override
    public boolean isCollidingWith(Player player) {
        return player.getWorld().getUID().equals(worldId) && box.overlaps(player.getBoundingBox());
    }

}
