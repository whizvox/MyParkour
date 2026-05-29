package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.util.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ParkourCommand {

    public static final Permission PERMISSION_RUN = new Permission("myparkour.run");

    private static int run(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Course course = CourseArgumentType.getCourse(context, "course");
        if (course.open()) {
            if (MyParkour.inst().getRuns().getRun(player).isEmpty()) {
                player.teleportAsync(course.start().toLocation()).thenAccept(success -> {
                    if (success) {
                        MyParkour.inst().getRuns().startRun(player, course);
                        player.showTitle(Title.title(Messages.translate("myparkour.run.start"), Component.empty(), 2, 20, 10));
                    } else {
                        player.sendMessage(Messages.translate("myparkour.error.run.teleportFailed.start"));
                    }
                });
            } else {
                player.sendMessage(Messages.translate("myparkour.error.run.alreadyRunning"));
            }
        } else {
            player.sendMessage(Messages.translate("myparkour.error.run.notOpen"));
        }
        return SINGLE_SUCCESS;
    }

    private static int exit(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        MyParkour.inst().getRuns().stop(player).ifPresentOrElse(run -> {
            player.teleportAsync(run.getCourse().exit().toLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(success -> {
                if (success) {
                    player.sendMessage(Messages.translate("myparkour.run.exit"));
                } else {
                    player.sendMessage(Messages.translate("myparkour.error.run.teleportFailed.exit"));
                }
            });
        }, () -> {
            player.sendMessage(Messages.translate("myparkour.error.run.notRunning"));
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
