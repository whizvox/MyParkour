package me.whizvox.myparkour;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.translation.GlobalTranslator;

import java.util.Locale;
import java.util.Map;

public class Messages {

    public static String key(String str) {
        return "myparkour." + str;
    }

    public static final String
        KEY_PREFIX = key("prefix"),
        // basic commands
        KEY_COMMAND_VERSION = key("command.version"),
        KEY_COMMAND_RELOAD = key("command.reload"),

        // parkour command
        KEY_RUN_START_SUCCESS = key("run.start.success"),
        KEY_RUN_START_CLOSED = key("run.start.closed"),
        KEY_RUN_START_ALREADY_RUNNING = key("run.start.alreadyRunning"),
        KEY_RUN_START_TELEPORT_FAILED = key("run.start.teleportFailed"),
        KEY_RUN_EXIT_SUCCESS = key("run.exit.success"),
        KEY_RUN_EXIT_TELEPORT_FAILED = key("run.exit.teleportFailed"),
        KEY_RUN_CHECKPOINT = key("run.checkpoint"),
        KEY_RUN_FINISH_FIRST = key("run.finish.first"),
        KEY_RUN_FINISH_NEW_BEST = key("run.finish.newBest"),
        KEY_RUN_FINISH_NO_CHANGE = key("run.finish.noChange"),
        KEY_RUN_GENERIC_NOT_RUNNING = key("run.generic.notRunning"),

        // editcourse command
        KEY_EDIT_CREATE_SUCCESS = key("edit.create.success"),
        KEY_EDIT_EDIT_SUCCESS = key("edit.edit.success"),
        KEY_EDIT_EDIT_STILL_OPEN = key("edit.edit.stillOpen"),
        KEY_EDIT_EDIT_ALREADY_EDITING_SELF = key("edit.edit.alreadyEditing.self"),
        KEY_EDIT_EDIT_ALREADY_EDITING_OTHER = key("edit.edit.alreadyEditing.other"),
        KEY_EDIT_DISCARD_SUCCESS = key("edit.discard.success"),
        KEY_EDIT_SAVE_SUCCESS = key("edit.save.success"),
        KEY_EDIT_SAVE_MISSING_NAME = key("edit.save.missingName"),
        KEY_EDIT_SAVE_MISSING_DISPLAY_NAME = key("edit.save.missingDisplayName"),
        KEY_EDIT_SAVE_MISSING_START = key("edit.save.missingStart"),
        KEY_EDIT_SAVE_MISSING_EXIT = key("edit.save.missingExit"),
        KEY_EDIT_SAVE_NO_CHECKPOINTS = key("edit.save.noCheckpoints"),
        KEY_EDIT_SET_NAME = key("edit.set.name"),
        KEY_EDIT_SET_DISPLAY_NAME = key("edit.set.displayName"),
        KEY_EDIT_SET_START = key("edit.set.start"),
        KEY_EDIT_SET_EXIT = key("edit.set.exit"),
        KEY_EDIT_SET_START_GAMEMODE = key("edit.set.startGameMode"),
        KEY_EDIT_SET_EXIT_GAMEMODE = key("edit.set.exitGameMode"),
        KEY_EDIT_SET_BOTTOM = key("edit.set.bottom"),
        KEY_EDIT_CHECKPOINT_ADD = key("edit.checkpoint.add"),
        KEY_EDIT_CHECKPOINT_LIST_NONE = key("edit.checkpoint.list.none"),
        KEY_EDIT_CHECKPOINT_LIST_ALL = key("edit.checkpoint.list.all"),
        KEY_EDIT_CHECKPOINT_INSERT = key("edit.checkpoint.insert"),
        KEY_EDIT_CHECKPOINT_REMOVE = key("edit.checkpoint.remove"),
        KEY_EDIT_CHECKPOINT_SPLIT_ADD = key("edit.checkpoint.split.add"),
        KEY_EDIT_CHECKPOINT_SPLIT_REMOVE = key("edit.checkpoint.split.remove"),
        KEY_EDIT_CHECKPOINT_GENERIC_INVALID_INDEX = key("edit.checkpoint.generic.invalidIndex"),
        KEY_EDIT_CHECKPOINT_SPLIT_NOT_SPLIT = key("edit.checkpoint.split.notSplit"),
        KEY_EDIT_CHECKPOINT_INVALID_LOCAL_SPLIT_INDEX = key("edit.checkpoint.split.invalidLocalSplitIndex"),
        KEY_EDIT_FLAG_SET = key("edit.flag.set"),
        KEY_EDIT_FLAG_UNSET = key("edit.flag.unset"),
        KEY_EDIT_FLAG_ALREADY_SET = key("edit.flag.alreadySet"),
        KEY_EDIT_FLAG_NOT_SET = key("edit.flag.notSet"),
        KEY_EDIT_OPEN_SUCCESS = key("edit.open.success"),
        KEY_EDIT_OPEN_ALREADY_OPEN = key("edit.open.alreadyOpen"),
        KEY_EDIT_CLOSE_SUCCESS = key("edit.close.success"),
        KEY_EDIT_CLOSE_ALREADY_CLOSED = key("edit.close.alreadyClosed"),
        KEY_EDIT_DELETE_SUCCESS = key("edit.delete.success"),
        KEY_EDIT_DELETE_STILL_OPEN = key("edit.delete.stillOpen"),
        KEY_EDIT_GENERIC_NOT_EDITING = key("edit.generic.notEditing"),
        KEY_EDIT_GENERIC_NAME_UNAVAILABLE = key("edit.generic.nameUnavailable"),

