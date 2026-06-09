package me.whizvox.myparkour.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.myparkour.core.CommandExceptionTypes;
import me.whizvox.myparkour.course.ExitGameMode;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ExitGameModeArgumentType implements CustomArgumentType.Converted<ExitGameMode, String> {

    private ExitGameModeArgumentType() {}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ExitGameMode.MAP.keySet().stream()
            .filter(gm -> gm.toLowerCase().startsWith(builder.getRemainingLowerCase()))
            .sorted()
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ExitGameMode convert(String nativeType) throws CommandSyntaxException {
        var result = ExitGameMode.MAP.get(nativeType);
        if (result != null) {
            return result;
        }
        throw CommandExceptionTypes.INVALID_EXIT_GAMEMODE.create(nativeType, ExitGameMode.MAP.keySet().stream().sorted().collect(Collectors.joining(", ")));
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static ExitGameModeArgumentType exitGameMode() {
        return new ExitGameModeArgumentType();
    }

    public static ExitGameMode getExitGameMode(CommandContext<?> context, String name) {
        return context.getArgument(name, ExitGameMode.class);
    }

}
