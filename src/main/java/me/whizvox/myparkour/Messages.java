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
        m.put("myparkour.edit.create.success", "<green>Successfully created a new parkour course <yellow><course></yellow>.</green> Run <yellow>/editcourse save</yellow> once you're finished editing.");
        m.put("myparkour.edit.edit.success", "<green>Course <course> is now being edited.</green> Run <yellow>/editcourse save</yellow> or <yellow>/editcourse discard</yellow> to stop editing.");
        m.put("myparkour.edit.discard", "All changes to have been discarded.");
        m.put("myparkour.edit.save", "<green>Course has been saved.</green>");
        m.put("myparkour.edit.set.name", "<green>Course name has been set to <yellow><name></yellow>.</green>");
        m.put("myparkour.edit.set.displayName", "<green>Course display name has been set to <displayname>.</green>");
        m.put("myparkour.edit.set.start", "<green>Course start location has been set to <location>.</green>");
        m.put("myparkour.edit.set.exit", "<green>Course exit location has been set to <location>.</green>");
        m.put("myparkour.edit.checkpoint.add", "<green>Added new checkpoint <red>#<index></red> <checkpoint>.</green>");
        m.put("myparkour.edit.checkpoint.list.none", "<gray>No checkpoints found.</gray>");
        m.put("myparkour.edit.checkpoint.list.all", "<aqua>Checkpoints for <yellow><course_name></yellow></aqua>: <checkpoints>");
        m.put("myparkour.edit.checkpoint.insert", "<green>Inserted new checkpoint <red>#<index></red> <checkpoint>.</green>");
        m.put("myparkour.edit.checkpoint.remove", "<gold>Removed checkpoint <red>#<index></red>.</gold>");
        m.put("myparkour.edit.checkpoint.split.add", "<green>Added new checkpoint to split <yellow>#<index></yellow> <checkpoint>.");
        m.put("myparkour.edit.checkpoint.split.remove", "<gold>Removed checkpoint from split.</gold>");
        m.put("myparkour.edit.flag.set.success", "Successfully set flag <yellow><flag></yellow> for <yellow><course></yellow>");
        m.put("myparkour.edit.flag.set.alreadySet", "<gold>That course already has that flag set.</gold>");
        m.put("myparkour.edit.flag.unset.success", "Successfully unset flag <yellow><flag></yellow> for <yellow><course></yellow>");
        m.put("myparkour.edit.flag.unset.notSet", "<gold>That course does not have that flag set.</gold>");
        m.put("myparkour.edit.edit.cannotBeOpen", "<gold>That course is still open. If you want to edit it, you'll have to close it first: <yellow>/editcourse close <course></yellow></gold>");
        m.put("myparkour.edit.warning.invalidCheckpointIndex", "<red>Invalid checkpoint index: <yellow><index></yellow></red>");
        m.put("myparkour.edit.warning.alreadyEditing", "<red>You are already editing a course: <course></red>");
        m.put("myparkour.edit.warning.alreadyEditing.other", "<red><yellow><player></yellow> is already editing that course.</red>");
        m.put("myparkour.edit.warning.notEditing", "<red>You are not currently editing a course.</red>");
        m.put("myparkour.edit.save.missingName", "<red>Cannot save course as it is missing a name.</red>");
        m.put("myparkour.edit.save.missingDisplayName", "<red>Cannot save course as it is missing a display name.</red>");
        m.put("myparkour.edit.save.noCheckpoints", "<red>Cannot save course as it has no checkpoints.</red>");
        m.put("myparkour.edit.save.missingStart", "<red>Cannot save course as it has no start location.</red>");
        m.put("myparkour.edit.save.missingExit", "<red>Cannot save course as it has no exit location.</red>");
        m.put("myparkour.edit.warning.nameUnavailable", "<red>That name is already taken by another course.</red>");
        m.put("myparkour.edit.warning.invalidSplitLocalIndex", "<gold>Invalid local split index: <index></gold>");
        m.put("myparkour.edit.save.notFound", "<red>Unexpected state: Could not find corresponding course even though you're editing one</red>");
        m.put("myparkour.edit.warning.notSplit", "<red>That is not a split checkpoint.</red>");
        m.put("myparkour.edit.open", "Course <yellow><course></yellow> is now open.");
        m.put("myparkour.edit.open.alreadyOpen", "<gold>That course is already open.</gold>");
        m.put("myparkour.edit.close", "Course <yellow><course></yellow> is now closed.");
        m.put("myparkour.edit.close.alreadyClosed", "<gold>That course is already closed.</gold>");
        m.put("myparkour.edit.error.couldNotSave", "<red>Could not save course: <reason>. Please report this to the plugin developer!</red>");
        m.put("myparkour.run.start", "<green>Running <course></green>");
        m.put("myparkour.run.exit", "Exited <course>");
        m.put("myparkour.run.nextCheckpoint", "<green>Checkpoint <yellow><checkpoint></yellow>/<yellow><total></yellow></green> | <red><time></red>");
        m.put("myparkour.run.finish.firstTime", "<aqua>You beat <course> for the first time in <yellow><time></yellow>!</aqua>");
        m.put("myparkour.run.finish.personalBest", "<aqua>You achieved a new personal best on <course> in <yellow><time></yellow>!</aqua>");
        m.put("myparkour.run.finish.noChange", "<green>You beat <course> in <yellow><time></yellow>.</green>");
        m.put("myparkour.run.error.alreadyRunning", "<red>You are already running in a course.</red>");
        m.put("myparkour.run.error.notRunning", "<red>You are not currently running in a course.</red>");
        m.put("myparkour.run.error.notOpen", "<red>That course is currently not open.</red>");
        m.put("myparkour.run.error.teleportFailed.start", "<red>Failed to teleport to the start of the course.</red>");
        m.put("myparkour.run.error.teleportFailed.exit", "<red>Failed to teleport to the exit of the course.</red>");
        m.put("myparkour.exception.invalidCourseFlag", "Invalid course flag: %s");
        return m;
    }

}
