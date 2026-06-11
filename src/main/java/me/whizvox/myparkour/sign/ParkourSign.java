package me.whizvox.myparkour.sign;

import org.bukkit.entity.Player;

public sealed interface ParkourSign permits ParkourCourseTimesSign, ParkourExitSign, ParkourRunSign, ParkourSelfTimesSign {

    void action(Player player);

}
