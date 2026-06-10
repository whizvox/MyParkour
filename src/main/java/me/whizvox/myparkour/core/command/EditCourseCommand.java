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
import me.whizvox.myparkour.command.*;
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
import org.jetbrains.annotations.Nullable;

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

    private static int createNewCourse(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        String name = StringArgumentType.getString(context, "name");
        EditableCourse course = new EditableCourse();
        course.setName(name);
        course.setDisplayName(name);
        var result = MyParkour.inst().getEdits().setEditingCourse(player, course);
        switch (result) {
            case SUCCESS ->
                player.sendMessage(Messages.translate("myparkour.edit.create.success", Map.of("course", name)));
            case PLAYER_EDITING ->
                player.sendMessage(Messages.translate("myparkour.edit.warning.alreadyEditing", Map.of("course",
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
        if (!course.open()) {
            EditableCourse edit = new EditableCourse(course);
            var result = MyParkour.inst().getEdits().setEditingCourse(player, edit);
            switch (result) {
                case SUCCESS ->
                    player.sendMessage(Messages.translate("myparkour.edit.edit.success", Map.of("course", course.name())));
                case PLAYER_EDITING -> {
                    String courseName = MyParkour.inst().getEdits().getEditing(player)
                        .map(otherCourse -> Objects.requireNonNullElse(otherCourse.getName(), "???"))
                        .orElseThrow();
                    player.sendMessage(Messages.translate("myparkour.edit.warning.alreadyEditing.self", Map.of("course", courseName)));
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
                    player.sendMessage(Messages.translate("myparkour.edit.warning.alreadyEditing.other", Map.of("player", playerName)));
                }
                default -> throw new IllegalStateException("Illegal result while editing course: " + result);
            }
        } else {
            player.sendMessage(Messages.translate("myparkour.edit.edit.cannotBeOpen", Map.of("course", course.name())));
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
                    player.sendMessage(Messages.translate(saveChanges ? "myparkour.edit.save" : "myparkour.edit.discard"));
                }
                case INVALID -> {
                    EditableCourse edit = MyParkour.inst().getEdits().getEditing(player).orElseThrow();
                    var validResult = edit.checkValid();
                    String msgKey = switch (validResult) {
                        case MISSING_NAME -> "myparkour.edit.save.missingName";
                        case MISSING_DISPLAY_NAME -> "myparkour.edit.save.missingDisplayName";
                        case NO_CHECKPOINTS -> "myparkour.edit.save.noCheckpoints";
                        case MISSING_START -> "myparkour.edit.save.missingStart";
                        case MISSING_EXIT -> "myparkour.edit.save.missingExit";
                        default ->
                            throw new IllegalStateException("Illegal state for editable course validation: " + validResult);
                    };
                    player.sendMessage(Messages.translate(msgKey, Map.of("name", Objects.requireNonNullElse(edit.getName(), "(unnamed)"), "id", edit.getId())));
                }
                case NAME_UNAVAILABLE -> player.sendMessage("myparkour.edit.save.nameUnavailable");
                case NOT_FOUND -> player.sendMessage("myparkour.edit.save.notFound");
            }
        } else {
            player.sendMessage(Messages.translate("myparkour.edit.warning.notEditing"));
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
                () -> context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.warning.notEditing"))
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
            (sender, loc) -> sender.sendMessage(Messages.translate("myparkour.edit.set.start", Map.of("location", MiniMessageUtils.formatLocation(loc))))
        );
    }

    private static int setExit(CommandContext<CommandSourceStack> context, Location exit) throws CommandSyntaxException {
        return setProperty(
            context,
            exit,
            (course, loc) -> course.setExit(new ImmutableLocation(loc)),
            (sender, loc) -> sender.sendMessage(Messages.translate("myparkour.edit.set.exit", Map.of("location", MiniMessageUtils.formatLocation(loc))))
        );
    }

    private static int setName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = StringArgumentType.getString(context, "name");
        if (MyParkour.inst().getCourses().isNameAvailable(name)) {
            setProperty(
                context,
                name,
                EditableCourse::setName,
                (sender, s) -> sender.sendMessage(Messages.translate("myparkour.edit.set.name", Map.of("name", s)))
            );
        } else {
            context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.warning.nameUnavailable"));
        }
        return SINGLE_SUCCESS;
    }

    private static int setDisplayName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String displayName = StringArgumentType.getString(context, "displayname");
        setProperty(context, displayName, EditableCourse::setDisplayName, (sender, s) -> sender.sendMessage(Messages.translate("myparkour.edit.set.displayName", Map.of("displayname", MiniMessage.miniMessage().deserialize(s)))));
        return SINGLE_SUCCESS;
    }

    private static int setStartGameMode(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        StartGameMode gamemode = StartGameModeArgumentType.getStartGameMode(context, "gamemode");
        setProperty(context, gamemode, EditableCourse::setStartGameMode, (sender, gm) -> sender.sendMessage(Messages.translate("myparkour.edit.set.startGameMode", Map.of("gamemode", gm.repr))));
        return SINGLE_SUCCESS;
    }

    private static int setExitGameMode(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ExitGameMode gamemode = ExitGameModeArgumentType.getExitGameMode(context, "gamemode");
        setProperty(context, gamemode, EditableCourse::setExitGameMode, (sender, gm) -> sender.sendMessage(Messages.translate("myparkour.edit.set.startExitMode", Map.of("gamemode", gm.repr))));
        return SINGLE_SUCCESS;
    }

    private static int listCheckpoints(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            if (course.getCheckpoints().isEmpty()) {
                context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.checkpoint.list.none"));
            } else {
                Component comp = Component.empty();
                for (int i = 0; i < course.getCheckpoints().size(); i++) {
                    Checkpoint checkpoint = course.getCheckpoints().get(i);
                    if (i > 0) {
                        comp = comp.append(Component.text(" "));
                    }
                    comp = comp.append(Component.text("[" + (i + 1) + "]", NamedTextColor.AQUA, TextDecoration.UNDERLINED).hoverEvent(HoverEvent.showText(MiniMessageUtils.formatCheckpoint(checkpoint))));
                }
                context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.checkpoint.list.all", Map.of("course_name", Objects.requireNonNullElse(course.getName(), "???"), "checkpoints", comp)));
            }
        });
        return SINGLE_SUCCESS;
    }

    private static void addCheckpoint(CommandContext<CommandSourceStack> context, Checkpoint checkpoint, int index) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            int finalIndex;
            if (index > 0 && index <= course.getCheckpoints().size()) {
                course.insertCheckpoint(index - 1, checkpoint);
                finalIndex = index;
            } else {
                course.addCheckpoint(checkpoint);
                finalIndex = course.getCheckpoints().size();
            }
            context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.checkpoint.add", Map.of("index", finalIndex, "checkpoint", MiniMessageUtils.formatCheckpoint(checkpoint))));
        });
    }

    private static int addBlockCheckpoint(CommandContext<CommandSourceStack> context, int index, @Nullable BlockPositionResolver blockPos, @Nullable FinePositionResolver respawnPos) throws CommandSyntaxException {
        Player player = (Player) context.getSource().getSender();
        BlockLocation blockLoc = new BlockLocation(blockPos == null ? player.getLocation() : blockPos.resolve(context.getSource()).toLocation(player.getWorld()));
        ImmutableLocation respawnLoc = new ImmutableLocation(respawnPos == null ? player.getLocation() : respawnPos.resolve(context.getSource()).toLocation(player.getWorld()));
        addCheckpoint(context, new BlockCheckpoint(blockLoc, respawnLoc), index);
        return SINGLE_SUCCESS;
    }

    private static int addBoxCheckpoint(CommandContext<CommandSourceStack> context, int index, @Nullable FinePositionResolver respawnPos) throws CommandSyntaxException {
        Player player = (Player) context.getSource().getSender();
        BlockPosition corner1 = context.getArgument("corner1", BlockPositionResolver.class).resolve(context.getSource());
        BlockPosition corner2 = context.getArgument("corner2", BlockPositionResolver.class).resolve(context.getSource());
        ImmutableLocation respawn = new ImmutableLocation(respawnPos == null ? player.getLocation() : respawnPos.resolve(context.getSource()).toLocation(player.getWorld()));
        int x1 = Math.min(corner1.blockX(), corner2.blockX());
        int x2 = Math.max(corner1.blockX(), corner2.blockX()) + 1;
        int y1 = Math.min(corner1.blockY(), corner2.blockY());
        int y2 = Math.max(corner1.blockY(), corner2.blockY()) + 1;
        int z1 = Math.min(corner1.blockZ(), corner2.blockZ());
        int z2 = Math.max(corner1.blockZ(), corner2.blockZ()) + 1;
        ImmutableBoundingBox box = new ImmutableBoundingBox(x1, y1, z1, x2, y2, z2);
        addCheckpoint(context, new BoxCheckpoint(box, player.getWorld().getUID(), respawn), index);
        return SINGLE_SUCCESS;
    }

    private static int removeCheckpoint(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            int index = context.getArgument("index", Integer.class);
            if (index <= course.getCheckpoints().size()) {
                course.getCheckpoints().remove(index - 1);
                context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.checkpoint.remove", Map.of("index", index)));
            } else {
                context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.warning.invalidCheckpointIndex", Map.of("index", index)));
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
            context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.checkpoint.add", Map.of("index", course.getCheckpoints().size(), "checkpoint", MiniMessageUtils.formatCheckpoint(checkpoint))));
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
                context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.checkpoint.split.add"));
            } else {
                context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.warning.notSplit"));
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
                    context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.split.remove"));
                } else {
                    context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.warning.invalidSplitLocalIndex", Map.of("index", localIndex)));
                }
            } else {
                context.getSource().getSender().sendMessage(Messages.translate("myparkour.edit.warning.notSplit"));
            }
        });
        return SINGLE_SUCCESS;
    }

    private static int changeFlagStatus(CommandContext<CommandSourceStack> context, boolean shouldSet) throws CommandSyntaxException {
        actOnPlayerCourse(context, course -> {
            CommandSender sender = context.getSource().getSender();
            CourseFlag flag = CourseFlagArgumentType.getFlag(context, "flag");
            if (shouldSet) {
                if (course.addFlag(flag)) {
                    sender.sendMessage(Messages.translate("myparkour.edit.flag.set.success", Map.of("flag", flag, "course", Objects.requireNonNullElse(course.getName(), "???"))));
                } else {
                    sender.sendMessage(Messages.translate("myparkour.edit.flag.set.alreadySet"));
                }
            } else {
                if (course.removeFlag(flag)) {
                    sender.sendMessage(Messages.translate("myparkour.edit.flag.unset.success", Map.of("flag", flag, "course", Objects.requireNonNullElse(course.getName(), "???"))));
                } else {
                    sender.sendMessage(Messages.translate("myparkour.edit.flag.unset.notSet"));
                }
            }
        });
        return SINGLE_SUCCESS;
    }

    private static int changeOpenStatus(CommandContext<CommandSourceStack> context, boolean shouldOpen) {
        Course course = CourseArgumentType.getCourse(context, "course");
        CommandSender sender = context.getSource().getSender();
        if (shouldOpen != course.open()) {
            EditableCourse edit = new EditableCourse(course);
            edit.setOpen(shouldOpen);
            var result = MyParkour.inst().getCourses().edit(edit);
            if (result == Courses.EditResult.SUCCESS) {
                sender.sendMessage(Messages.translate(shouldOpen ? "myparkour.edit.open" : "myparkour.edit.close", Map.of("course", course.name())));
            } else {
                sender.sendMessage(Messages.translate("myparkour.edit.error.couldNotSave", Map.of("reason", result)));
            }
        } else {
            sender.sendMessage(Messages.translate(shouldOpen ? "myparkour.edit.open.alreadyOpen" : "myparkour.edit.close.alreadyClosed"));
        }
        return SINGLE_SUCCESS;
    }

    private static int deleteCourse(CommandContext<CommandSourceStack> context) {
        Course course = CourseArgumentType.getCourse(context, "course");
        CommandSender sender = context.getSource().getSender();
        if (!course.open()) {
            MyParkour.inst().getCourses().remove(course.id());
            MyParkour.inst().getLeaderboards().deleteByCourse(course.id());
            sender.sendMessage(Messages.translate("myparkour.edit.delete.success"));
        } else {
            sender.sendMessage(Messages.translate("myparkour.edit.delete.open", Map.of("course", course.name())));
        }
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
                .then(Commands.literal("startgamemode")
                    .then(Commands.argument("gamemode", StartGameModeArgumentType.startGameMode())
                        .executes(EditCourseCommand::setStartGameMode)
                    )
                )
                .then(Commands.literal("exitgamemode")
                    .then(Commands.argument("gamemode", ExitGameModeArgumentType.exitGameMode())
                        .executes(EditCourseCommand::setExitGameMode)
                    )
                )
            )
            .then(Commands.literal("checkpoint")
                .then(Commands.literal("list")
                    .executes(EditCourseCommand::listCheckpoints)
                )
                .then(Commands.literal("add")
                    .then(Commands.literal("block")
                        .then(Commands.argument("position", ArgumentTypes.blockPosition())
                            .then(Commands.argument("respawn", ArgumentTypes.finePosition())
                                .executes(context -> addBlockCheckpoint(
                                    context,
                                    -1,
                                    context.getArgument("position", BlockPositionResolver.class),
                                    context.getArgument("respawn", FinePositionResolver.class)
                                ))
                            )
                            .executes(context -> addBlockCheckpoint(
                                context,
                                -1,
                                context.getArgument("position", BlockPositionResolver.class),
                                null
                            ))
                        )
                        .executes(context -> addBlockCheckpoint(context, -1, null, null))
                    )
                    .then(Commands.literal("box")
                        .then(Commands.argument("corner1", ArgumentTypes.blockPosition())
                            .then(Commands.argument("corner2", ArgumentTypes.blockPosition())
                                .then(Commands.argument("respawn", ArgumentTypes.finePosition())
                                    .executes(context -> addBoxCheckpoint(
                                        context,
                                        -1,
                                        context.getArgument("respawn", FinePositionResolver.class)
                                    ))
                                )
                                .executes(context -> addBoxCheckpoint(
                                    context,
                                    -1,
                                    null
                                ))
                            )
                        )
                    )
                    .then(Commands.literal("split")
                        .then(Commands.argument("checkpoints", IntegersArgumentType.integers())
                            .executes(EditCourseCommand::addSplitCheckpoint)
                        )
                    )
                )
                .then(Commands.literal("insert")
                    .then(Commands.argument("index", IntegerArgumentType.integer(1))
                        .then(Commands.literal("block")
                            .then(Commands.argument("position", ArgumentTypes.blockPosition())
                                .then(Commands.argument("respawn", ArgumentTypes.finePosition())
                                    .executes(context -> addBlockCheckpoint(
                                        context,
                                        IntegerArgumentType.getInteger(context, "index"),
                                        context.getArgument("position", BlockPositionResolver.class),
                                        context.getArgument("respawn", FinePositionResolver.class)
                                    ))
                                )
                                .executes(context -> addBlockCheckpoint(
                                    context,
                                    IntegerArgumentType.getInteger(context, "index"),
                                    context.getArgument("position", BlockPositionResolver.class),
                                    null
                                ))
                            )
                            .executes(context -> addBlockCheckpoint(context, -1, null, null))
                        )
                        .then(Commands.literal("box")
                            .then(Commands.argument("corner1", ArgumentTypes.blockPosition())
                                .then(Commands.argument("corner2", ArgumentTypes.blockPosition())
                                    .then(Commands.argument("respawn", ArgumentTypes.finePosition())
                                        .executes(context -> addBoxCheckpoint(
                                            context,
                                            IntegerArgumentType.getInteger(context, "index"),
                                            context.getArgument("respawn", FinePositionResolver.class)
                                        ))
                                    )
                                    .executes(context -> addBoxCheckpoint(
                                        context,
                                        IntegerArgumentType.getInteger(context, "index"),
                                        null
                                    ))
                                )
                            )
                        )
                        .then(Commands.literal("split")
                            .then(Commands.argument("checkpoints", IntegersArgumentType.integers())
                                .executes(EditCourseCommand::addSplitCheckpoint)
                            )
                        )
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
            .then(Commands.literal("flag")
                .then(Commands.literal("set")
                    .then(Commands.argument("flag", CourseFlagArgumentType.flag())
                        .executes(context -> changeFlagStatus(context, true))
                    )
                )
                .then(Commands.literal("unset")
                    .then(Commands.argument("flag", CourseFlagArgumentType.flag())
                        .executes(context -> changeFlagStatus(context, false))
                    )
                )
            )
            .then(Commands.literal("open")
                .then(Commands.argument("course", CourseArgumentType.course())
                    .executes(context -> changeOpenStatus(context, true))
                )
            )
            .then(Commands.literal("close")
                .then(Commands.argument("course", CourseArgumentType.course())
                    .executes(context -> changeOpenStatus(context, false))
                )
            )
            .then(Commands.literal("delete")
                .then(Commands.argument("course", CourseArgumentType.course())
                    .executes(EditCourseCommand::deleteCourse)
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
