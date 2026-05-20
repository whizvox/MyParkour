package me.whizvox.myparkour.course.leaderboard;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.util.Persistent;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class Leaderboards implements Persistent<List<MutableCourseTime>> {

    private final Int2ObjectMap<MutableCourseTime> times;
    private final Map<PlayerCourseKey, MutableCourseTime> byPlayerAndCourse;
    private final Map<UUID, List<MutableCourseTime>> byPlayer;
    private final Int2ObjectMap<List<MutableCourseTime>> byCourse;
    private int lastId;

    public Leaderboards() {
        times = new Int2ObjectOpenHashMap<>();
        byPlayerAndCourse = new Object2ObjectOpenHashMap<>();
        byPlayer = new Object2ObjectOpenHashMap<>();
        byCourse = new Int2ObjectOpenHashMap<>();
        lastId = 0;
    }

    private static void resortCourseTimes(List<MutableCourseTime> courseTimes) {
        courseTimes.sort(CourseTime.COMPARATOR);
        for (int i = 0; i < courseTimes.size(); i++) {
            MutableCourseTime courseTime = courseTimes.get(i);
            if (courseTime.rank() != i) {
                courseTime.setRank(i);
            }
        }
    }

    private void resortCourseTimes(int courseId) {
        List<MutableCourseTime> courseTimes = byCourse.get(courseId);
        if (courseTimes != null) {
            resortCourseTimes(courseTimes);
        }
    }

    public Optional<CourseTime> getTime(int id) {
        return Optional.ofNullable(times.get(id));
    }

    public Optional<CourseTime> getTime(UUID playerId, int courseId) {
        return Optional.ofNullable(byPlayerAndCourse.get(new PlayerCourseKey(playerId, courseId)));
    }

    public Stream<CourseTime> getTimes(LeaderboardQuery query) {
        Stream<MutableCourseTime> stream;
        if (query.getPlayerId() != null) {
            if (query.isCourseSet()) {
                return Stream.of(byPlayerAndCourse.get(new PlayerCourseKey(query.getPlayerId(), query.getCourseId())).toImmutable());
            } else {
                stream = byPlayer.get(query.getPlayerId()).stream();
            }
        } else if (query.isCourseSet()) {
            stream = byCourse.get(query.getCourseId()).stream();
        } else {
            stream = times.values().stream();
        }
        if (query.isAscending()) {
            stream = switch (query.getSort()) {
                case RANK -> stream.sorted(Comparator.comparing(MutableCourseTime::rank));
                case TIME -> stream.sorted(Comparator.comparing(MutableCourseTime::time));
                case COURSE -> stream.sorted(Comparator.comparing(MutableCourseTime::courseId));
            };
        } else {
            stream = switch (query.getSort()) {
                case RANK -> stream.sorted((o1, o2) -> Integer.compare(o2.rank(), o1.rank()));
                case TIME -> stream.sorted((o1, o2) -> Integer.compare(o2.time(), o1.time()));
                case COURSE -> stream.sorted((o1, o2) -> Integer.compare(o2.courseId(), o1.courseId()));
            };
        }
        return stream
            .skip((long) query.getPage() * query.getLimit())
            .limit(query.getLimit())
            .map(MutableCourseTime::toImmutable);
    }

    public AddResult log(UUID playerId, int courseId, int time) {
        MutableCourseTime oldTime = byPlayerAndCourse.get(new PlayerCourseKey(playerId, courseId));
        if (oldTime != null) {
            if (oldTime.time() < time) {
                return AddResult.NO_CHANGE;
            }
            oldTime.setWhen(LocalDateTime.now());
            oldTime.setTime(time);
            resortCourseTimes(courseId);
            return AddResult.NEW_PERSONAL_BEST;
        }
        while (times.containsKey(lastId)) {
            lastId++;
        }
        MutableCourseTime newTime = new MutableCourseTime(lastId, playerId, courseId, LocalDateTime.now(), time, 0);
        times.put(newTime.id(), newTime);
        byPlayerAndCourse.put(new PlayerCourseKey(playerId, courseId), newTime);
        byPlayer.computeIfAbsent(playerId, _ -> new ArrayList<>()).add(newTime);
        byCourse.computeIfAbsent(courseId, _ -> new ArrayList<>()).add(newTime);
        resortCourseTimes(courseId);
        return AddResult.FIRST_TIME;
    }

    public boolean remove(int timeId) {
        MutableCourseTime time = times.remove(timeId);
        if (time != null) {
            byPlayerAndCourse.remove(new PlayerCourseKey(time.playerId(), time.courseId()));
            byPlayer.get(time.playerId()).removeIf(t -> t.id() == timeId);
            byCourse.get(time.courseId()).removeIf(t -> t.id() == timeId);
            resortCourseTimes(time.courseId());
            return true;
        }
        return false;
    }

    public boolean clearCourse(int courseId) {
        List<MutableCourseTime> courseTimes = byCourse.remove(courseId);
        if (courseTimes != null) {
            courseTimes.forEach(time -> {
                times.remove(time.id());
                byPlayer.get(time.playerId()).removeIf(t -> t.courseId() == courseId);
            });
            List<PlayerCourseKey> toRemove = byPlayerAndCourse.keySet().stream().filter(key -> key.courseId == courseId).toList();
            toRemove.forEach(byPlayerAndCourse::remove);
            return true;
        }
        return false;
    }

    public boolean clearPlayer(UUID playerId) {
        List<MutableCourseTime> courseTimes = byPlayer.remove(playerId);
        if (courseTimes != null) {
            courseTimes.forEach(time -> {
                times.remove(time.id());
                byCourse.get(time.courseId()).removeIf(t -> t.playerId().equals(playerId));
            });
            List<PlayerCourseKey> toRemove = byPlayerAndCourse.keySet().stream().filter(key -> key.playerId.equals(playerId)).toList();
            toRemove.forEach(byPlayerAndCourse::remove);
            return true;
        }
        return false;
    }

    @Override
    public List<MutableCourseTime> writePersistent() {
        return new ArrayList<>(times.values());
    }

    @Override
    public void readPersistent(List<MutableCourseTime> allTimes) {
        times.clear();
        byPlayerAndCourse.clear();
        byPlayer.clear();
        byCourse.clear();
        allTimes.forEach(time -> {
            times.put(time.id(), time);
            byPlayerAndCourse.put(new PlayerCourseKey(time.playerId(), time.courseId()), time);
            byPlayer.computeIfAbsent(time.playerId(), _ -> new ArrayList<>()).add(time);
            byCourse.computeIfAbsent(time.courseId(), _ -> new ArrayList<>()).add(time);
        });
        byCourse.values().forEach(Leaderboards::resortCourseTimes);
    }

    @Override
    public Type getPersistentType() {
        return new TypeToken<ArrayList<CourseTime>>(){}.getType();
    }

    public enum AddResult {
        FIRST_TIME,
        NEW_PERSONAL_BEST,
        NO_CHANGE
    }

    private record PlayerCourseKey(UUID playerId, int courseId) {
    }

}
