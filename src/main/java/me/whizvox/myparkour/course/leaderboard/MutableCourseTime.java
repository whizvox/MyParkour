package me.whizvox.myparkour.course.leaderboard;

import org.jetbrains.annotations.NotNullByDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@NotNullByDefault
public class MutableCourseTime implements CourseTime {

    private final int id;
    private final UUID playerId;
    private final int courseId;
    private LocalDateTime when;
    private int time;
    private int rank;

    public MutableCourseTime(int id, UUID playerId, int courseId, LocalDateTime when, int time, int rank) {
        this.id = id;
        this.playerId = playerId;
        this.courseId = courseId;
        this.when = when;
        this.time = time;
        this.rank = rank;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public UUID playerId() {
        return playerId;
    }

    @Override
    public int courseId() {
        return courseId;
    }

    @Override
    public LocalDateTime when() {
        return when;
    }

    @Override
    public int time() {
        return time;
    }

    @Override
    public int rank() {
        return rank;
    }

    public void setWhen(LocalDateTime when) {
        this.when = when;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public MutableCourseTime toMutable() {
        return this;
    }

    @Override
    public ImmutableCourseTime toImmutable() {
        return new ImmutableCourseTime(id, playerId, courseId, when, time, rank);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
