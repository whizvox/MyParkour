package me.whizvox.myparkour.command;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.whizvox.myparkour.MyParkour;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public record TranslatableCommandExceptionType(String key, int argumentCount) implements CommandExceptionType {

    public TranslatableCommandExceptionType(String key) {
        this(key, 0);
    }

    private String translate(Object... args) {
        Preconditions.checkState(args.length == argumentCount, "Expected %d arguments, but got %d instead".formatted(argumentCount, args.length));
        String format = MyParkour.inst().getTranslations().getPlainString(key);
        if (args.length == 0) {
            return format;
        }
        return format.formatted(args);
    }

    public CommandSyntaxException create(Object... args) {
        return new CommandSyntaxException(this, new LiteralMessage(translate(args)));
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object... args) {
        return new CommandSyntaxException(this, new LiteralMessage(translate(args)), reader.getString(), reader.getCursor());
    }

}
