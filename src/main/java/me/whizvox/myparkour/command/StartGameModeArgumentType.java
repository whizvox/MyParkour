package me.whizvox.myparkour.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.myparkour.core.CommandExceptionTypes;
import me.whizvox.myparkour.course.StartGameMode;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@NotNullByDefault
public class StartGameModeArgumentType implements CustomArgumentType.Converted<StartGameMode, String> {

    private StartGameModeArgumentType() {}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StartGameMode.MAP.keySet().stream()
            .filter(gm -> gm.toLowerCase().startsWith(builder.getRemainingLowerCase()))
            .sorted()
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public StartGameMode convert(String nativeType) throws CommandSyntaxException {
        var result = StartGameMode.MAP.get(nativeType);
        if (result != null) {
            return result;
        }
        throw CommandExceptionTypes.INVALID_START_GAMEMODE.create(nativeType, StartGameMode.MAP.keySet().stream().sorted().collect(Collectors.joining(", ")));
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static StartGameModeArgumentType startGameMode() {
        return new StartGameModeArgumentType();
    }

    public static StartGameMode getStartGameMode(CommandContext<?> context, String name) {
        return context.getArgument(name, StartGameMode.class);
    }

}
