package me.whizvox.myparkour.course;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collections;
import java.util.Map;

public enum ExitGameMode {

    SURVIVAL("survival"),
    ADVENTURE("adventure"),
    CREATIVE("creative"),
    SPECTATOR("spectator"),
    PREVIOUS("previous"),
    DEFAULT("default"),
    NONE("none");

    public final String repr;

    ExitGameMode(String repr) {
        this.repr = repr;
    }

    public static final Map<String, ExitGameMode> MAP;

    static {
        Map<String, ExitGameMode> map = new Object2ObjectOpenHashMap<>();
        for (ExitGameMode value : values()) {
            map.put(value.repr, value);
        }
        MAP = Collections.unmodifiableMap(map);
    }

}
