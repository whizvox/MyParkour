package me.whizvox.myparkour.course.run;

import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.Course;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public class CourseRun {

    private final Player player;
    private final Course course;
    private int start;
    private int currentCheckpointIndex;
    private Checkpoint currentCheckpoint;

    private final OnCheckpoint onCheckpoint;
    private final OnComplete onComplete;

    public CourseRun(Player player, Course course, OnCheckpoint onCheckpoint, OnComplete onComplete) {
        this.player = player;
        this.course = course;
        this.onCheckpoint = onCheckpoint;
        this.onComplete = onComplete;
        this.start = Bukkit.getCurrentTick();
        currentCheckpointIndex = 0;
        currentCheckpoint = course.checkpoints().getFirst();
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
        onCheckpoint.onCheckpoint(OnCheckpoint.Cause.RESTART, 0);
    }

    public void update() {
        if (currentCheckpoint.isCollidingWith(player)) {
            currentCheckpointIndex++;
            if (currentCheckpointIndex < course.checkpoints().size()) {
                currentCheckpoint = course.checkpoints().get(currentCheckpointIndex);
                onComplete.onComplete(getTime());
            } else {
                onCheckpoint.onCheckpoint(OnCheckpoint.Cause.NEXT, currentCheckpointIndex);
            }
        }
    }

}
