package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.math.BlockPosition;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CommandConsumer;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.command.IntegersArgumentType;
import me.whizvox.myparkour.core.CommandExceptionTypes;
import me.whizvox.myparkour.course.*;
import me.whizvox.myparkour.course.edit.EditableCourse;
import me.whizvox.myparkour.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.myparkour.util.CommandUtils.createPermission;

public class EditCourseCommand {

    public static final Permission
        PERMISSION_CREATE = createPermission("course.create"),
        PERMISSION_EDIT = createPermission("course.edit");

    public static final String
        KEY_CREATE = "command.editCourse.create",
        KEY_EDIT = "command.editCourse.edit",
        KEY_DISCARD = "command.editCourse.discard",
        KEY_SAVE = "command.editCourse.save",
        KEY_SET_NAME = "command.editCourse.set.name",
        KEY_SET_DISPLAY_NAME = "command.editCourse.set.displayName",
        KEY_SET_START = "command.editCourse.set.start",
        KEY_SET_EXIT = "command.editCourse.set.exit",
        KEY_CHECKPOINT_ADD = "command.editCourse.checkpoint.add",
        KEY_CHECKPOINT_LIST_NONE = "command.editCourse.checkpoint.list.none",
        KEY_CHECKPOINT_LIST_HEADER = "command.editCourse.checkpoint.list.header",
        KEY_CHECKPOINT_INSERT = "command.editCourse.checkpoint.insert",
        KEY_CHECKPOINT_REMOVE = "command.editCourse.checkpoint.remove",
        KEY_CHECKPOINT_SPLIT_ADD = "command.editCourse.checkpoint.split.add",
        KEY_CHECKPOINT_SPLIT_REMOVE = "command.editCourse.checkpoint.split.remove",
        KEY_INVALID_CHECKPOINT_INDEX = "edit.invalidCheckpointIndex",
        KEY_ALREADY_EDITING_SELF = "edit.alreadyEditing.self",
        KEY_ALREADY_EDITING_OTHER = "edit.alreadyEditing.other",
        KEY_NOT_EDITING = "edit.notEditing",
        KEY_MISSING_NAME = "edit.missingName",
        KEY_MISSING_DISPLAY_NAME = "edit.missingDisplayName",
        KEY_NO_CHECKPOINTS = "edit.noCheckpoints",
        KEY_MISSING_START = "edit.missingStart",
        KEY_MISSING_EXIT = "edit.missingExit",
        KEY_NAME_UNAVAILABLE = "edit.nameUnavailable",
        KEY_NOT_FOUND = "edit.notFound",
        KEY_NOT_SPLIT = "edit.notSplit",
        KEY_INVALID_SPLIT_LOCAL_INDEX = "edit.invalidLocalSplitIndex";

    private static int createNewCourse(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String name = StringArgumentType.getString(context, "name");
        EditableCourse course = new EditableCourse();
        course.setName(name);
        course.setDisplayName(name);
        var result = MyParkour.inst().getEdits().setEditingCourse(player, course);
        switch (result) {
            case SUCCESS -> player.sendMessage(Messages.translate(KEY_CREATE));
            case PLAYER_EDITING -> player.sendMessage(Messages.translate(KEY_ALREADY_EDITING_SELF, Map.of("course",
                MyParkour.inst().getEdits().getEditing(player)
                    .map(otherCourse -> Objects.requireNonNullElse(otherCourse.getName(), "(unnamed)"))
                    .orElse("(unknown)")
            )));
            default -> throw new IllegalStateException("Illegal result while creating course: " + result);
        }
        return SINGLE_SUCCESS;
    }

