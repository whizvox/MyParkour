package me.whizvox.myparkour.util;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class CommandUtils {

    public static Permission createPermission(String name) {
        return new Permission("myparkour." + name);
    }

    public static boolean senderHasPermission(CommandSourceStack source, Permission permission) {
        return source.getSender().hasPermission(permission);
    }

    public static boolean playerHasPermission(CommandSourceStack source, Permission permission) {
        return source.getSender() instanceof Player && senderHasPermission(source, permission);
    }

}
