package me.whizvox.myparkour.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.myparkour.core.CommandExceptionTypes;
import me.whizvox.myparkour.course.CourseFlag;

import java.util.concurrent.CompletableFuture;

public class CourseFlagArgumentType implements CustomArgumentType.Converted<CourseFlag, String> {

    private CourseFlagArgumentType() {}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        CourseFlag.NAMES.keySet().stream()
            .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
            .sorted()
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public CourseFlag convert(String str) throws CommandSyntaxException {
        return CourseFlag.parse(str).orElseThrow(() -> CommandExceptionTypes.INVALID_COURSE_FLAG.create(str));
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static CourseFlagArgumentType flag() {
        return new CourseFlagArgumentType();
    }

    public static CourseFlag getFlag(CommandContext<?> context, String name) {
        return context.getArgument(name, CourseFlag.class);
    }

}
