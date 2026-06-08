package me.whizvox.myparkour.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.core.CommandExceptionTypes;
import me.whizvox.myparkour.course.leaderboard.CourseTime;

public class CourseTimeArgumentType implements CustomArgumentType.Converted<CourseTime, Integer> {

    private CourseTimeArgumentType() {}

    @Override
    public CourseTime convert(Integer nativeType) throws CommandSyntaxException {
        return MyParkour.inst().getLeaderboards().getTime(nativeType)
            .orElseThrow(() -> CommandExceptionTypes.UNKNOWN_COURSE_TIME.create(nativeType));
    }

    @Override
    public ArgumentType<Integer> getNativeType() {
        return IntegerArgumentType.integer(1);
    }

    public static CourseTimeArgumentType time() {
        return new CourseTimeArgumentType();
    }

    public static CourseTime getTime(CommandContext<?> context, String name) {
        return context.getArgument(name, CourseTime.class);
    }

}
