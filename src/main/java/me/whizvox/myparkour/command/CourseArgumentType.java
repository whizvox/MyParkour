package me.whizvox.myparkour.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.core.CommandExceptionTypes;
import me.whizvox.myparkour.course.Course;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CourseArgumentType implements CustomArgumentType.Converted<Course, String> {

    private CourseArgumentType() {}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Stream<Course> stream = MyParkour.inst().getCourses().stream();
        if (!builder.getRemainingLowerCase().isEmpty()) {
            stream = stream.filter(course -> course.name().toLowerCase().startsWith(builder.getRemainingLowerCase()));
        }
        stream.forEach(course -> builder.suggest(course.name()));
        return builder.buildFuture();
    }

    @Override
    public Course convert(String name) throws CommandSyntaxException {
        return MyParkour.inst().getCourses()
            .get(name)
            .orElseThrow(() -> CommandExceptionTypes.UNKNOWN_COURSE.create(name));
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static CourseArgumentType course() {
        return new CourseArgumentType();
    }

    public static Course getCourse(CommandContext<?> context, String name) {
        return context.getArgument(name, Course.class);
    }

}
