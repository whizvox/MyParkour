package me.whizvox.myparkour.util;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@NotNullByDefault
public record SlottedItem(int slot, ItemStack item) implements ConfigurationSerializable {

    public SlottedItem(Map<String, Object> obj) {
        this(
            (Integer) obj.get("slot"),
            (ItemStack) obj.get("item")
        );
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of("slot", slot, "item", item);
    }

    public static List<SlottedItem> getItems(Player player) {
        List<SlottedItem> items = new ArrayList<>();
        PlayerInventory inv = player.getInventory();
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack item = inv.getItem(slot);
            if (item != null && !item.isEmpty()) {
                items.add(new SlottedItem(slot, item));
            }
        }
        return Collections.unmodifiableList(items);
    }

}
