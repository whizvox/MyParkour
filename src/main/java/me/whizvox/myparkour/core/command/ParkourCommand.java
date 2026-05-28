package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.run.CourseRun;
import me.whizvox.myparkour.util.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ParkourCommand {

    public static final Permission PERMISSION_RUN = new Permission("myparkour.run");

    public static final String
        KEY_RUN_START = "myparkour.run.start",
        KEY_RUN_EXIT = "myparkour.run.exit",
        KEY_RUN_ALREADY_RUNNING = "myparkour.run.alreadyRunning",
        KEY_RUN_NOT_RUNNING = "myparkour.run.notRunning",
        KEY_RUN_NOT_OPEN = "myparkour.run.notOpen",
        KEY_RUN_TELEPORT_START_FAILED = "myparkour.run.teleportStartFailed";

    private static int run(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Course course = CourseArgumentType.getCourse(context, "course");
        if (course.open()) {
            if (MyParkour.inst().getRuns().getRun(player).isEmpty()) {
                player.teleportAsync(course.start().toLocation()).thenAccept(success -> {
                    if (success) {
                        MyParkour.inst().getRuns().startRun(player, course);
                        player.showTitle(Title.title(Messages.translate(KEY_RUN_START), Component.empty(), 2, 20, 10));
                    } else {
                        player.sendMessage(Messages.translate(KEY_RUN_TELEPORT_START_FAILED));
                    }
                });
            } else {
                player.sendMessage(Messages.translate(KEY_RUN_ALREADY_RUNNING));
            }
        } else {
            player.sendMessage(Messages.translate(KEY_RUN_NOT_OPEN));
        }
        return SINGLE_SUCCESS;
    }

    private static int exit(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        MyParkour.inst().getRuns().stop(player).ifPresentOrElse(run -> {
            player.teleportAsync(run.getCourse().exit().toLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(success -> {
                if (success) {
                    player.sendMessage(Messages.translate(KEY_RUN_EXIT));
                } else {
                    player.sendMessage(Messages.translate(CourseRun.KEY_RUN_FAILED_TO_EXIT));
                }
            });
        }, () -> {
            player.sendMessage(Messages.translate(KEY_RUN_NOT_RUNNING));
        });
        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(Commands.literal("parkour")
                .then(Commands.literal("run")
                    .requires(source -> CommandUtils.playerHasPermission(source, PERMISSION_RUN))
                    .then(Commands.argument("course", CourseArgumentType.course())
                        .executes(ParkourCommand::run)
                    )
                )
                .then(Commands.literal("exit")
                    .requires(source -> CommandUtils.playerHasPermission(source, PERMISSION_RUN))
                    .executes(ParkourCommand::exit)
                )
            .build()
        );
    }

}
