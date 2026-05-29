package me.whizvox.myparkour.json;

import com.google.gson.*;
import me.whizvox.myparkour.course.leaderboard.CourseTime;
import me.whizvox.myparkour.course.leaderboard.LeaderboardTimes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardTimesJsonCodec implements JsonSerializer<LeaderboardTimes>, JsonDeserializer<LeaderboardTimes> {

    private LeaderboardTimesJsonCodec() {}

    @Override
    public LeaderboardTimes deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonArray root = json.getAsJsonArray();
        List<CourseTime> times = new ArrayList<>(root.size());
        root.forEach(timeElem -> times.add(context.deserialize(timeElem, CourseTime.class)));
        return new LeaderboardTimes(Collections.unmodifiableList(times));
    }

    @Override
    public JsonElement serialize(LeaderboardTimes src, Type type, JsonSerializationContext context) {
        JsonArray root = new JsonArray(src.times().size());
        src.times().forEach(time -> root.add(context.serialize(time, CourseTime.class)));
        return root;
    }

    public static final LeaderboardTimesJsonCodec INSTANCE = new LeaderboardTimesJsonCodec();

}
