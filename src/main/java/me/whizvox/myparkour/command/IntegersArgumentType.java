package me.whizvox.myparkour.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class IntegersArgumentType implements CustomArgumentType.Converted<IntList, String> {

    private IntegersArgumentType() {}

    @Override
    public IntList convert(String str) throws CommandSyntaxException {
        String[] split = str.split(" ");
        IntList list = new IntArrayList(split.length);
        for (String s : split) {
            try {
                int index = Integer.parseInt(s);
                list.add(index);
            } catch (NumberFormatException e) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(s);
            }
        }
        return list;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    public static IntegersArgumentType integers() {
        return new IntegersArgumentType();
    }

    public static IntList getIntegers(CommandContext<?> context, String name) {
        return context.getArgument(name, IntList.class);
    }

}
