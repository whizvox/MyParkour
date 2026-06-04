package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.command.CourseArgumentType;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.leaderboard.CourseTime;
import me.whizvox.myparkour.course.leaderboard.LeaderboardQuery;
import me.whizvox.myparkour.util.CommandUtils;
import me.whizvox.myparkour.util.MiniMessageUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class TimesCommand {

    public static final Permission PERMISSION_COURSE_TIMES = CommandUtils.createPermission("times.course");

    private static int showCourseTimes(CommandContext<CommandSourceStack> context) {
        Course course = CourseArgumentType.getCourse(context, "course");
        CommandSender sender = context.getSource().getSender();
        List<CourseTime> times = MyParkour.inst().getLeaderboards().getTimes(new LeaderboardQuery().setCourseId(course.id())).toList();
        if (!times.isEmpty()) {

        } else {
            sender.sendMessage(Messages.translate("myparkour.times.course.empty", Map.of("course", MiniMessage.miniMessage().deserialize(course.displayName()))));
        }
        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(Commands.literal("times")
            .then(Commands.literal("course")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_COURSE_TIMES))
                .then(Commands.argument("course", CourseArgumentType.course())
                    .executes(TimesCommand::showCourseTimes)
                )
            )
            .build()
        );
    }

}
