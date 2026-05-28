package me.whizvox.myparkour.course;

import me.whizvox.myparkour.util.ImmutableLocation;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public record SplitCheckpoint(List<Checkpoint> checkpoints) implements Checkpoint {

    public SplitCheckpoint(List<Checkpoint> checkpoints) {
        this.checkpoints = Collections.unmodifiableList(checkpoints);
    }

    public int checkCollidingWith(Player player) {
        for (int i = 0; i < checkpoints.size(); i++) {
            if (checkpoints.get(i).isCollidingWith(player)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isCollidingWith(Player player) {
        return checkCollidingWith(player) != -1;
    }

    @Override
    public ImmutableLocation respawn() {
        return checkpoints.getFirst().respawn();
    }

}
