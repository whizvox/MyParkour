package me.whizvox.myparkour.course;

import me.whizvox.myparkour.util.BlockLocation;
import org.bukkit.entity.Player;

public record BlockCheckpoint(BlockLocation location) implements Checkpoint {

    @Override
    public boolean isCollidingWith(Player player) {
        return player.getWorld().getUID().equals(location.worldId()) && player.getBoundingBox().overlaps(location.getBoundingBox());
    }

}
