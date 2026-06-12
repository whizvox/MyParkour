package me.whizvox.myparkour.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

@NotNullByDefault
public record ImmutableLocation(UUID worldId, double x, double y, double z, float pitch, float yaw) implements ConfigurationSerializable {

    public ImmutableLocation(Map<String, Object> data) {
        this(
            UUID.fromString((String) data.get("world")),
            (Double) data.get("x"),
            (Double) data.get("y"),
            (Double) data.get("z"),
            ((Double) data.get("pitch")).floatValue(),
            ((Double) data.get("yaw")).floatValue()
        );
    }

    public ImmutableLocation(Location loc) {
        this(loc.getWorld().getUID(), loc.x(), loc.y(), loc.z(), loc.getPitch(), loc.getYaw());
    }

    public @Nullable World getWorld() {
        return Bukkit.getWorld(worldId);
    }

    public Location toLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
            "world", worldId.toString(),
            "x", x,
            "y", y,
            "z", z,
            "pitch", pitch,
            "yaw", yaw
        );
    }

}
