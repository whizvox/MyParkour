package me.whizvox.myparkour.course;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public record SplitCheckpoint(List<Checkpoint> checkpoints) implements Checkpoint {

    public SplitCheckpoint(List<Checkpoint> checkpoints) {
        this.checkpoints = Collections.unmodifiableList(checkpoints);
    }

    @Override
    public boolean isCollidingWith(Player player) {
        for (Checkpoint checkpoint : checkpoints) {
            if (checkpoint.isCollidingWith(player)) {
                return true;
            }
        }
        return false;
    }

}
