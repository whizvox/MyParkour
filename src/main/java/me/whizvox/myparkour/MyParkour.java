package me.whizvox.myparkour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.whizvox.myparkour.core.command.EditCourseCommand;
import me.whizvox.myparkour.core.command.MyParkourCommand;
import me.whizvox.myparkour.core.command.ParkourCommand;
import me.whizvox.myparkour.core.command.TimesCommand;
import me.whizvox.myparkour.course.*;
import me.whizvox.myparkour.course.edit.CourseEdits;
import me.whizvox.myparkour.course.leaderboard.CourseTime;
import me.whizvox.myparkour.course.leaderboard.LeaderboardTimes;
import me.whizvox.myparkour.course.leaderboard.Leaderboards;
import me.whizvox.myparkour.course.run.CourseRuns;
import me.whizvox.myparkour.json.*;
import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.ImmutableBoundingBox;
import me.whizvox.myparkour.util.ImmutableLocation;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@NotNullByDefault
public final class MyParkour extends JavaPlugin {

    private final Gson gson;
    private final SimpleTranslationStore translationStore;
    private final Courses courses;
    private final CourseEdits edits;
    private final CourseRuns runs;
    private final Leaderboards leaderboards;

    private @Nullable MyParkourPaths paths;
    private @Nullable Connection conn;
    private @Nullable DSLContext create;
    private int updateRunsTaskId;

    public MyParkour() {
        instance = this;
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(BlockLocation.class, BlockLocationJsonCodec.INSTANCE)
            .registerTypeAdapter(Checkpoint.class, CheckpointJsonCodecs.GENERIC)
            .registerTypeAdapter(BlockCheckpoint.class, CheckpointJsonCodecs.BLOCK)
            .registerTypeAdapter(BoxCheckpoint.class, CheckpointJsonCodecs.BOX)
            .registerTypeAdapter(SplitCheckpoint.class, CheckpointJsonCodecs.SPLIT)
            .registerTypeAdapter(CourseFlag.class, CourseFlagJsonCodec.INSTANCE)
            .registerTypeAdapter(Course.class, CourseJsonCodec.INSTANCE)
            .registerTypeAdapter(CourseTime.class, CourseTimeJsonCodec.INSTANCE)
            .registerTypeAdapter(ImmutableBoundingBox.class, ImmutableBoundingBoxJsonCodec.INSTANCE)
            .registerTypeAdapter(ImmutableLocation.class, ImmutableLocationJsonCodec.INSTANCE)
            .registerTypeAdapter(LeaderboardTimes.class, LeaderboardTimesJsonCodec.INSTANCE)
            .registerTypeAdapter(LocalDateTime.class, LocalDateTimeJsonCodec.INSTANCE)
            .registerTypeAdapter(UUID.class, UUIDJsonCodec.INSTANCE)
            .create();
        translationStore = new SimpleTranslationStore();
        courses = new Courses();
        edits = new CourseEdits(courses);
        runs = new CourseRuns();
        leaderboards = new Leaderboards();
        paths = null;
        conn = null;
        create = null;
        updateRunsTaskId = -1;
    }

    public DSLContext dsl() {
        return Objects.requireNonNull(create, "Attempted to retrieve DSL context while in an incomplete state");
    }

    public Gson getGson() {
        return gson;
    }

    public MyParkourPaths getPaths() {
        return Objects.requireNonNull(paths, "Attempted to retrieve paths object while in an incomplete state");
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
            //noinspection DataFlowIssue
            translationStore.load(paths.messagesFile());
            courses.load(paths.coursesFile());
            edits.save(paths.editsFile());
            getLogger().info("Finished reloading messages, courses, edits, and leaderboards");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not complete plugin reload. This plugin will most likely behave abnormally!", e);
        }
    }

    @Override
    public void onEnable() {
        paths = new MyParkourPaths(getDataPath());
        paths.mkdir();
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + paths.dbFile().toAbsolutePath());
            create = DSL.using(conn, SQLDialect.SQLITE);
        } catch (SQLException e) {
            getLogger().severe("Could not connect to database");
            throw new RuntimeException(e);
        }
        leaderboards.initialize();
        GlobalTranslator.translator().addSource(translationStore);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            MyParkourCommand.register(commands.registrar());
            EditCourseCommand.register(commands.registrar());
            ParkourCommand.register(commands.registrar());
            TimesCommand.register(commands.registrar());
        });
        reload();
        updateRunsTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runs::update, 0, 1);
        getLogger().info("Scheduled runs updater task");
    }

    @Override
    public void onDisable() {
        if (conn != null) {
            try {
                conn.close();
                getLogger().info("Closed database connection");
                conn = null;
                create = null;
            } catch (SQLException e) {
                getLogger().log(Level.SEVERE, "Could not close database connection", e);
            }
        }
        try {
            //noinspection DataFlowIssue
            edits.save(paths.editsFile());
            courses.save(paths.coursesFile());
            getLogger().info("Finished saving edits, leaderboards, and courses");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save courses or leaderboards", e);
        }
        Bukkit.getScheduler().cancelTask(updateRunsTaskId);
        getLogger().info("Cancelled runs updater task");
    }

    private static @Nullable MyParkour instance = null;

    public static MyParkour inst() {
        //noinspection DataFlowIssue
        return instance;
    }

}
