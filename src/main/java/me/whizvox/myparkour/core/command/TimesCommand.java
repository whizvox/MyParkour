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
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.leaderboard.CourseTime;
import me.whizvox.myparkour.course.leaderboard.LeaderboardQuery;
import me.whizvox.myparkour.util.CommandUtils;
import me.whizvox.myparkour.util.Page;
import me.whizvox.myparkour.util.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class TimesCommand {

    public static final Permission
        PERMISSION_COURSE_TIMES = CommandUtils.createPermission("times.course"),
        PERMISSION_CLEAR_COURSE = CommandUtils.createPermission("times.clear.course"),
        PERMISSION_CLEAR_PLAYER = CommandUtils.createPermission("times.clear.player"),
        PERMISSION_CLEAR_ALL = CommandUtils.createPermission("times.clear.all");

    private static int showCourseTimes(CommandContext<CommandSourceStack> context, int page) {
        Course course = CourseArgumentType.getCourse(context, "course");
        CommandSender sender = context.getSource().getSender();
        Page<CourseTime> times = MyParkour.inst().getLeaderboards().getTimes(new LeaderboardQuery().setCourseId(course.id()).setAscending(true).setPage(page - 1));
        if (!times.items().isEmpty()) {
            Component comp = Messages.translate("myparkour.times.course.header", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()), "current_page", times.page() + 1, "total_pages", times.totalPages()));
            for (CourseTime item : times.items()) {
                comp = comp.appendNewline().append(Messages.translate("myparkour.times.course.entry", Map.of("rank", item.rank(), "time", StringUtils.formatTime(item.time()), "player", MyParkour.inst().getNames().getDisplayName(item.playerId()))));
            }
            sender.sendMessage(comp);
        } else {
            sender.sendMessage(Messages.translate("myparkour.times.course.none", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()))));
        }
        return SINGLE_SUCCESS;
    }

    private static int clearTimesForCourse(CommandContext<CommandSourceStack> context) {
        Course course = CourseArgumentType.getCourse(context, "course");
        boolean success = MyParkour.inst().getLeaderboards().clearCourse(course.id());
        context.getSource().getSender().sendMessage(Messages.translate("myparkour.times.clear.course" + (success ? "" : ".none"), Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()))));
        return SINGLE_SUCCESS;
    }

    private static int clearTimesForPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        Player player = resolver.resolve(context.getSource()).getFirst();
        CommandSender sender = context.getSource().getSender();
        boolean success = MyParkour.inst().getLeaderboards().clearPlayer(player.getUniqueId());
        sender.sendMessage(Messages.translate("myparkour.times.clear.player" + (success ? "" : ".none"), Map.of("player", player.name())));
        return SINGLE_SUCCESS;
    }

    private static int clearAllCourseTimes(CommandContext<CommandSourceStack> context) {
        boolean success = MyParkour.inst().getLeaderboards().clearAll();
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
            .then(Commands.literal("clear")
                .then(Commands.literal("course")
                    .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_CLEAR_COURSE))
                    .then(Commands.argument("course", CourseArgumentType.course())
                        .executes(TimesCommand::clearTimesForCourse)
                    )
                )
                .then(Commands.literal("player")
                    .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_CLEAR_PLAYER))
                    .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(TimesCommand::clearTimesForPlayer)
                    )
                )
                .then(Commands.literal("all")
                    .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_CLEAR_ALL))
                    .executes(TimesCommand::clearAllCourseTimes)
                )
            )
            .build()
        );
    }

}
