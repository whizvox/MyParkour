package me.whizvox.myparkour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.whizvox.myparkour.command.MyParkourCommand;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.CourseFlag;
import me.whizvox.myparkour.course.Courses;
import me.whizvox.myparkour.course.edit.CourseEdits;
import me.whizvox.myparkour.course.leaderboard.CourseTime;
import me.whizvox.myparkour.course.leaderboard.Leaderboards;
import me.whizvox.myparkour.course.run.CourseRuns;
import me.whizvox.myparkour.json.*;
import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.ImmutableBoundingBox;
import me.whizvox.myparkour.util.ImmutableLocation;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;

public final class MyParkour extends JavaPlugin {

    private final Gson gson;
    private final SimpleTranslationStore translationStore;
    private final Courses courses;
    private final CourseEdits edits;
    private final CourseRuns runs;
    private final Leaderboards leaderboards;

    public MyParkour() {
        instance = this;
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(BlockLocation.class, BlockLocationJsonCodec.INSTANCE)
            .registerTypeAdapter(Checkpoint.class, CheckpointJsonCodec.INSTANCE)
            .registerTypeAdapter(CourseFlag.class, CourseFlagJsonCodec.INSTANCE)
            .registerTypeAdapter(Course.class, CourseJsonCodec.INSTANCE)
            .registerTypeAdapter(CourseTime.class, CourseTimeJsonCodec.INSTANCE)
            .registerTypeAdapter(ImmutableBoundingBox.class, ImmutableBoundingBoxJsonCodec.INSTANCE)
            .registerTypeAdapter(ImmutableLocation.class, ImmutableLocationJsonCodec.INSTANCE)
            .registerTypeAdapter(LocalDateTime.class, LocalDateTimeJsonCodec.INSTANCE)
            .registerTypeAdapter(UUID.class, UUIDJsonCodec.INSTANCE)
            .create();
        translationStore = new SimpleTranslationStore();
        courses = new Courses();
        edits = new CourseEdits(courses);
        runs = new CourseRuns();
        leaderboards = new Leaderboards();
    }

    public Gson getGson() {
        return gson;
    }

    public Courses getCourses() {
        return courses;
    }

    public CourseEdits getEdits() {
        return edits;
    }

    public CourseRuns getRuns() {
        return runs;
    }

    public Leaderboards getLeaderboards() {
        return leaderboards;
    }

    public void reload() {
        try {
            Files.createDirectories(getDataPath());
            translationStore.load(getDataPath().resolve("messages.json"));
            courses.load(getDataPath().resolve("courses.json"));
            leaderboards.load(getDataPath().resolve("times.json"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not complete plugin reload. This plugin will most likely behave abnormally!", e);
        }
    }

    @Override
    public void onEnable() {
        GlobalTranslator.translator().addSource(translationStore);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> MyParkourCommand.register(commands.registrar()));
        reload();
    }

    @Override
    public void onDisable() {
        try {
            courses.save(getDataPath().resolve("courses.json"));
            leaderboards.save(getDataPath().resolve("times.json"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save courses or leaderboards", e);
        }
    }

    private static MyParkour instance = null;

    public static MyParkour inst() {
        return instance;
    }

}
