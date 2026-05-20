package me.whizvox.myparkour;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.translation.Argument;

import java.util.Map;

public class Messages {

    private static String key(String str) {
        return "myparkour." + str;
    }

    public static final String
        KEY_PREFIX = key("prefix"),
        KEY_COMMAND_VERSION = key("command.version"),
        KEY_GENERAL_ALREADY_EDITING_SELF = key("general.alreadyEditing.self"),
        KEY_GENERAL_ALREADY_EDITING_OTHER = key("general.alreadyEditing.other"),
        KEY_GENERAL_CANNOT_EDIT_OPEN = key("general.isOpen");

    @SuppressWarnings("PatternValidation")
    public static TranslatableComponent translate(String key, Map<String, Object> args) {
        ComponentLike[] tlArgs = new ComponentLike[args.size() + 1];
        tlArgs[0] = Argument.component("prefix", Component.translatable(KEY_PREFIX));
        int index = 1;
        for (String k : args.keySet()) {
            Object value = args.get(k);
            ComponentLike arg;
            if (value instanceof ComponentLike compLike) {
                arg = Argument.component(k, compLike);
            } else if (value instanceof Number num) {
                arg = Argument.numeric(k, num);
            } else {
                arg = Argument.string(k, String.valueOf(value));
            }
            tlArgs[index++] = arg;
        }
        return Component.translatable(key, tlArgs);
    }

    public static Map<String, String> getDefaultMessages() {
        Map<String, String> m = new Object2ObjectOpenHashMap<>();
        m.put(KEY_PREFIX, "[<bold><gradient:#674fff:#80b3ff>MyParkour</gradient></bold>]");
        m.put(KEY_COMMAND_VERSION, "<prefix> <aqua>Running <yellow><version></yellow></aqua>");
        m.put(KEY_GENERAL_ALREADY_EDITING_SELF, "<red>You are already editing a course.</red>");
        m.put(KEY_GENERAL_ALREADY_EDITING_OTHER, "<red><player> is already editing that course.</red>");
        m.put(KEY_GENERAL_CANNOT_EDIT_OPEN, "<red>Course cannot be edited while it is open. Run <green>/myparkour close <courseName></green> to close it.</red>");
        return m;
    }

}
