package me.whizvox.myparkour.course;

import me.whizvox.myparkour.util.ImmutableBoundingBox;
import org.bukkit.entity.Player;

import java.util.UUID;

public record BoxCheckpoint(ImmutableBoundingBox box, UUID worldId) implements Checkpoint {

    @Override
    public boolean isCollidingWith(Player player) {
        return player.getWorld().getUID().equals(worldId) && box.overlaps(player.getBoundingBox());
    }

}
