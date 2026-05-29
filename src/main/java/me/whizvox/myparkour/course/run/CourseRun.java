package me.whizvox.myparkour.course.run;

import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.core.command.ParkourCommand;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.CourseFlag;
import me.whizvox.myparkour.course.SplitCheckpoint;
import me.whizvox.myparkour.util.StringUtils;
import me.whizvox.myparkour.util.WorldUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;

@NotNullByDefault
public class CourseRun {

    private final Player player;
    private final Course course;
    private int start;
    private int currentCheckpointIndex;
    private int lastSplitCheckpointIndex;
    private Checkpoint currentCheckpoint;
    private boolean checkWater, checkLava;

    public CourseRun(Player player, Course course) {
        this.player = player;
        this.course = course;
        this.start = Bukkit.getCurrentTick();
        currentCheckpointIndex = 0;
        lastSplitCheckpointIndex = 0;
        currentCheckpoint = course.checkpoints().getFirst();
        checkWater = course.flags().contains(CourseFlag.FAIL_WATER);
        checkLava = course.flags().contains(CourseFlag.FAIL_LAVA);
    }

    private void onCheckpoint(CheckpointCause cause) {
        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.UI, 1.0f, 1.0f), Sound.Emitter.self());
        if (cause == CheckpointCause.RESTART) {
            player.showTitle(Title.title(Component.empty(), Messages.translate("myparkour.run.start", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()))), 2, 20, 2));
        } else if (cause == CheckpointCause.NEXT) {
            player.showTitle(Title.title(Component.empty(), Messages.translate("myparkour.run.nextCheckpoint", Map.of("checkpoint", currentCheckpointIndex, "total", course.checkpoints().size() - 1, "time", StringUtils.formatTime(getTime()))), 2, 20, 2));
        }
    }

    private void onFinish() {
        player.teleportAsync(course.exit().toLocation()).thenAccept(success -> {
            MyParkour.inst().getRuns().stop(player);
            int ticks = getTime();
            String time = StringUtils.formatTime(ticks);
            var result = MyParkour.inst().getLeaderboards().log(player.getUniqueId(), course.id(), ticks);
            Component message = switch (result) {
                case FIRST_TIME -> Messages.translate("myparkour.run.finish.firstTime", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()), "time", time));
                case NEW_PERSONAL_BEST -> Messages.translate("myparkour.run.finish.personalBest", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()), "time", time));
                case NO_CHANGE -> Messages.translate("myparkour.run.finish.noChange", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()), "time", time));
            };
            player.sendMessage(message);
            if (!success) {
                player.sendMessage(Messages.translate("myparkour.error.run.teleportFailed.exit"));
            }
        });
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
            for (Block block : WorldUtils.getTouchingBlocks(player)) {
                if ((checkWater && block.getType() == Material.WATER) || (checkLava && block.getType() == Material.LAVA)) {
                    teleportToLastCheckpoint();
                }
            }
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