    private static int editCourse(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Course course = CourseArgumentType.getCourse(context, "course");
        EditableCourse edit = new EditableCourse(course);
        var result = MyParkour.inst().getEdits().setEditingCourse(player, edit);
        switch (result) {
            case SUCCESS -> player.sendMessage(Messages.translate(KEY_EDIT));
            case PLAYER_EDITING -> {
                String courseName = MyParkour.inst().getEdits().getEditing(player)
                    .map(otherCourse -> Objects.requireNonNullElse(otherCourse.getName(), "(unnamed)"))
                    .orElseThrow();
                player.sendMessage(Messages.translate(KEY_ALREADY_EDITING_SELF, Map.of("course", courseName)));
            }
            case COURSE_EDITED -> {
                Component playerName = MyParkour.inst().getEdits().getPlayer(course.id()).map(otherPlayerId -> {
                    Player otherPlayer = Bukkit.getPlayer(otherPlayerId);
                    if (otherPlayer != null) {
                        return otherPlayer.displayName();
                    } else {
                        return Component.text(otherPlayerId.toString(), NamedTextColor.RED);
                    }
                }).orElseThrow();
                player.sendMessage(Messages.translate(KEY_ALREADY_EDITING_OTHER, Map.of("player", playerName)));
            }
            default -> throw new IllegalStateException("Illegal result while editing course: " + result);
        }
        return SINGLE_SUCCESS;
    }