        // item name and descriptions when running course
        KEY_ITEM_BACK_NAME = key("run.item.back.name"),
        KEY_ITEM_BACK_LORE = key("run.item.back.lore"),
        KEY_ITEM_RESTART_NAME = key("run.item.restart.name"),
        KEY_ITEM_RESTART_LORE = key("run.item.restart.lore"),
        KEY_ITEM_EXIT_NAME = key("run.item.exit.name"),
        KEY_ITEM_EXIT_LORE = key("run.item.exit.lore"),

        // times command
        KEY_TIMES_COURSE_NONE = key("times.course.none"),
        KEY_TIMES_COURSE_HEADER = key("times.course.header"),
        KEY_TIMES_COURSE_ENTRY = key("times.course.entry"),
        KEY_TIMES_PLAYER_NONE = key("times.player.none"),
        KEY_TIMES_PLAYER_HEADER = key("times.player.header"),
        KEY_TIMES_PLAYER_ENTRY = key("times.player.entry"),
        KEY_TIMES_INFO = key("times.info"),
        KEY_TIMES_LOOKUP_NONE = key("times.lookup.none"),
        KEY_TIMES_DELETE_ONE = key("times.delete.one"),
        KEY_TIMES_DELETE_COURSE = key("times.delete.course"),
        KEY_TIMES_DELETE_COURSE_NONE = key("times.delete.course.none"),
        KEY_TIMES_DELETE_PLAYER = key("times.delete.player"),
        KEY_TIMES_DELETE_PLAYER_NONE = key("times.delete.player.none"),
        KEY_TIMES_DELETE_ALL = key("times.delete.all"),
        KEY_TIMES_DELETE_ALL_NONE = key("times.delete.all.none"),

        // signs
        KEY_SIGN_PLACE = key("sign.place"),
        KEY_SIGN_REPLACE = key("sign.replace"),
        KEY_SIGN_BREAK = key("sign.break"),

        // debug command
        KEY_DEBUG_ADD_FAKE_TIMES = key("debug.addFakeTimes"),
        KEY_DEBUG_CLEAR = key("debug.clear"),

        // plaintext exception messages
        KEY_EXCEPTION_INVALID_COURSE_FLAG = key("exception.invalidCourseFlag"),
        KEY_EXCEPTION_UNKNOWN_COURSE_TIME = key("exception.unknownCourseTime"),
        KEY_EXCEPTION_INVALID_START_GAMEMODE = key("exception.invalidStartGameMode"),
        KEY_EXCEPTION_INVALID_EXIT_GAMEMODE = key("exception.invalidExitGameMode"),
        KEY_EXCEPTION_UNKNOWN_OFFLINE_PLAYER = key("exception.unknownOfflinePlayer");

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

