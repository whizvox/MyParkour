package me.whizvox.myparkour.course.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

@NotNullByDefault
public interface CourseTime extends Comparable<CourseTime> {

    int id();

    UUID playerId();

    int courseId();

    int time();

    LocalDateTime when();

    int rank();

    default Duration toDuration() {
        return Duration.ofMillis(50L * time());
    }

    @Override
    default int compareTo(@NotNull CourseTime o) {
        return COMPARATOR.compare(this, o);
    }

    Comparator<CourseTime> COMPARATOR = Comparator.comparing(CourseTime::time).thenComparing(CourseTime::when);

}
