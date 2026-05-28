package me.whizvox.myparkour.course;

import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.ImmutableLocation;
import org.bukkit.entity.Player;

public record BlockCheckpoint(BlockLocation location, ImmutableLocation respawn) implements Checkpoint {

    @Override
    public boolean isCollidingWith(Player player) {
        return player.getWorld().getUID().equals(location.worldId()) && player.getBoundingBox().overlaps(location.getBoundingBox());
    }

}