    public static Component translateImmediately(String key) {
        return GlobalTranslator.translator().translate(translate(key), Locale.ENGLISH);
    }

    public static Map<String, String> getDefaultMessages() {
        Map<String, String> m = new Object2ObjectOpenHashMap<>();
        m.put(KEY_PREFIX, "[<bold><gradient:#674fff:#80b3ff>MyParkour</gradient></bold>]");
        m.put(KEY_COMMAND_VERSION, "<prefix> <aqua>Running <yellow><version></yellow></aqua>");
        m.put(KEY_COMMAND_RELOAD, "<prefix> <green>Reload complete</green>");
        m.put(KEY_EDIT_CREATE_SUCCESS, "<green>Successfully created a new parkour course <yellow><course></yellow>.</green> Run <yellow>/editcourse save</yellow> once you're finished editing.");
        m.put(KEY_EDIT_EDIT_SUCCESS, "<green>Course <course> is now being edited.</green> Run <yellow>/editcourse save</yellow> or <yellow>/editcourse discard</yellow> to stop editing.");
        m.put(KEY_EDIT_DISCARD_SUCCESS, "<gold>All changes have been discarded.</gold>");
        m.put(KEY_EDIT_SAVE_SUCCESS, "<green>Course has been saved.</green>");
        m.put(KEY_EDIT_SET_NAME, "<green>Course name has been set to <yellow><name></yellow>.</green>");
        m.put(KEY_EDIT_SET_DISPLAY_NAME, "<green>Course display name has been set to <displayname>.</green>");
        m.put(KEY_EDIT_SET_START, "<green>Course start location has been set to <location>.</green>");
        m.put(KEY_EDIT_SET_EXIT, "<green>Course exit location has been set to <location>.</green>");
        m.put(KEY_EDIT_SET_START_GAMEMODE, "Course start gamemode has been set to <yellow><gamemode></yellow>.");
        m.put(KEY_EDIT_SET_EXIT_GAMEMODE, "Course exit gamemode has been set to <yellow><gamemode></yellow>.");
        m.put(KEY_EDIT_SET_BOTTOM, "Bottom level has been set to <yellow>y=<min_y></yellow>.");
        m.put(KEY_EDIT_CHECKPOINT_ADD, "<green>Added new checkpoint <red>#<index></red> <checkpoint>.</green>");
        m.put(KEY_EDIT_CHECKPOINT_LIST_NONE, "<gray>No checkpoints found.</gray>");
        m.put(KEY_EDIT_CHECKPOINT_LIST_ALL, "<aqua>Checkpoints for <yellow><course_name></yellow></aqua>: <checkpoints>");
        m.put(KEY_EDIT_CHECKPOINT_INSERT, "<green>Inserted new checkpoint <red>#<index></red> <checkpoint>.</green>");
        m.put(KEY_EDIT_CHECKPOINT_REMOVE, "<gold>Removed checkpoint <red>#<index></red>.</gold>");
        m.put(KEY_EDIT_CHECKPOINT_SPLIT_ADD, "<green>Added new checkpoint to split <yellow>#<index></yellow> <checkpoint>.");
        m.put(KEY_EDIT_CHECKPOINT_SPLIT_REMOVE, "<gold>Removed checkpoint from split.</gold>");
        m.put(KEY_EDIT_CHECKPOINT_INVALID_LOCAL_SPLIT_INDEX, "<gold>Invalid local split index: <index></gold>");
        m.put(KEY_EDIT_FLAG_SET, "Successfully set flag <yellow><flag></yellow> for <yellow><course></yellow>");
        m.put(KEY_EDIT_FLAG_ALREADY_SET, "<gold>That course already has that flag set.</gold>");
        m.put(KEY_EDIT_FLAG_UNSET, "Successfully unset flag <yellow><flag></yellow> for <yellow><course></yellow>");
        m.put(KEY_EDIT_FLAG_NOT_SET, "<gold>That course does not have that flag set.</gold>");
        m.put(KEY_EDIT_EDIT_STILL_OPEN, "<gold>That course is still open. If you want to edit it, you'll have to close it first: <yellow>/editcourse close <course></yellow></gold>");
        m.put(KEY_EDIT_CHECKPOINT_GENERIC_INVALID_INDEX, "<red>Invalid checkpoint index: <yellow><index></yellow></red>");
        m.put(KEY_EDIT_EDIT_ALREADY_EDITING_SELF, "<red>You are already editing a course: <course></red>");
        m.put(KEY_EDIT_EDIT_ALREADY_EDITING_OTHER, "<red><yellow><player></yellow> is already editing that course.</red>");
        m.put(KEY_EDIT_GENERIC_NOT_EDITING, "<red>You are not currently editing a course.</red>");
        m.put(KEY_EDIT_GENERIC_NAME_UNAVAILABLE, "<red>That name is already taken by another course.</red>");
        m.put(KEY_EDIT_SAVE_MISSING_NAME, "<red>Cannot save course as it is missing a name.</red>");
        m.put(KEY_EDIT_SAVE_MISSING_DISPLAY_NAME, "<red>Cannot save course as it is missing a display name.</red>");
        m.put(KEY_EDIT_SAVE_NO_CHECKPOINTS, "<red>Cannot save course as it has no checkpoints.</red>");
        m.put(KEY_EDIT_SAVE_MISSING_START, "<red>Cannot save course as it has no start location.</red>");
        m.put(KEY_EDIT_SAVE_MISSING_EXIT, "<red>Cannot save course as it has no exit location.</red>");
        m.put("myparkour.edit.save.notFound", "<red>Unexpected state: Could not find corresponding course even though you're editing one</red>");
        m.put(KEY_EDIT_CHECKPOINT_SPLIT_NOT_SPLIT, "<red>That is not a split checkpoint.</red>");
        m.put(KEY_EDIT_OPEN_SUCCESS, "Course <yellow><course></yellow> is now open.");
        m.put(KEY_EDIT_OPEN_ALREADY_OPEN, "<gold>That course is already open.</gold>");
        m.put(KEY_EDIT_CLOSE_SUCCESS, "Course <yellow><course></yellow> is now closed.");
        m.put(KEY_EDIT_CLOSE_ALREADY_CLOSED, "<gold>That course is already closed.</gold>");
        m.put(KEY_EDIT_DELETE_SUCCESS, "<gold>Course successfully deleted.</gold>");
        m.put(KEY_EDIT_DELETE_STILL_OPEN, "<red>That course is still open. If you want to delete it, you'll need to close it first: </red><yellow>/editcourse close <course></yellow>");
        m.put(KEY_RUN_START_SUCCESS, "<green>Running <course></green>");
        m.put(KEY_RUN_EXIT_SUCCESS, "Exited <course>");
        m.put(KEY_RUN_CHECKPOINT, "<green>Checkpoint <yellow><checkpoint></yellow>/<yellow><total></yellow></green> | <red><time></red>");
        m.put(KEY_RUN_FINISH_FIRST, "<aqua>You beat <course> for the first time in <yellow><time></yellow>!</aqua>");
        m.put(KEY_RUN_FINISH_NEW_BEST, "<aqua>You achieved a new personal best on <course> in <yellow><time></yellow>!</aqua>");
        m.put(KEY_RUN_FINISH_NO_CHANGE, "<green>You beat <course> in <yellow><time></yellow>.</green>");
        m.put(KEY_ITEM_BACK_NAME, "<gold><bold>Go Back</bold></gold>");
        m.put(KEY_ITEM_BACK_LORE, "<gray><italic>Right-click while holding this item to go back to the last checkpoint.</italic></gray>");
        m.put(KEY_ITEM_RESTART_NAME, "<gold><bold>Restart</bold></gold>");
        m.put(KEY_ITEM_RESTART_LORE, "<gray><italic>Right-click while holding this item to restart the course.</italic></gray>");
        m.put(KEY_ITEM_EXIT_NAME, "<gold><bold>Exit</bold></gold>");
        m.put(KEY_ITEM_EXIT_LORE, "<gray><italic>Right-click while holding this item to exit the course.</italic></gray>");
        m.put(KEY_RUN_START_ALREADY_RUNNING, "<red>You are already running in a course.</red>");
        m.put(KEY_RUN_GENERIC_NOT_RUNNING, "<red>You are not currently running in a course.</red>");
        m.put(KEY_RUN_START_CLOSED, "<red>That course is currently not open.</red>");
        m.put(KEY_RUN_START_TELEPORT_FAILED, "<red>Failed to teleport to the start of the course.</red>");
        m.put(KEY_RUN_EXIT_TELEPORT_FAILED, "<red>Failed to teleport to the exit of the course.</red>");
        m.put(KEY_TIMES_COURSE_NONE, "<aqua>There are no times set for <course>.</aqua>");
        m.put(KEY_TIMES_COURSE_HEADER, "<aqua>Times for <course> (<yellow><current_page></yellow>/<yellow><total_pages></yellow>):</aqua>");
        m.put(KEY_TIMES_COURSE_ENTRY, "<gold>#<rank></gold> <red><time></red> by <yellow><player></yellow>");
        m.put(KEY_TIMES_PLAYER_NONE, "<aqua>There are no times set by <player>.</aqua>");
        m.put(KEY_TIMES_PLAYER_HEADER, "<aqua>Times from <player> (<yellow><current_page></yellow>/<yellow><total_pages></yellow>):</aqua>");
        m.put(KEY_TIMES_PLAYER_ENTRY, "<gold>#<rank></gold> <red><time></red> on <yellow><course></yellow>");
        m.put(KEY_TIMES_INFO, "ID: <aqua><id></aqua>\nPlayer: <aqua><player_name></aqua> (<yellow><player_id></yellow>)\nCourse: <aqua><course></aqua> (<yellow><course_name></yellow>)\nTime: <aqua><time></aqua> (<yellow><time_ticks> ticks</yellow>)\nWhen Set: <aqua><when_set></aqua>\nRank: <aqua><rank></aqua>");
        m.put(KEY_TIMES_LOOKUP_NONE, "<red>Player <player> has not set a time on course <course>.</red>");
        m.put(KEY_TIMES_DELETE_ONE, "<gold>Time successfully deleted.</gold>");
        m.put(KEY_TIMES_DELETE_COURSE, "<gold>Cleared all times for <course>.</gold>");
        m.put(KEY_TIMES_DELETE_COURSE_NONE, "<gold><course> has no times to clear.</gold>");
        m.put(KEY_TIMES_DELETE_PLAYER, "<gold>Cleared all times for <player>.</gold>");
        m.put(KEY_TIMES_DELETE_PLAYER_NONE, "<gold><player> has not set any times.</gold>");
        m.put(KEY_TIMES_DELETE_ALL, "<gold>Cleared all course times.</gold>");
        m.put(KEY_TIMES_DELETE_ALL_NONE, "<gold>There are no times to clear.</gold>");
        m.put(KEY_SIGN_PLACE, "<green>Parkour sign successfully placed.</green>");
        m.put(KEY_SIGN_REPLACE, "<green>Parkour sign successfully replaced.</green>");
        m.put(KEY_SIGN_BREAK, "<gold>Parkour sign has been deleted.</gold>");
        m.put(KEY_DEBUG_ADD_FAKE_TIMES, "Fake times added to <course>");
        m.put(KEY_DEBUG_CLEAR, "Cleared all debug data");
        m.put(KEY_EXCEPTION_INVALID_COURSE_FLAG, "Invalid course flag: %s");
        m.put(KEY_EXCEPTION_UNKNOWN_COURSE_TIME, "Unknown course time: %d");
        m.put(KEY_EXCEPTION_INVALID_START_GAMEMODE, "Invalid start gamemode: %s, must be one of %s");
        m.put(KEY_EXCEPTION_INVALID_EXIT_GAMEMODE, "Invalid exit gamemode: %s, must be one of %s");
        m.put(KEY_EXCEPTION_UNKNOWN_OFFLINE_PLAYER, "Unknown player: %s");
        return m;
    }

}
