package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.leaderboard.CourseTime;
import me.whizvox.myparkour.util.CommandUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

import java.time.LocalDateTime;
import java.util.*;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.myparkour.db.tables.Times.TIMES;
import static me.whizvox.myparkour.util.CommandUtils.createPermission;

public class MyParkourCommand {

    public static final Permission
        PERMISSION_VERSION = createPermission("version"),
        PERMISSION_RELOAD = createPermission("reload"),
        PERMISSION_DEBUG = createPermission("debug");

    private static int version(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(Messages.translate(Messages.KEY_COMMAND_VERSION, Map.of("version", MyParkour.inst().getPluginMeta().getVersion())));
        return SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        MyParkour.inst().reload();
        context.getSource().getSender().sendMessage(Messages.translate(Messages.KEY_COMMAND_RELOAD));
        return SINGLE_SUCCESS;
    }

    private static int debugAddFakeTimes(CommandContext<CommandSourceStack> context) {
        Course course = CourseArgumentType.getCourse(context, "course");
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            UUID fakePlayerId = new UUID(rand.nextLong(), rand.nextLong());
            String fakePlayerName = "FakePlayer_" + Integer.toString(rand.nextInt(100000), 16);
            MyParkour.inst().getNames().update(fakePlayerId, fakePlayerName, Component.text(fakePlayerName), LocalDateTime.now());
            MyParkour.inst().getLeaderboards().log(fakePlayerId, course.id(), rand.nextInt(5, 10000));
        }
        context.getSource().getSender().sendMessage(Messages.translate(Messages.KEY_DEBUG_ADD_FAKE_TIMES, Map.of("course", course.name())));
        return SINGLE_SUCCESS;
    }

    private static int debugClear(CommandContext<CommandSourceStack> context) {
        List<CourseTime> fakeTimes = new ArrayList<>();
        IntSet coursesToReorder = new IntArraySet();
        try (var stream = MyParkour.inst().getLeaderboards().getAllTimes()) {
            stream.forEach(courseTime -> {
                OfflinePlayer player = Bukkit.getOfflinePlayer(courseTime.playerId());
                if (!player.hasPlayedBefore()) {
                    fakeTimes.add(courseTime);
                    coursesToReorder.add(courseTime.courseId());
                }
            });
        }
        MyParkour.inst().dsl().batched(c ->
            fakeTimes.forEach(time ->
                c.dsl().deleteFrom(TIMES)
                    .where(TIMES.ID.eq(time.id()))
                    .execute()
            )
        );
        coursesToReorder.forEach(courseId -> MyParkour.inst().getLeaderboards().reorderCourseRanks(courseId));
        context.getSource().getSender().sendMessage(Messages.translate(Messages.KEY_DEBUG_CLEAR));
        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(Commands.literal("myparkour")
            .then(Commands.literal("version")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_VERSION))
                .executes(MyParkourCommand::version)
            )
            .then(Commands.literal("reload")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_RELOAD))
                .executes(MyParkourCommand::reload)
            )
            .then(Commands.literal("debug")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_DEBUG))
                .then(Commands.literal("addfaketimes")
                    .then(Commands.argument("course", CourseArgumentType.course())
                        .executes(MyParkourCommand::debugAddFakeTimes)
                    )
                )
                .then(Commands.literal("clear")
                    .executes(MyParkourCommand::debugClear)
                )
            )
            .build()
        );
    }

}
