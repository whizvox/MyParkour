package me.whizvox.myparkour.course.leaderboard;

import java.time.LocalDateTime;
import java.util.UUID;

public record ImmutableCourseTime(int id, UUID playerId, int courseId, LocalDateTime when, int time, int rank) implements CourseTime {

    @Override
    public MutableCourseTime toMutable() {
        return new MutableCourseTime(id, playerId, courseId, when, time, rank);
    }

    @Override
    public ImmutableCourseTime toImmutable() {
        return this;
    }

}
