package me.whizvox.myparkour.util;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;

@NotNullByDefault
public record ImmutableBoundingBox(double x1, double y1, double z1, double x2, double y2,
                                   double z2) implements ConfigurationSerializable {

    public ImmutableBoundingBox(BoundingBox box) {
        this(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
    }

    public boolean overlaps(ImmutableBoundingBox other) {
        return x1 < other.x2 && x2 > other.x1 &&
            y1 < other.y2 && y2 > other.y1 &&
            z1 < other.z2 && z2 > other.z1;
    }

    public boolean overlaps(BoundingBox other) {
        return x1 <= other.getMaxX() && x2 >= other.getMinX() &&
            y1 <= other.getMaxY() && y2 >= other.getMinY() &&
            z1 <= other.getMaxZ() && z2 >= other.getMinZ();
    }

    public BoundingBox toBox() {
        return new BoundingBox(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("x1", x1, "y1", y1, "z1", z1, "x2", x2, "y2", y2, "z2", z2);
    }

    public static ImmutableBoundingBox deserialize(Map<String, Object> map) {
        return new ImmutableBoundingBox(
            (Double) map.get("x1"),
            (Double) map.get("y1"),
            (Double) map.get("z1"),
            (Double) map.get("x2"),
            (Double) map.get("y2"),
            (Double) map.get("z2")
        );
    }

}
