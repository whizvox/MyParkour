package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.command.CourseTimeArgumentType;
import me.whizvox.myparkour.command.OfflinePlayerArgumentType;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.leaderboard.CourseTime;
import me.whizvox.myparkour.course.leaderboard.LeaderboardQuery;
import me.whizvox.myparkour.util.CommandUtils;
import me.whizvox.myparkour.util.Page;
import me.whizvox.myparkour.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNullByDefault;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@NotNullByDefault
public class TimesCommand {

    public static final Permission
        PERMISSION_COURSE_TIMES = CommandUtils.createPermission("times.course"),
        PERMISSION_PLAYER_TIMES = CommandUtils.createPermission("times.player"),
        PERMISSION_TIME_INFO = CommandUtils.createPermission("times.info"),
        PERMISSION_TIME_LOOKUP = CommandUtils.createPermission("times.lookup"),
        PERMISSION_DELETE_ONE = CommandUtils.createPermission("times.delete.one"),
        PERMISSION_DELETE_COURSE = CommandUtils.createPermission("times.delete.course"),
        PERMISSION_DELETE_PLAYER = CommandUtils.createPermission("times.delete.player"),
        PERMISSION_DELETE_ALL = CommandUtils.createPermission("times.delete.all");

    private static Component getTimeInfo(CourseTime time) {
        Course course = MyParkour.inst().getCourses().get(time.courseId())
            .orElseThrow(() -> new RuntimeException("Could not retrieve course from course time: time=%d, course=%d".formatted(time.id(), time.courseId())));
        return Messages.translate("myparkour.times.info", Map.of(
            "id", time.id(),
            "player_name", MyParkour.inst().getNames().getDisplayName(time.playerId()),
            "player_id", time.playerId(),
            "course", course.displayName(),
            "course_name", course.name(),
            "time", StringUtils.formatTime(time.time()),
            "time_ticks", time.time(),
            "when_set", DateTimeFormatter.ISO_DATE_TIME.format(time.when()),
            "rank", time.rank()
        ));
    }

    private static int showCourseTimes(CommandContext<CommandSourceStack> context, int page) {
        Course course = CourseArgumentType.getCourse(context, "course");
        CommandSender sender = context.getSource().getSender();
        Page<CourseTime> times = MyParkour.inst().getLeaderboards().getTimes(new LeaderboardQuery().setCourseId(course.id()).setAscending(true).setPage(page - 1));
        if (!times.items().isEmpty()) {
            boolean canSeeInfo = sender.hasPermission(PERMISSION_TIME_INFO);
            Component comp = Messages.translate("myparkour.times.course.header", Map.of("course", course.displayName(), "current_page", times.page() + 1, "total_pages", times.totalPages()));
            for (CourseTime item : times.items()) {
                Component basicInfo = Messages.translate("myparkour.times.course.entry", Map.of("rank", item.rank(), "time", StringUtils.formatTime(item.time()), "player", MyParkour.inst().getNames().getDisplayName(item.playerId())));
                if (canSeeInfo) {
                    comp = comp.appendNewline().append(basicInfo.hoverEvent(getTimeInfo(item)));
                } else {
                    comp = comp.appendNewline().append(basicInfo);
                }
            }
            sender.sendMessage(comp);
        } else {
            sender.sendMessage(Messages.translate("myparkour.times.course.none", Map.of("course", course.displayName())));
        }
        return SINGLE_SUCCESS;
    }

    private static int showPlayerTimes(CommandContext<CommandSourceStack> context, int page) {
        CommandSender sender = context.getSource().getSender();
        OfflinePlayer player = OfflinePlayerArgumentType.getOfflinePlayer(context, "player");
        Page<CourseTime> times = MyParkour.inst().getLeaderboards().getTimes(new LeaderboardQuery().setPlayerId(player.getUniqueId()).setAscending(true).setPage(page - 1));
        if (!times.items().isEmpty()) {
            boolean canSeeInfo = sender.hasPermission(PERMISSION_TIME_INFO);
            Component comp = Messages.translate("myparkour.times.player.header", Map.of("player", MyParkour.inst().getNames().getDisplayName(player.getUniqueId()), "current_page", times.page() + 1, "total_pages", times.totalPages()));
            for (CourseTime item : times.items()) {
                Component courseName = MyParkour.inst().getCourses().get(item.courseId())
                    .map(Course::displayName)
                    .orElse(Component.text("???", NamedTextColor.DARK_RED));
                Component basicInfo = Messages.translate("myparkour.times.player.entry", Map.of("rank", item.rank(), "time", StringUtils.formatTime(item.time()), "course", courseName));
                if (canSeeInfo) {
                    comp = comp.appendNewline().append(basicInfo.hoverEvent(getTimeInfo(item)));
                } else {
                    comp = comp.appendNewline().append(basicInfo);
                }
            }
            sender.sendMessage(comp);
        } else {
            sender.sendMessage(Messages.translate("myparkour.times.player.none", Map.of("player", MyParkour.inst().getNames().getDisplayName(player.getUniqueId()))));
        }
        return SINGLE_SUCCESS;
    }

