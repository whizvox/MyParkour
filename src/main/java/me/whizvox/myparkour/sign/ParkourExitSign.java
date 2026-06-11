package me.whizvox.myparkour.sign;

import org.bukkit.entity.Player;

public non-sealed class ParkourExitSign implements ParkourSign {

    @Override
    public void action(Player player) {
        player.performCommand("myparkour:parkour exit");
    }

}
