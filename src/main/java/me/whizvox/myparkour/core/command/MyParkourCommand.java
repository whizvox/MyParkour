package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
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
import static me.whizvox.myparkour.Messages.key;
import static me.whizvox.myparkour.util.CommandUtils.createPermission;

public class MyParkourCommand {

    public static final Permission
        PERMISSION_VERSION = createPermission("version"),
        PERMISSION_RELOAD = createPermission("reload"),
        PERMISSION_DEBUG = createPermission("debug");

    public static final String
        KEY_VERSION = key("command.version"),
        KEY_RELOAD = key("command.reload");

    private static int version(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(Messages.translate(KEY_VERSION, Map.of("version", MyParkour.inst().getPluginMeta().getVersion())));
        return SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        MyParkour.inst().reload();
        context.getSource().getSender().sendMessage(Messages.translate(KEY_RELOAD));
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
        context.getSource().getSender().sendMessage(Messages.translate("myparkour.debug.addFakeTimes", Map.of("course", course.name())));
        return SINGLE_SUCCESS;
    }

    private static int debugClear(CommandContext<CommandSourceStack> context) {
        List<CourseTime> fakeTimes = new ArrayList<>();
        try (var stream = MyParkour.inst().getLeaderboards().getAllTimes()) {
            stream.forEach(courseTime -> {
                OfflinePlayer player = Bukkit.getOfflinePlayer(courseTime.playerId());
                if (!player.hasPlayedBefore()) {
                    fakeTimes.add(courseTime);
                }
            });
        }
        fakeTimes.forEach(courseTime -> MyParkour.inst().getLeaderboards().remove(courseTime.id()));
        context.getSource().getSender().sendMessage(Messages.translate("myparkour.debug.clear"));
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
