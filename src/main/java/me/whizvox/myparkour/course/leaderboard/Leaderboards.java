package me.whizvox.myparkour.course.leaderboard;

import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.util.Page;
import org.jooq.*;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static me.whizvox.myparkour.db.Tables.TIMES;

public class Leaderboards {

    private int lastId;

    public Leaderboards() {
        lastId = 0;
    }

    private static SelectJoinStep<Record> select() {
        return MyParkour.inst().dsl().select().from(TIMES);
    }

    private static CourseTime mapRecord(Record record) {
        return new ImmutableCourseTime(
            record.get(TIMES.ID),
            UUID.fromString(record.get(TIMES.PLAYER)),
            record.get(TIMES.COURSE),
            record.get(TIMES.WHEN_SET),
            record.get(TIMES.TIME),
            record.get(TIMES.RANK)
        );
    }

    private void updateLastId() {
        try (var stream = MyParkour.inst().dsl().select(TIMES.ID).from(TIMES).orderBy(TIMES.ID.desc()).limit(1).stream()) {
            lastId = stream.map(Record1::value1).findAny().orElse(0);
        }
    }

    public void initialize() {
        MyParkour.inst().dsl()
            .createTableIfNotExists(TIMES)
            .columns(TIMES.fields())
            .primaryKey(TIMES.ID)
            .execute();
    }

    public Optional<CourseTime> getTime(int id) {
        return select()
            .where(TIMES.ID.eq(id))
            .fetch(Leaderboards::mapRecord)
            .stream()
            .findAny();
    }

    public Optional<CourseTime> getTime(UUID playerId, int courseId) {
        return select()
            .where(TIMES.PLAYER.eq(playerId.toString()).and(TIMES.COURSE.eq(courseId)))
            .fetch(Leaderboards::mapRecord)
            .stream()
            .findAny();
    }

    public Stream<CourseTime> getAllTimes() {
        return select()
            .stream()
            .map(Leaderboards::mapRecord);
    }

    public Page<CourseTime> getTimes(LeaderboardQuery query) {
        Condition[] conditions;
        if (query.getPlayerId() != null) {
            if (query.isCourseSet()) {
                conditions = new Condition[]{TIMES.PLAYER.eq(query.getPlayerId().toString()).and(TIMES.COURSE.eq(query.getCourseId()))};
            } else {
                conditions = new Condition[]{TIMES.PLAYER.eq(query.getPlayerId().toString())};
            }
        } else if (query.isCourseSet()) {
            conditions = new Condition[]{TIMES.COURSE.eq(query.getCourseId())};
        } else {
            conditions = new Condition[0];
        }
        int count = MyParkour.inst().dsl().selectCount().from(TIMES).where(conditions).fetch().getFirst().value1();
        SortOrder order = query.isAscending() ? SortOrder.ASC : SortOrder.DESC;
        OrderField<?> orderBy = switch (query.getSort()) {
            case RANK -> TIMES.RANK.sort(order);
            case TIME -> TIMES.TIME.sort(order);
            case COURSE -> TIMES.COURSE.sort(order);
        };
        List<CourseTime> items = select()
            .where(conditions)
            .orderBy(orderBy)
            .offset(query.getPage() * query.getLimit())
            .limit(query.getLimit())
            .fetch(Leaderboards::mapRecord);
        return new Page<>(query.getPage(), count, (int) Math.ceil((double) count / query.getLimit()), items);
    }

    public AddResult log(UUID playerId, int courseId, int time) {
        return getTime(playerId, courseId).map(oldTime -> {
            if (oldTime.time() <= time) {
                return AddResult.NO_CHANGE;
            }
            MyParkour.inst().dsl().update(TIMES)
                .set(TIMES.WHEN_SET, LocalDateTime.now())
                .set(TIMES.TIME, time)
                .execute();
            MyParkour.inst().getLogger().info("Updated course time: player=%s, course=%d, time=%d".formatted(playerId, courseId, time));
            return AddResult.NEW_PERSONAL_BEST;
        }).orElseGet(() -> {
            updateLastId();
            lastId++;
            MyParkour.inst().dsl().insertInto(TIMES, TIMES.ID, TIMES.PLAYER, TIMES.COURSE, TIMES.WHEN_SET, TIMES.TIME, TIMES.RANK)
                .values(lastId, playerId.toString(), courseId, LocalDateTime.now(), time, (short) 0)
                .execute();
            MyParkour.inst().getLogger().info("Logged first course time: player=%s, course=%d, time=%d".formatted(playerId, courseId, time));
            return AddResult.FIRST_TIME;
        });
    }

    public boolean remove(int timeId) {
        return MyParkour.inst().dsl().deleteFrom(TIMES)
            .where(TIMES.ID.eq(timeId))
            .execute() > 0;
    }

    public boolean clearCourse(int courseId) {
        return MyParkour.inst().dsl().deleteFrom(TIMES)
            .where(TIMES.COURSE.eq(courseId))
            .execute() > 0;
    }

    public boolean clearPlayer(UUID playerId) {
        return MyParkour.inst().dsl().deleteFrom(TIMES)
            .where(TIMES.PLAYER.eq(playerId.toString()))
            .execute() > 0;
    }

    public boolean clearAll() {
        return MyParkour.inst().dsl().deleteFrom(TIMES)
            .execute() > 0;
    }

    public enum AddResult {
        FIRST_TIME,
        NEW_PERSONAL_BEST,
        NO_CHANGE
    }

}
