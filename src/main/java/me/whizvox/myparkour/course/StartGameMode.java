package me.whizvox.myparkour.course;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collections;
import java.util.Map;

public enum StartGameMode {

    SURVIVAL("survival"),
    ADVENTURE("adventure"),
    CREATIVE("creative"),
    DEFAULT("default"),
    NONE("none");

    public final String repr;

    StartGameMode(String repr) {
        this.repr = repr;
    }

    public static final Map<String, StartGameMode> MAP;

    static {
        Map<String, StartGameMode> map = new Object2ObjectOpenHashMap<>();
        for (StartGameMode value : values()) {
            map.put(value.repr, value);
        }
        MAP = Collections.unmodifiableMap(map);
    }

}
