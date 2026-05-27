package me.whizvox.myparkour.util;

import me.whizvox.myparkour.course.BlockCheckpoint;
import me.whizvox.myparkour.course.BoxCheckpoint;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.SplitCheckpoint;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

public class MiniMessageUtils {

    public static Component formatLocation(double x, double y, double z, double yaw, double pitch, String worldName) {
        return Component.text(String.format("%.1f", x), NamedTextColor.YELLOW)
            .append(Component.text(", "))
            .append(Component.text(String.format("%.1f", y), NamedTextColor.YELLOW))
            .append(Component.text(", "))
            .append(Component.text(String.format("%.1f", z), NamedTextColor.YELLOW))
            .append(Component.text(" <"))
            .append(Component.text(String.format("%.1f", yaw), NamedTextColor.AQUA))
            .append(Component.text(", "))
            .append(Component.text(String.format("%.1f", pitch), NamedTextColor.AQUA))
            .append(Component.text("> ("))
            .append(Component.text(worldName, NamedTextColor.YELLOW))
            .append(Component.text(")"));
    }

    public static Component formatLocation(Location loc) {
        return formatLocation(loc.x(), loc.y(), loc.z(), loc.getYaw(), loc.getPitch(), loc.getWorld().getName());
    }

    public static Component formatLocation(ImmutableLocation loc) {
        return formatLocation(loc.x(), loc.y(), loc.z(), loc.yaw(), loc.pitch(), WorldUtils.getWorldName(loc.worldId()));
    }

    public static Component formatBlockLocation(int x, int y, int z, String worldName) {
        return Component.text(x, NamedTextColor.YELLOW)
            .append(Component.text(", "))
            .append(Component.text(y, NamedTextColor.YELLOW))
            .append(Component.text(", "))
            .append(Component.text(z, NamedTextColor.YELLOW))
            .append(Component.text(" ("))
            .append(Component.text(worldName))
            .append(Component.text(")"));
    }

    public static Component formatBlockLocation(BlockLocation loc) {
        return formatBlockLocation(loc.x(), loc.y(), loc.z(), WorldUtils.getWorldName(loc.worldId()));
    }

    public static Component formatBox(ImmutableBoundingBox box) {
        return Component.text("(")
            .append(Component.text(String.format("%.1f", box.x1()), NamedTextColor.YELLOW))
            .append(Component.text(", "))
            .append(Component.text(String.format("%.1f", box.y1()), NamedTextColor.YELLOW))
            .append(Component.text(", "))
            .append(Component.text(String.format("%.1f", box.z1()), NamedTextColor.YELLOW))
            .append(Component.text(") ("))
            .append(Component.text(String.format("%.1f", box.x2()), NamedTextColor.AQUA))
            .append(Component.text(", "))
            .append(Component.text(String.format("%.1f", box.y2()), NamedTextColor.AQUA))
            .append(Component.text(", "))
            .append(Component.text(String.format("%.1f", box.z2()), NamedTextColor.AQUA))
            .append(Component.text(")"));
    }

    public static Component formatCheckpoint(Checkpoint checkpoint) {
        return switch (checkpoint) {
            case BlockCheckpoint block -> formatBlockLocation(block.location());
            case BoxCheckpoint box -> formatBox(box.box()).append(Component.text(" (")).append(Component.text(WorldUtils.getWorldName(box.worldId()), NamedTextColor.YELLOW));
            case SplitCheckpoint split -> {
                Component comp = Component.empty();
                int index = 1;
                for (Checkpoint cp : split.checkpoints()) {
                    comp = comp.append(Component.text("\n  " + index + ". ")).append(formatCheckpoint(cp));
                    index++;
                }
                yield comp;
            }
        };
    }

}
