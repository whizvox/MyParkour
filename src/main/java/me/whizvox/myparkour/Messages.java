package me.whizvox.myparkour;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.core.command.EditCourseCommand;
import me.whizvox.myparkour.core.command.MyParkourCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.translation.Argument;

import java.util.Map;

public class Messages {

    public static String key(String str) {
        return "myparkour." + str;
    }

    public static final String KEY_PREFIX = key("prefix");

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

    public static TranslatableComponent translate(String key) {
        return Component.translatable(key, Argument.component("prefix", Component.translatable(KEY_PREFIX)));
    }

    public static Map<String, String> getDefaultMessages() {
        Map<String, String> m = new Object2ObjectOpenHashMap<>();
        m.put(KEY_PREFIX, "[<bold><gradient:#674fff:#80b3ff>MyParkour</gradient></bold>]");
        m.put(MyParkourCommand.KEY_VERSION, "<prefix> <aqua>Running <yellow><version></yellow></aqua>");
        m.put(MyParkourCommand.KEY_RELOAD, "<prefix> <green>Reload complete</green>");
        m.put(EditCourseCommand.KEY_CREATE, "<green>Successfully created a new parkour course. Use the tools in your hotbar to edit it.</green>");
        m.put(EditCourseCommand.KEY_EDIT, "<green>Course is now being edited. Run <yellow>/editcourse save</yellow> or <yellow>/editcourse discard</yellow> to stop editing.</green>");
        m.put(EditCourseCommand.KEY_DISCARD, "<gold>All changes have been discarded.</gold>");
        m.put(EditCourseCommand.KEY_SAVE, "<green>Course has been saved.</green>");
        m.put(EditCourseCommand.KEY_SET_NAME, "<green>Course name has been set to <yellow><name></yellow>.</green>");
        m.put(EditCourseCommand.KEY_SET_DISPLAY_NAME, "<green>Course display name has been set to <displayname>.</green>");
        m.put(EditCourseCommand.KEY_SET_START, "<green>Course start location has been set to <location>.</green>");
        m.put(EditCourseCommand.KEY_SET_EXIT, "<green>Course exit location has been set to <location>.</green>");
        m.put(EditCourseCommand.KEY_CHECKPOINT_ADD, "<green>Added new checkpoint <red>#<index></red> <checkpoint>.</green>");
        m.put(EditCourseCommand.KEY_CHECKPOINT_LIST_NONE, "<gray>No checkpoints found.</gray>");
        m.put(EditCourseCommand.KEY_CHECKPOINT_LIST_HEADER, "<aqua>Checkpoints for <yellow><course_name></yellow></aqua>: <checkpoints>");
        m.put(EditCourseCommand.KEY_CHECKPOINT_INSERT, "<green>Inserted new checkpoint <red>#<index></red> <checkpoint>.</green>");
        m.put(EditCourseCommand.KEY_CHECKPOINT_REMOVE, "<gold>Removed checkpoint <red>#<index></red>.</gold>");
        m.put(EditCourseCommand.KEY_CHECKPOINT_SPLIT_ADD, "<green>Added new checkpoint to split <yellow>#<index></yellow> <checkpoint>.");
        m.put(EditCourseCommand.KEY_CHECKPOINT_SPLIT_REMOVE, "<gold>Removed checkpoint from split.</gold>");
        m.put(EditCourseCommand.KEY_INVALID_CHECKPOINT_INDEX, "<red>Invalid checkpoint index: <yellow><index></yellow></red>");
        m.put(EditCourseCommand.KEY_ALREADY_EDITING_SELF, "<red>You are already editing a course.</red>");
        m.put(EditCourseCommand.KEY_ALREADY_EDITING_OTHER, "<red><yellow><player></yellow> is already editing that course.</red>");
        m.put(EditCourseCommand.KEY_NOT_EDITING, "<red>You are not currently editing a course.</red>");
        m.put(EditCourseCommand.KEY_MISSING_NAME, "<red>Cannot save course as it is missing a name.</red>");
        m.put(EditCourseCommand.KEY_MISSING_DISPLAY_NAME, "<red>Cannot save course as it is missing a display name.</red>");
        m.put(EditCourseCommand.KEY_NO_CHECKPOINTS, "<red>Cannot save course as it has no checkpoints.</red>");
        m.put(EditCourseCommand.KEY_MISSING_START, "<red>Cannot save course as it has no start location.</red>");
        m.put(EditCourseCommand.KEY_MISSING_EXIT, "<red>Cannot save course as it has no exit location.</red>");
        m.put(EditCourseCommand.KEY_NAME_UNAVAILABLE, "<red>That name is already taken by another course.</red>");
        m.put(EditCourseCommand.KEY_NOT_FOUND, "<red>Unexpected state: Could not find corresponding course even though you're editing one</red>");
        m.put(EditCourseCommand.KEY_NOT_SPLIT, "<red>That is not a split checkpoint.</red>");
        m.put("myparkour.run.start", "<green>Running <course></green>");
        m.put("myparkour.run.exit", "Exited <course>");
        m.put("myparkour.run.nextCheckpoint", "<green>Checkpoint <yellow><checkpoint></yellow>/<yellow><total></yellow></green>");
        m.put("myparkour.run.finish.firstTime", "<green>You beat <course> for the first time in <time>!</green>");
        m.put("myparkour.run.finish.personalBest", "<green>You achieved a new personal best on <course> in <time>!</green>");
        m.put("myparkour.run.finish.noChange", "<green>You beat <course> in <time>.</green>");
        m.put("myparkour.error.run.alreadyRunning", "<red>You are already running in a course.</red>");
        m.put("myparkour.error.run.notRunning", "<red>You are not currently running in a course.</red>");
        m.put("myparkour.error.run.notOpen", "<red>That course is currently not open.</red>");
        m.put("myparkour.error.run.teleportFailed.start", "<red>Failed to teleport to the start of the course.</red>");
        m.put("myparkour.error.run.teleportFailed.exit", "<red>Failed to teleport to the exit of the course.</red>");
        return m;
    }

}
