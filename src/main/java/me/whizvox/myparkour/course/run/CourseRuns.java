package me.whizvox.myparkour.course.run;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.course.Course;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.UUID;

@NotNullByDefault
public class CourseRuns {

    private final Map<UUID, CourseRun> runs;

    public CourseRuns() {
        runs = new Object2ObjectOpenHashMap<>();
    }

    public boolean isRunning(Player player) {
        return runs.containsKey(player.getUniqueId());
    }

    public boolean startRun(Player player, Course course) {
        if (!isRunning(player)) {
            return false;
        }
        CourseRun run = new CourseRun(player, course, (i1, i2) -> {}, (i1) -> {});
        runs.put(player.getUniqueId(), run);
        return true;
    }

    public void update() {
        runs.values().forEach(CourseRun::update);
    }

}
