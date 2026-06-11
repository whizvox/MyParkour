package me.whizvox.myparkour.sign;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.Persistent;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class ParkourSigns implements Persistent<ParkourSigns.SaveData> {

    private final Map<BlockLocation, ParkourSign> signs;

    public ParkourSigns() {
        this.signs = new Object2ObjectOpenHashMap<>();
    }

    public boolean register(BlockLocation location, ParkourSign sign) {
        if (!signs.containsKey(location)) {
            signs.put(location, sign);
            return true;
        }
        return false;
    }

    public boolean exists(BlockLocation location) {
        return signs.containsKey(location);
    }

    public boolean delete(BlockLocation location) {
        return signs.remove(location) != null;
    }

    public Optional<ParkourSign> get(BlockLocation location) {
        return Optional.ofNullable(signs.get(location));
    }

    @Override
    public SaveData writePersistent() {
        return new SaveData(signs);
    }

    @Override
    public void readPersistent(SaveData obj) {
        signs.clear();
        signs.putAll(obj.signs);
    }

    @Override
    public Type getPersistentType() {
        return SaveData.class;
    }

    public record SaveData(Map<BlockLocation, ParkourSign> signs) {
    }

}
