package me.whizvox.myparkour.course.run;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.course.Course;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NotNullByDefault
public class CourseRuns {

    private final Map<UUID, CourseRun> runs;

    public CourseRuns() {
        runs = new Object2ObjectOpenHashMap<>();
    }

    public Optional<CourseRun> getRun(Player player) {
        return Optional.ofNullable(runs.get(player.getUniqueId()));
    }

    public boolean startRun(Player player, Course course, GameMode previousGameMode) {
        if (runs.containsKey(player.getUniqueId())) {
            return false;
        }
        CourseRun run = new CourseRun(player, course, previousGameMode);
        runs.put(player.getUniqueId(), run);
        return true;
    }

    public void update() {
        runs.values().forEach(CourseRun::update);
    }

    public Optional<CourseRun> stop(Player player) {
        return Optional.ofNullable(runs.remove(player.getUniqueId()));
    }

}
