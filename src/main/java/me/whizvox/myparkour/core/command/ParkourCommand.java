package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.StartGameMode;
import me.whizvox.myparkour.course.run.CourseRun;
import me.whizvox.myparkour.util.CommandUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
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
                        ItemStack[] inventory;
                        if (course.shouldClearInventory()) {
                            inventory = player.getInventory().getContents();
                            player.getInventory().clear();
                            ItemStack backItem = new ItemStack(Material.STICK);
                            backItem.setData(DataComponentTypes.ITEM_NAME, Messages.translateImmediately(Messages.KEY_ITEM_BACK_NAME));
                            backItem.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Messages.translateImmediately(Messages.KEY_ITEM_BACK_LORE)).build());
                            ItemStack restartItem = new ItemStack(Material.CLOCK);
                            restartItem.setData(DataComponentTypes.ITEM_NAME, Messages.translateImmediately(Messages.KEY_ITEM_RESTART_NAME));
                            restartItem.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Messages.translateImmediately(Messages.KEY_ITEM_RESTART_LORE)).build());
                            ItemStack exitItem = new ItemStack(Material.OAK_SAPLING);
                            exitItem.setData(DataComponentTypes.ITEM_NAME, Messages.translateImmediately(Messages.KEY_ITEM_EXIT_NAME));
                            exitItem.setData(DataComponentTypes.LORE, ItemLore.lore().addLine(Messages.translateImmediately(Messages.KEY_ITEM_EXIT_LORE)).build());
                            player.getInventory().setItem(0, backItem);
                            player.getInventory().setItem(4, restartItem);
                            player.getInventory().setItem(8, exitItem);
                        } else {
                            inventory = new ItemStack[0];
                        }
                        MyParkour.inst().getRuns().startRun(player, course, gameMode, inventory);
                        player.showTitle(Title.title(Component.empty(), Messages.translate(Messages.KEY_RUN_START_SUCCESS, Map.of("course", course.displayName())), 2, 20, 10));
                    } else {
                        player.sendMessage(Messages.translate(Messages.KEY_RUN_START_TELEPORT_FAILED));
                    }
                });
            } else {
                player.sendMessage(Messages.translate(Messages.KEY_RUN_START_ALREADY_RUNNING));
            }
        } else {
            player.sendMessage(Messages.translate(Messages.KEY_RUN_START_CLOSED));
        }
        return SINGLE_SUCCESS;
    }

    private static int teleportToLastCheckpoint(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        MyParkour.inst().getRuns().getRun(player).ifPresentOrElse(
            CourseRun::teleportToLastCheckpoint,
            () -> player.sendMessage(Messages.translate(Messages.KEY_RUN_GENERIC_NOT_RUNNING))
        );
        return SINGLE_SUCCESS;
    }

    private static int restart(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        MyParkour.inst().getRuns().getRun(player).ifPresentOrElse(
            CourseRun::restart,
            () -> player.sendMessage(Messages.translate(Messages.KEY_RUN_GENERIC_NOT_RUNNING))
        );
        return SINGLE_SUCCESS;
    }

    private static int exit(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        MyParkour.inst().getRuns().stop(player, false).ifPresentOrElse(run -> {
            player.teleportAsync(run.getCourse().exit().toLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(success -> {
                run.handleExit();
                if (success) {
                    player.sendMessage(Messages.translate(Messages.KEY_RUN_EXIT_SUCCESS, Map.of("course", run.getCourse().displayName())));
                } else {
                    player.sendMessage(Messages.translate(Messages.KEY_RUN_EXIT_TELEPORT_FAILED));
                }
            });
        }, () -> {
            player.sendMessage(Messages.translate(Messages.KEY_RUN_GENERIC_NOT_RUNNING));
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
                .then(Commands.literal("back")
                    .requires(source -> CommandUtils.playerHasPermission(source, PERMISSION_RUN))
                    .executes(ParkourCommand::teleportToLastCheckpoint)
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
