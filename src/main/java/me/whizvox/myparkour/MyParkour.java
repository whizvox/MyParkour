package me.whizvox.myparkour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.whizvox.myparkour.core.command.EditCourseCommand;
import me.whizvox.myparkour.core.command.MyParkourCommand;
import me.whizvox.myparkour.core.command.ParkourCommand;
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
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;

public final class MyParkour extends JavaPlugin {

    private final Gson gson;
    private MyParkourPaths paths;
    private final SimpleTranslationStore translationStore;
    private final Courses courses;
    private final CourseEdits edits;
    private final CourseRuns runs;
    private final Leaderboards leaderboards;

    private int updateRunsTaskId;

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
        paths = null;
        translationStore = new SimpleTranslationStore();
        courses = new Courses();
        edits = new CourseEdits(courses);
        runs = new CourseRuns();
        leaderboards = new Leaderboards();
        updateRunsTaskId = -1;
    }

    public Gson getGson() {
        return gson;
    }

    public MyParkourPaths getPaths() {
        return paths;
    }

    public SimpleTranslationStore getTranslations() {
        return translationStore;
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
            translationStore.load(paths.messagesFile());
            courses.load(paths.coursesFile());
            edits.save(paths.editsFile());
            leaderboards.load(paths.timesFile());
            getLogger().info("Finished reloading messages, courses, edits, and leaderboards");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not complete plugin reload. This plugin will most likely behave abnormally!", e);
        }
    }

    @Override
    public void onEnable() {
        paths = new MyParkourPaths(getDataPath());
        GlobalTranslator.translator().addSource(translationStore);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            MyParkourCommand.register(commands.registrar());
            EditCourseCommand.register(commands.registrar());
            ParkourCommand.register(commands.registrar());
        });
        reload();
        updateRunsTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runs::update, 0, 1);
        getLogger().info("Scheduled runs updater task");
    }

    @Override
    public void onDisable() {
        try {
            edits.save(paths.editsFile());
            leaderboards.save(paths.timesFile());
            courses.save(paths.coursesFile());
            getLogger().info("Finished saving edits, leaderboards, and courses");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save courses or leaderboards", e);
        }
        Bukkit.getScheduler().cancelTask(updateRunsTaskId);
        getLogger().info("Cancelled runs updater task");
    }

    private static MyParkour instance = null;

    public static MyParkour inst() {
        return instance;
    }

}
