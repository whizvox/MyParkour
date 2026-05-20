package me.whizvox.myparkour.course.leaderboard;

import java.time.LocalDateTime;
import java.util.UUID;

public record ImmutableCourseTime(int id, UUID playerId, int courseId, LocalDateTime when, int time, int rank) implements CourseTime {

}
