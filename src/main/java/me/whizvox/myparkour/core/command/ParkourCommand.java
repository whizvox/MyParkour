package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.StartGameMode;
import me.whizvox.myparkour.course.run.CourseRun;
import me.whizvox.myparkour.util.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ParkourCommand {

    public static final Permission PERMISSION_RUN = new Permission("myparkour.run");

    private static int run(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Course course = CourseArgumentType.getCourse(context, "course");
        GameMode gameMode = player.getGameMode();
        if (course.open()) {
            if (MyParkour.inst().getRuns().getRun(player).isEmpty()) {
                player.teleportAsync(course.start().toLocation()).thenAccept(success -> {
                    if (success) {
                        StartGameMode startGameMode;
                        if (course.startGameMode() == StartGameMode.DEFAULT) {
                            startGameMode = MyParkour.inst().getPluginConfig().getDefaultStartGameMode().orElse(StartGameMode.NONE);
                        } else {
                            startGameMode = course.startGameMode();
                        }
                        switch (startGameMode) {
                            case SURVIVAL -> player.setGameMode(GameMode.SURVIVAL);
                            case ADVENTURE -> player.setGameMode(GameMode.ADVENTURE);
                            case CREATIVE -> player.setGameMode(GameMode.CREATIVE);
                        }
                        MyParkour.inst().getRuns().startRun(player, course, gameMode);
                        player.showTitle(Title.title(Component.empty(), Messages.translate("myparkour.run.start", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()))), 2, 20, 10));
                    } else {
                        player.sendMessage(Messages.translate("myparkour.run.error.teleportFailed.start"));
                    }
                });
            } else {
                player.sendMessage(Messages.translate("myparkour.run.error.alreadyRunning"));
            }
        } else {
            player.sendMessage(Messages.translate("myparkour.run.error.notOpen"));
        }
        return SINGLE_SUCCESS;
    }

    private static int restart(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        MyParkour.inst().getRuns().getRun(player).ifPresentOrElse(
            CourseRun::restart,
            () -> player.sendMessage(Messages.translate("myparkour.run.error.notRunning"))
        );
        return SINGLE_SUCCESS;
    }

    private static int exit(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        MyParkour.inst().getRuns().stop(player).ifPresentOrElse(run -> {
            player.teleportAsync(run.getCourse().exit().toLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(success -> {
                if (success) {
                    player.sendMessage(Messages.translate("myparkour.run.exit", Map.of("course", MiniMessage.miniMessage().deserialize(run.getCourse().displayName()))));
                } else {
                    player.sendMessage(Messages.translate("myparkour.run.error.teleportFailed.exit"));
                }
            });
        }, () -> {
            player.sendMessage(Messages.translate("myparkour.run.error.notRunning"));
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
                .then(Commands.literal("restart")
                    .requires(source -> CommandUtils.playerHasPermission(source, PERMISSION_RUN))
                    .executes(ParkourCommand::restart)
                )
                .then(Commands.literal("exit")
                    .requires(source -> CommandUtils.playerHasPermission(source, PERMISSION_RUN))
                    .executes(ParkourCommand::exit)
                )
            .build()
        );
    }

}
