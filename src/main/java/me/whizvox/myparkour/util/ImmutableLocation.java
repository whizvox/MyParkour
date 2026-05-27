package me.whizvox.myparkour.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NotNullByDefault
public record ImmutableLocation(UUID worldId, double x, double y, double z, float pitch, float yaw) {

    public ImmutableLocation(Location loc) {
        this(loc.getWorld().getUID(), loc.x(), loc.y(), loc.z(), loc.getPitch(), loc.getYaw());
    }

    public @Nullable World getWorld() {
        return Bukkit.getWorld(worldId);
    }

    public Location toLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

}
