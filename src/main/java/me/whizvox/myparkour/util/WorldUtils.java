package me.whizvox.myparkour.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WorldUtils {

    public static Optional<World> getWorld(UUID worldId) {
        return Optional.ofNullable(Bukkit.getWorld(worldId));
    }

    public static String getWorldName(UUID worldId) {
        return getWorld(worldId).map(World::getName).orElse("???");
    }

    public static Stream<Block> getBlockStream(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return StreamSupport.stream(new BlockSpliterator(world, minX, minY, minZ, maxX, maxY, maxZ), false);
    }

    public static Stream<Block> getBlocksTouchingPlayer(Player player) {
        BoundingBox box = player.getBoundingBox();
        int minX = (int) box.getMinX();
        int minY = (int) box.getMinY();
        int minZ = (int) box.getMinZ();
        int maxX = (int) Math.ceil(box.getMaxX());
        int maxY = (int) Math.ceil(box.getMaxY());
        int maxZ = (int) Math.ceil(box.getMaxZ());
        return getBlockStream(player.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
    }

}
