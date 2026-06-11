package me.whizvox.myparkour.sign;

import org.bukkit.entity.Player;

public non-sealed class ParkourSelfTimesSign implements ParkourSign {

    @Override
    public void action(Player player) {
        player.performCommand("myparkour:times self");
    }

}
