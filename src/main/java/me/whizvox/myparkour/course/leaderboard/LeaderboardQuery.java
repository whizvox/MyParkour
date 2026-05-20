package me.whizvox.myparkour.course.leaderboard;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@NotNullByDefault
public class LeaderboardQuery {

    private @Nullable UUID playerId;
    private int courseId;
    private Sort sort;
    private boolean ascending;
    private int limit;
    private int page;

    public LeaderboardQuery() {
        playerId = null;
        courseId = 0;
        sort = Sort.TIME;
        ascending = false;
        limit = 10;
        page = 0;
    }

    public @Nullable UUID getPlayerId() {
        return playerId;
    }

    public int getCourseId() {
        return courseId;
    }

    public Sort getSort() {
        return sort;
    }

    public boolean isAscending() {
        return ascending;
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }

    public boolean isCourseSet() {
        return courseId != 0;
    }

    public LeaderboardQuery setPlayerId(@Nullable UUID playerId) {
        this.playerId = playerId;
        return this;
    }

    public LeaderboardQuery setCourseId(int courseId) {
        this.courseId = courseId;
        return this;
    }

    public LeaderboardQuery setSort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public LeaderboardQuery setAscending(boolean ascending) {
        this.ascending = ascending;
        return this;
    }

    public LeaderboardQuery setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public LeaderboardQuery setPage(int page) {
        this.page = page;
        return this;
    }

    public enum Sort {
        RANK,
        TIME,
        COURSE
    }

}