    private static int stopEditing(CommandContext<CommandSourceStack> context, boolean saveChanges) {
        Player player = (Player) context.getSource().getSender();
        var result = MyParkour.inst().getEdits().stopEditingCourse(player, saveChanges);
        if (result != null) {
            switch (result) {
                case SUCCESS -> {
                    if (saveChanges) {
                        try {
                            MyParkour.inst().getCourses().save(MyParkour.inst().getPaths().coursesFile());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    player.sendMessage(Messages.translate(saveChanges ? KEY_SAVE : KEY_DISCARD));
                }
                case INVALID -> {
                    EditableCourse edit = MyParkour.inst().getEdits().getEditing(player).orElseThrow();
                    var validResult = edit.checkValid();
                    String msgKey = switch (validResult) {
                        case MISSING_NAME -> KEY_MISSING_NAME;
                        case MISSING_DISPLAY_NAME -> KEY_MISSING_DISPLAY_NAME;
                        case NO_CHECKPOINTS -> KEY_NO_CHECKPOINTS;
                        case MISSING_START -> KEY_MISSING_START;
                        case MISSING_EXIT -> KEY_MISSING_EXIT;
                        default ->
                            throw new IllegalStateException("Illegal state for editable course validation: " + validResult);
                    };
                    player.sendMessage(Messages.translate(msgKey, Map.of("name", Objects.requireNonNullElse(edit.getName(), "(unnamed)"), "id", edit.getId())));
                }
                case NAME_UNAVAILABLE -> player.sendMessage(KEY_NAME_UNAVAILABLE);
                case NOT_FOUND -> player.sendMessage(KEY_NOT_FOUND);
            }
        } else {
            player.sendMessage(Messages.translate(KEY_NOT_EDITING));
        }
        return SINGLE_SUCCESS;
    }

    private static void actOnPlayerCourse(CommandContext<CommandSourceStack> context, CommandConsumer<EditableCourse> consumer) throws CommandSyntaxException {
        try {
            MyParkour.inst().getEdits().getEditing((Player) context.getSource().getSender()).ifPresentOrElse(
                course -> {
                    try {
                        consumer.run(course);
                    } catch (CommandSyntaxException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> context.getSource().getSender().sendMessage(Messages.translate(KEY_NOT_EDITING))
            );
        } catch (RuntimeException e) {
            if (e.getCause() instanceof CommandSyntaxException cse) {
                throw cse;
            } else {
                throw e;
            }
        }
    }

    private static <T> int setProperty(CommandContext<CommandSourceStack> context, T value, BiConsumer<EditableCourse, T> setter, BiConsumer<CommandSender, T> messageSender) throws CommandSyntaxException {
        Player player = (Player) context.getSource().getSender();
        actOnPlayerCourse(context, course -> {
            setter.accept(course, value);
            messageSender.accept(player, value);
        });
        return SINGLE_SUCCESS;
    }

    private static int setStart(CommandContext<CommandSourceStack> context, Location start) throws CommandSyntaxException {
        return setProperty(
            context,
            start,
            (course, loc) -> course.setStart(new ImmutableLocation(loc)),
            (sender, loc) -> sender.sendMessage(Messages.translate(KEY_SET_START, Map.of("location", MiniMessageUtils.formatLocation(loc))))
        );
    }

    private static int setExit(CommandContext<CommandSourceStack> context, Location exit) throws CommandSyntaxException {
        return setProperty(
            context,
            exit,
            (course, loc) -> course.setExit(new ImmutableLocation(loc)),
            (sender, loc) -> sender.sendMessage(Messages.translate(KEY_SET_EXIT, Map.of("location", MiniMessageUtils.formatLocation(loc))))
        );
    }

    private static int setName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        if (MyParkour.inst().getCourses().isNameAvailable(name)) {
            setProperty(
                context,
                name,
                EditableCourse::setName,
                (sender, s) -> sender.sendMessage(Messages.translate(KEY_SET_NAME, Map.of("name", s)))
            );
        } else {
            context.getSource().getSender().sendMessage(Messages.translate(KEY_NAME_UNAVAILABLE));
        }
        return SINGLE_SUCCESS;
    }

    private static int setDisplayName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String displayName = StringArgumentType.getString(context, "displayname");
        setProperty(context, displayName, EditableCourse::setDisplayName, (sender, s) -> sender.sendMessage(Messages.translate(KEY_SET_DISPLAY_NAME, Map.of("displayname", MiniMessage.miniMessage().deserialize(s)))));
        return SINGLE_SUCCESS;
    }

    private static int listCheckpoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            if (course.getCheckpoints().isEmpty()) {
                context.getSource().getSender().sendMessage(Messages.translate(KEY_CHECKPOINT_LIST_NONE));
            } else {
                Component comp = Component.empty();
                for (int i = 0; i < course.getCheckpoints().size(); i++) {
                    Checkpoint checkpoint = course.getCheckpoints().get(i);
                    if (i > 0) {
                        comp = comp.append(Component.text(" "));
                    }
                    comp = comp.append(Component.text("[" + (i + 1) + "]", NamedTextColor.AQUA, TextDecoration.UNDERLINED).hoverEvent(HoverEvent.showText(MiniMessageUtils.formatCheckpoint(checkpoint))));
                }
                context.getSource().getSender().sendMessage(Messages.translate(KEY_CHECKPOINT_LIST_HEADER, Map.of("course_name", Objects.requireNonNullElse(course.getName(), "???"), "checkpoints", comp)));
            }
        });
        return SINGLE_SUCCESS;
    }

    private static int addCheckpoint(CommandContext<CommandSourceStack> context, Checkpoint checkpoint) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            course.addCheckpoint(checkpoint);
            context.getSource().getSender().sendMessage(Messages.translate(KEY_CHECKPOINT_ADD, Map.of("index", course.getCheckpoints().size(), "checkpoint", MiniMessageUtils.formatCheckpoint(checkpoint))));
        });
        return SINGLE_SUCCESS;
    }

    private static int removeCheckpoint(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            int index = context.getArgument("index", Integer.class);
            if (index <= course.getCheckpoints().size()) {
                course.getCheckpoints().remove(index);
                context.getSource().getSender().sendMessage(Messages.translate(KEY_CHECKPOINT_REMOVE));
            } else {
                context.getSource().getSender().sendMessage(Messages.translate(KEY_INVALID_CHECKPOINT_INDEX, Map.of("index", index)));
            }
        });
        return SINGLE_SUCCESS;
    }

    private static void checkCheckpointIndex(int index, EditableCourse course, boolean notSplit) throws CommandSyntaxException {
        if (index < 1 || index > course.getCheckpoints().size()) {
            throw CommandExceptionTypes.SPLIT_CHECKPOINTS_UNKNOWN.create(index);
        }
        if (!notSplit || course.getCheckpoints().get(index - 1) instanceof SplitCheckpoint) {
            throw CommandExceptionTypes.SPLIT_CHECKPOINTS_CANNOT_ADD_SPLIT.create(index);
        }
    }

    private static int addSplitCheckpoint(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            IntList indices = IntegersArgumentType.getIntegers(context, "checkpoints");
            for (int i = 0; i < indices.size(); i++) {
                int index = indices.getInt(i);
                checkCheckpointIndex(index, course, true);
                for (int j = i + 1; j < indices.size(); j++) {
                    if (index == indices.getInt(j)) {
                        throw CommandExceptionTypes.SPLIT_CHECKPOINTS_NO_DUPLICATES.create(index);
                    }
                }
            }
            IntList copy = new IntArrayList(indices);
            copy.sort((o1, o2) -> o2 - o1);
            List<Checkpoint> checkpoints = new ArrayList<>(indices.size());
            copy.forEach(index -> {
                Checkpoint checkpoint = course.getCheckpoints().remove(index - 1);
                checkpoints.add(checkpoint);
            });
            Checkpoint checkpoint = new SplitCheckpoint(checkpoints);
            course.addCheckpoint(checkpoint);
            context.getSource().getSender().sendMessage(Messages.translate(KEY_CHECKPOINT_ADD, Map.of("checkpoint", checkpoint)));
        });
        return SINGLE_SUCCESS;
    }

    private static int addToSplitCheckpoint(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            int splitIndex = IntegerArgumentType.getInteger(context, "split");
            int checkpointIndex = IntegerArgumentType.getInteger(context, "checkpoint");
            checkCheckpointIndex(splitIndex, course, false);
            checkCheckpointIndex(checkpointIndex, course, true);
            if (course.getCheckpoints().get(splitIndex - 1) instanceof SplitCheckpoint(List<Checkpoint> checkpoints)) {
                List<Checkpoint> newCheckpoints = new ArrayList<>(checkpoints);
                newCheckpoints.add(course.getCheckpoints().get(checkpointIndex - 1));
                SplitCheckpoint newSplit = new SplitCheckpoint(newCheckpoints);
                course.getCheckpoints().set(splitIndex - 1, newSplit);
                course.getCheckpoints().remove(checkpointIndex - 1);
                context.getSource().getSender().sendMessage(Messages.translate(KEY_CHECKPOINT_SPLIT_ADD));
            } else {
                context.getSource().getSender().sendMessage(Messages.translate(KEY_NOT_SPLIT));
            }
        });
        return SINGLE_SUCCESS;
    }

    private static int removeFromSplitCheckpoint(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            int splitIndex = IntegerArgumentType.getInteger(context, "split");
            int localIndex = IntegerArgumentType.getInteger(context, "index");
            checkCheckpointIndex(splitIndex, course, false);
            if (course.getCheckpoints().get(splitIndex - 1) instanceof SplitCheckpoint(List<Checkpoint> checkpoints)) {
                if (localIndex >= 1 && localIndex <= checkpoints.size()) {
                    List<Checkpoint> newCheckpoints = new ArrayList<>(checkpoints);
                    newCheckpoints.remove(localIndex - 1);
                    SplitCheckpoint newSplit = new SplitCheckpoint(newCheckpoints);
                    course.getCheckpoints().set(splitIndex - 1, newSplit);
                    context.getSource().getSender().sendMessage(Messages.translate(KEY_CHECKPOINT_SPLIT_REMOVE));
                } else {
                    context.getSource().getSender().sendMessage(Messages.translate(KEY_INVALID_SPLIT_LOCAL_INDEX, Map.of("index", localIndex)));
                }
            } else {
                context.getSource().getSender().sendMessage(Messages.translate(KEY_NOT_SPLIT));
            }
        });
        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(Commands.literal("editcourse")
            .requires(source -> CommandUtils.playerHasPermission(source, PERMISSION_EDIT))
            .then(Commands.literal("create")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_CREATE))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(EditCourseCommand::createNewCourse)
                )
            )
            .then(Commands.literal("edit")
                .then(Commands.argument("course", CourseArgumentType.course())
                    .executes(EditCourseCommand::editCourse)
                )
            )
            .then(Commands.literal("set")
                .then(Commands.literal("name")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(EditCourseCommand::setName)
                    )
                )
                .then(Commands.literal("displayname")
                    .then(Commands.argument("displayname", StringArgumentType.greedyString())
                        .executes(EditCourseCommand::setDisplayName)
                    )
                )
                .then(Commands.literal("start")
                    .then(Commands.argument("position", ArgumentTypes.finePosition())
                        .executes(context -> setStart(
                            context,
                            context.getArgument("position", FinePositionResolver.class).resolve(context.getSource()).toLocation(((Player) context.getSource().getSender()).getWorld())
                        ))
                    )
                    .executes(context -> setStart(context, ((Player) context.getSource().getSender()).getLocation()))
                )
                .then(Commands.literal("exit")
                    .then(Commands.argument("position", ArgumentTypes.finePosition())
                        .executes(context -> setExit(
                            context,
                            context.getArgument("position", FinePositionResolver.class).resolve(context.getSource()).toLocation(((Player) context.getSource().getSender()).getWorld())
                        ))
                    )
                    .executes(context -> setExit(context, ((Player) context.getSource().getSender()).getLocation()))
                )
            )
                .then(Commands.literal("checkpoint")
                    .then(Commands.literal("list")
                        .executes(EditCourseCommand::listCheckpoints)
                    )
                    .then(Commands.literal("add")
                        .then(Commands.argument("position", ArgumentTypes.blockPosition())
                            .executes(context -> {
                                Player player = (Player) context.getSource().getSender();
                                BlockPosition pos = context.getArgument("position", BlockPositionResolver.class).resolve(context.getSource());
                                Location loc = pos.toLocation(player.getWorld());
                                return addCheckpoint(context, new BlockCheckpoint(new BlockLocation(loc)));
                            })
                        )
                        .executes(context -> {
                            Player player = (Player) context.getSource().getSender();
                            return addCheckpoint(context, new BlockCheckpoint(new BlockLocation(player.getLocation())));
                        })
                    )
                    .then(Commands.literal("addbox")
                        .then(Commands.argument("corner1", ArgumentTypes.blockPosition())
                            .then(Commands.argument("corner2", ArgumentTypes.blockPosition())
                                .executes(context -> {
                                    Player player = (Player) context.getSource().getSender();
                                    BlockPosition corner1 = context.getArgument("corner1", BlockPositionResolver.class).resolve(context.getSource());
                                    BlockPosition corner2 = context.getArgument("corner2", BlockPositionResolver.class).resolve(context.getSource());
                                    BoundingBox box = new BoundingBox(corner1.x(), corner1.y(), corner1.z(), corner2.x(), corner2.y(), corner2.z());
                                    return addCheckpoint(context, new BoxCheckpoint(new ImmutableBoundingBox(box), player.getWorld().getUID()));
                                })
                            )
                        )
                    )
                    .then(Commands.literal("addsplit")
                        .then(Commands.argument("checkpoints", IntegersArgumentType.integers())
                            .executes(EditCourseCommand::addSplitCheckpoint)
                        )
                    )
                    .then(Commands.literal("remove")
                        .then(Commands.argument("index", IntegerArgumentType.integer(1))
                            .executes(EditCourseCommand::removeCheckpoint)
                        )
                    )
                    .then(Commands.literal("split")
                        .then(Commands.literal("add")
                            .then(Commands.argument("split", IntegerArgumentType.integer())
                                .then(Commands.argument("checkpoint", IntegerArgumentType.integer())
                                    .executes(EditCourseCommand::addToSplitCheckpoint)
                                )
                            )
                        )
                        .then(Commands.literal("remove")
                            .then(Commands.argument("split", IntegerArgumentType.integer())
                                .then(Commands.argument("index", IntegerArgumentType.integer())
                                    .executes(EditCourseCommand::removeFromSplitCheckpoint)
                                )
                            )
                        )
                    )
                )
            .then(Commands.literal("discard")
                .executes(context -> stopEditing(context, false))
            )
            .then(Commands.literal("save")
                .executes(context -> stopEditing(context, true))
            )
            .build());
    }

}
