package me.whizvox.myparkour.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.MyParkour;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static me.whizvox.myparkour.db.tables.PlayerNames.PLAYER_NAMES;

public class PlayerNameCache {

    private final Map<UUID, Entry> names;

    public PlayerNameCache() {
        this.names = new Object2ObjectOpenHashMap<>();
    }

    public void load() {
        MyParkour.inst().dsl()
            .createTableIfNotExists(PLAYER_NAMES)
            .columns(PLAYER_NAMES.fields())
            .execute();
        names.clear();
        MyParkour.inst().dsl()
            .select()
            .from(PLAYER_NAMES)
            .forEach(record -> {
                UUID playerId = UUID.fromString(record.get(PLAYER_NAMES.ID));
                String name = record.get(PLAYER_NAMES.NAME);
                Component displayName = JSONComponentSerializer.json().deserialize(record.get(PLAYER_NAMES.DISPLAY_NAME));
                LocalDateTime lastModified = record.get(PLAYER_NAMES.LAST_MODIFIED);
                names.put(playerId, new Entry(name, displayName, lastModified));
            });
        MyParkour.inst().getLogger().info("Finished loading player name cache");
    }

    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.put(player.getUniqueId(), new Entry(player.getName(), player.displayName(), LocalDateTime.now()));
        }
        save();
        MyParkour.inst().getLogger().info("Finished reloading player name cache");
    }

    public void save() {
        if (!names.isEmpty()) {
            MyParkour.inst().dsl().deleteFrom(PLAYER_NAMES).execute();
            var batch = MyParkour.inst().dsl().batch(MyParkour.inst().dsl().insertInto(PLAYER_NAMES).values(null, null, null, null));
            names.forEach((id, entry) -> {
                batch.bind(id.toString(), entry.name, JSONComponentSerializer.json().serialize(entry.displayName), entry.lastLogin);
            });
            batch.execute();
            MyParkour.inst().getLogger().info("Finished saving player name cache");
        }
    }

    public void update(UUID playerId, String name, Component displayName, LocalDateTime lastLogin) {
        names.put(playerId, new Entry(name, displayName, lastLogin));
    }

    public void update(Player player) {
        update(player.getUniqueId(), player.getName(), player.displayName(), LocalDateTime.now());
    }

    public Component getDisplayName(UUID playerId) {
        Component result;
        if (names.containsKey(playerId)) {
            result = names.get(playerId).displayName;
        } else {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                result = player.displayName();
                names.put(playerId, new Entry(player.getName(), result, LocalDateTime.now()));
            } else {
                String name = Bukkit.getOfflinePlayer(playerId).getName();
                if (name != null) {
                    result = Component.text(name);
                    names.put(playerId, new Entry(name, result, LocalDateTime.now()));
                } else {
                    result = Component.text("???", NamedTextColor.DARK_RED);
                }
            }
        }
        return result;
    }

    public record Entry(String name, Component displayName, LocalDateTime lastLogin) {
    }

}
