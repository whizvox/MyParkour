package me.whizvox.myparkour.course.run;

import io.papermc.paper.block.fluid.FluidData;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.course.*;
import me.whizvox.myparkour.util.SlottedItem;
import me.whizvox.myparkour.util.StringUtils;
import me.whizvox.myparkour.util.WorldUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NotNullByDefault
public class CourseRun {

    private final Player player;
    private final Course course;
    private final GameMode previousGameMode;
    private int start;
    private int currentCheckpointIndex;
    private int lastSplitCheckpointIndex;
    private Checkpoint currentCheckpoint;
    private final boolean checkWater;
    private final boolean checkLava;
    private final List<SlottedItem> prevInventory;

    public CourseRun(Player player, Course course, GameMode previousGameMode, ItemStack[] inventory) {
        this.player = player;
        this.course = course;
        this.previousGameMode = previousGameMode;
        this.start = Bukkit.getCurrentTick();
        currentCheckpointIndex = 0;
        lastSplitCheckpointIndex = 0;
        currentCheckpoint = course.checkpoints().getFirst();
        checkWater = course.flags().contains(CourseFlag.FAIL_WATER);
        checkLava = course.flags().contains(CourseFlag.FAIL_LAVA);
        prevInventory = new ArrayList<>();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack item = inventory[i];
            //noinspection ConstantValue
            if (item != null) {
                prevInventory.add(new SlottedItem(i, item));
            }
        }
    }

    private void onCheckpoint(CheckpointCause cause) {
        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.UI, 1.0f, 1.0f), Sound.Emitter.self());
        if (cause == CheckpointCause.RESTART) {
            player.showTitle(Title.title(Component.empty(), Messages.translate(Messages.KEY_RUN_START_SUCCESS, Map.of("course", course.displayName())), 2, 20, 2));
        } else if (cause == CheckpointCause.NEXT) {
            player.showTitle(Title.title(Component.empty(), Messages.translate(Messages.KEY_RUN_CHECKPOINT, Map.of("checkpoint", currentCheckpointIndex, "total", course.checkpoints().size() - 1, "time", StringUtils.formatTime(getTime()))), 2, 20, 2));
        }
    }

    private void onFinish() {
        MyParkour.inst().getRuns().stop(player, false);
        int ticks = getTime();
        String time = StringUtils.formatTime(ticks);
        var result = MyParkour.inst().getLeaderboards().log(player.getUniqueId(), course.id(), ticks);
        Component message = switch (result) {
            case FIRST_TIME ->
                Messages.translate(Messages.KEY_RUN_FINISH_FIRST, Map.of("course", course.displayName(), "time", time));
            case NEW_PERSONAL_BEST ->
                Messages.translate(Messages.KEY_RUN_FINISH_NEW_BEST, Map.of("course", course.displayName(), "time", time));
            case NO_CHANGE ->
                Messages.translate(Messages.KEY_RUN_FINISH_NO_CHANGE, Map.of("course", course.displayName(), "time", time));
        };
        player.sendMessage(message);
        player.teleportAsync(course.exit().toLocation()).thenAccept(success -> {
            if (!success) {
                player.sendMessage(Messages.translate(Messages.KEY_RUN_EXIT_TELEPORT_FAILED));
            }
            handleExit();
        });
    }

    public StoredRun store() {
        ExitGameMode exitGameMode;
        if (course.exitGameMode() == ExitGameMode.DEFAULT) {
            exitGameMode = MyParkour.inst().getPluginConfig().getDefaultExitGameMode().orElse(ExitGameMode.NONE);
        } else {
            exitGameMode = course.exitGameMode();
        }
        GameMode exitMode = switch (exitGameMode) {
            case SURVIVAL -> GameMode.SURVIVAL;
            case ADVENTURE -> GameMode.ADVENTURE;
            case CREATIVE -> GameMode.CREATIVE;
            case SPECTATOR -> GameMode.SPECTATOR;
            default -> previousGameMode;
        };
        return new StoredRun(
            player.getUniqueId(),
            new ArrayList<>(prevInventory),
            course.exit(),
            exitMode
        );
    }

    public void handleExit() {
        ExitGameMode exitGameMode;
        if (course.exitGameMode() == ExitGameMode.DEFAULT) {
            exitGameMode = MyParkour.inst().getPluginConfig().getDefaultExitGameMode().orElse(ExitGameMode.NONE);
        } else {
            exitGameMode = course.exitGameMode();
        }
        switch (exitGameMode) {
            case SURVIVAL -> player.setGameMode(GameMode.SURVIVAL);
            case ADVENTURE -> player.setGameMode(GameMode.ADVENTURE);
            case CREATIVE -> player.setGameMode(GameMode.CREATIVE);
            case SPECTATOR -> player.setGameMode(GameMode.SPECTATOR);
            case PREVIOUS -> player.setGameMode(previousGameMode);
        }
        if (course.shouldClearInventory()) {
            player.getInventory().clear();
            prevInventory.forEach(item -> player.getInventory().setItem(item.slot(), item.item()));
        }
    }

    public Course getCourse() {
        return course;
    }

    public boolean hasCompleted() {
        return currentCheckpointIndex >= course.checkpoints().size();
    }

    public int getTime() {
        return Bukkit.getCurrentTick() - start;
    }

    public void restart() {
        currentCheckpointIndex = 0;
        currentCheckpoint = course.checkpoints().getFirst();
        start = Bukkit.getCurrentTick();
        onCheckpoint(CheckpointCause.RESTART);
    }

    public void teleportToLastCheckpoint() {
        Location loc;
        if (currentCheckpointIndex == 0) {
            loc = course.start().toLocation();
        } else {
            Checkpoint checkpoint = course.checkpoints().get(currentCheckpointIndex - 1);
            if (checkpoint instanceof SplitCheckpoint split && lastSplitCheckpointIndex >= 0 && lastSplitCheckpointIndex < split.checkpoints().size()) {
                loc = split.checkpoints().get(lastSplitCheckpointIndex).respawn().toLocation();
            } else {
                loc = checkpoint.respawn().toLocation();
            }
        }
        player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN).thenAccept(success -> {
            if (success) {
                onCheckpoint(CheckpointCause.FAIL);
            } else {
                MyParkour.inst().getLogger().warning("Could not teleport player to last checkpoint: player=%s (%s)".formatted(player.getName(), player.getUniqueId()));
            }
        });
    }

    public void update() {
        if (checkWater || checkLava) {
            boolean shouldReset = WorldUtils.getBlocksTouchingPlayer(player).anyMatch(block -> {
                FluidData fluid = block.getWorld().getFluidData(block.getX(), block.getY(), block.getZ());
                return fluid.getFluidType() != Fluid.EMPTY &&
                    ((checkWater && (fluid.getFluidType() == Fluid.WATER || fluid.getFluidType() == Fluid.FLOWING_WATER)) ||
                        (checkLava && (fluid.getFluidType() == Fluid.LAVA || fluid.getFluidType() == Fluid.FLOWING_LAVA)));
            });
            if (shouldReset) {
                teleportToLastCheckpoint();
                return;
            }
        }
        if (player.getY() < course.minY()) {
            teleportToLastCheckpoint();
            return;
        }
        if (currentCheckpoint.isCollidingWith(player)) {
            if (currentCheckpoint instanceof SplitCheckpoint split) {
                lastSplitCheckpointIndex = split.checkCollidingWith(player);
                if (lastSplitCheckpointIndex == -1) {
                    MyParkour.inst().getLogger().warning("Could not properly update lastSplitCheckpointIndex");
                }
            }
            currentCheckpointIndex++;
            if (currentCheckpointIndex < course.checkpoints().size()) {
                currentCheckpoint = course.checkpoints().get(currentCheckpointIndex);
                onCheckpoint(CheckpointCause.NEXT);
            } else {
                onFinish();
            }
        }
    }

}
