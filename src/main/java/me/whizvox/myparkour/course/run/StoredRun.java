package me.whizvox.myparkour.course.run;

import me.whizvox.myparkour.util.ImmutableLocation;
import me.whizvox.myparkour.util.SlottedItem;
import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record StoredRun(UUID playerId, List<SlottedItem> inventory, ImmutableLocation exit, GameMode gameMode) implements ConfigurationSerializable {

    public StoredRun(Map<String, Object> data) {
        //noinspection unchecked
        this(
            UUID.fromString((String) data.get("player")),
            (List<SlottedItem>) data.get("inventory"),
            (ImmutableLocation) data.get("exit"),
            GameMode.valueOf((String) data.get("gamemode"))
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
            "player", playerId.toString(),
            "inventory", inventory,
            "exit", exit,
            "gamemode", gameMode.toString()
        );
    }

}