    private static int showTimeInfo(CommandContext<CommandSourceStack> context) {
        CourseTime time = CourseTimeArgumentType.getTime(context, "timeId");
        context.getSource().getSender().sendMessage(getTimeInfo(time));
        return SINGLE_SUCCESS;
    }

    private static int lookupTimeInfo(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        Course course = CourseArgumentType.getCourse(context, "course");
        OfflinePlayer player = OfflinePlayerArgumentType.getOfflinePlayer(context, "player");
        MyParkour.inst().getLeaderboards().getTime(player.getUniqueId(), course.id()).ifPresentOrElse(time -> {
            sender.sendMessage(getTimeInfo(time));
        }, () -> {
            sender.sendMessage(Messages.translate("myparkour.times.lookup.none", Map.of("player", MyParkour.inst().getNames().getDisplayName(player.getUniqueId()), "course", course.displayName())));
        });
        return SINGLE_SUCCESS;
    }

    private static int deleteTime(CommandContext<CommandSourceStack> context) {
        CourseTime time = CourseTimeArgumentType.getTime(context, "timeId");
        MyParkour.inst().getLeaderboards().delete(time.id());
        context.getSource().getSender().sendMessage(Messages.translate("myparkour.times.delete.one"));
        return SINGLE_SUCCESS;
    }

    private static int deleteTimesForCourse(CommandContext<CommandSourceStack> context) {
        Course course = CourseArgumentType.getCourse(context, "course");
        boolean success = MyParkour.inst().getLeaderboards().deleteByCourse(course.id());
        context.getSource().getSender().sendMessage(Messages.translate("myparkour.times.clear.course" + (success ? "" : ".none"), Map.of("course", course.displayName())));
        return SINGLE_SUCCESS;
    }

    private static int deleteTimesForPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        Player player = resolver.resolve(context.getSource()).getFirst();
        CommandSender sender = context.getSource().getSender();
        boolean success = MyParkour.inst().getLeaderboards().deleteByPlayer(player.getUniqueId());
        sender.sendMessage(Messages.translate("myparkour.times.clear.player" + (success ? "" : ".none"), Map.of("player", player.name())));
        return SINGLE_SUCCESS;
    }

    private static int deleteAllCourseTimes(CommandContext<CommandSourceStack> context) {
        boolean success = MyParkour.inst().getLeaderboards().deleteAll();
        context.getSource().getSender().sendMessage(Messages.translate("myparkour.times.clear.all" + (success ? "" : ".none")));
        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(Commands.literal("times")
            .then(Commands.literal("course")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_COURSE_TIMES))
                .then(Commands.argument("course", CourseArgumentType.course())
                    .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(context -> showCourseTimes(context, IntegerArgumentType.getInteger(context, "page")))
                    )
                    .executes(context -> showCourseTimes(context, 1))
                )
            )
            .then(Commands.literal("player")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_PLAYER_TIMES))
                .then(Commands.argument("player", OfflinePlayerArgumentType.offlinePlayer())
                    .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(context -> showPlayerTimes(context, IntegerArgumentType.getInteger(context, "page")))
                    )
                    .executes(context -> showPlayerTimes(context, 1))
                )
            )
            .then(Commands.literal("info")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_TIME_INFO))
                .then(Commands.argument("timeId", CourseTimeArgumentType.time())
                    .executes(TimesCommand::showTimeInfo)
                )
            )
            .then(Commands.literal("lookup")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_TIME_LOOKUP))
                .then(Commands.argument("course", CourseArgumentType.course())
                    .then(Commands.argument("player", OfflinePlayerArgumentType.offlinePlayer())
                        .executes(TimesCommand::lookupTimeInfo)
                    )
                )
            )
            .then(Commands.literal("delete")
                .then(Commands.literal("time")
                    .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_DELETE_ONE))
                    .then(Commands.argument("timeId", CourseTimeArgumentType.time())
                        .executes(TimesCommand::deleteTime)
                    )
                )
                .then(Commands.literal("course")
                    .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_DELETE_COURSE))
                    .then(Commands.argument("course", CourseArgumentType.course())
                        .executes(TimesCommand::deleteTimesForCourse)
                    )
                )
                .then(Commands.literal("player")
                    .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_DELETE_PLAYER))
                    .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(TimesCommand::deleteTimesForPlayer)
                    )
                )
                .then(Commands.literal("all")
                    .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_DELETE_ALL))
                    .executes(TimesCommand::deleteAllCourseTimes)
                )
            )
            .build()
        );
    }

}
