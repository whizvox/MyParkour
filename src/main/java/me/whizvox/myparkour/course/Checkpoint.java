package me.whizvox.myparkour.course;

import me.whizvox.myparkour.util.ImmutableLocation;
import org.bukkit.entity.Player;

public sealed interface Checkpoint permits BlockCheckpoint, BoxCheckpoint, SplitCheckpoint {

    boolean isCollidingWith(Player player);

    ImmutableLocation respawn();

}
