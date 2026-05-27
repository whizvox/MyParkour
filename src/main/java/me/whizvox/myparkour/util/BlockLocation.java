package me.whizvox.myparkour.util;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record BlockLocation(int x, int y, int z, UUID worldId) implements ConfigurationSerializable {

    public BlockLocation(Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getUID());
    }

    public BoundingBox getBoundingBox() {
        return new BoundingBox(x, y, z, x + 1, y + 1, z + 1);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("x", x, "y", y, "z", z, "world", worldId);
    }

    public static BlockLocation deserialize(Map<String, Object> map) {
        return new BlockLocation((Integer) map.get("x"), (Integer) map.get("y"), (Integer) map.get("z"), (UUID) map.get("world"));
    }

}
