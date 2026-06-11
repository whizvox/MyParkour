package me.whizvox.myparkour.sign;

import me.whizvox.myparkour.course.Course;
import org.bukkit.entity.Player;

public record ParkourCourseTimesSign(Course course) implements ParkourSign {

    @Override
    public void action(Player player) {
        player.performCommand("myparkour:times course " + course.name());
    }

}
