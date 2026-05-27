package me.whizvox.myparkour.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;

public class WorldUtils {

    public static Optional<World> getWorld(UUID worldId) {
        return Optional.ofNullable(Bukkit.getWorld(worldId));
    }

    public static String getWorldName(UUID worldId) {
        return getWorld(worldId).map(World::getName).orElse("???");
    }

}
