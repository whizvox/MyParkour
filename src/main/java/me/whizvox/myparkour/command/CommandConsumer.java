package me.whizvox.myparkour.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface CommandConsumer<T> {

    void run(T obj) throws CommandSyntaxException;

}
