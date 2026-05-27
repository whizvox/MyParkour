package me.whizvox.myparkour.course;

import org.bukkit.entity.Player;

public sealed interface Checkpoint permits BlockCheckpoint, BoxCheckpoint, SplitCheckpoint {

    boolean isCollidingWith(Player player);

}
