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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OfflinePlayerArgumentType implements CustomArgumentType.Converted<OfflinePlayer, String> {

    private OfflinePlayerArgumentType() {}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        // much less expensive compared to Bukkit.getOfflinePlayers()
        MyParkour.inst().getNames().stream()
            .map(entry -> entry.getValue().name())
            .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
            .sorted()
            .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public OfflinePlayer convert(String nativeType) throws CommandSyntaxException {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(nativeType);
        if (player != null) {
            return player;
        }
        try {
            UUID playerId = UUID.fromString(nativeType);
            player = Bukkit.getOfflinePlayer(playerId);
            if (player.hasPlayedBefore()) {
                return player;
            }
        } catch (IllegalArgumentException ignored) {}
        throw CommandExceptionTypes.UNKNOWN_OFFLINE_PLAYER.create(nativeType);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static OfflinePlayerArgumentType offlinePlayer() {
        return new OfflinePlayerArgumentType();
    }

    public static OfflinePlayer getOfflinePlayer(CommandContext<?> context, String name) {
        return context.getArgument(name, OfflinePlayer.class);
    }

}
