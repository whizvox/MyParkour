package me.whizvox.myparkour.sign;

import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.course.Course;
import org.bukkit.entity.Player;

public record ParkourRunSign(Course course) implements ParkourSign {

    @Override
    public void action(Player player) {
        if (course.open()) {
            player.performCommand("myparkour:parkour run " + course.name());
        } else {
            player.sendMessage(Messages.translate(Messages.KEY_RUN_START_CLOSED));
        }
    }

}
