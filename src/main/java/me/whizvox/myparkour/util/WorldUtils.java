package me.whizvox.myparkour.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorldUtils {

    public static Optional<World> getWorld(UUID worldId) {
        return Optional.ofNullable(Bukkit.getWorld(worldId));
    }

    public static String getWorldName(UUID worldId) {
        return getWorld(worldId).map(World::getName).orElse("???");
    }

    public static List<Block> getTouchingBlocks(Player player) {
        List<Block> blocks = new ArrayList<>();
        BoundingBox box = player.getBoundingBox().expand(0.1, 0.1, 0.1);
        int minX = (int) box.getMinX();
        int minY = (int) box.getMinY();
        int minZ = (int) box.getMinZ();
        int maxX = (int) Math.ceil(box.getMaxX());
        int maxY = (int) Math.ceil(box.getMaxY());
        int maxZ = (int) Math.ceil(box.getMaxZ());
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    if (!block.getType().isAir() && block.getCollisionShape().overlaps(box)) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

}
