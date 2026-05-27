package me.whizvox.myparkour.core.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.util.CommandUtils;
import org.bukkit.permissions.Permission;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.myparkour.Messages.key;
import static me.whizvox.myparkour.util.CommandUtils.createPermission;

public class MyParkourCommand {

    public static final Permission
        PERMISSION_VERSION = createPermission("version"),
        PERMISSION_RELOAD = createPermission("reload");

    public static final String
        KEY_VERSION = key("command.version"),
        KEY_RELOAD = key("command.reload");

    private static int version(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(Messages.translate(KEY_VERSION, Map.of("version", MyParkour.inst().getPluginMeta().getVersion())));
        return SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        MyParkour.inst().reload();
        context.getSource().getSender().sendMessage(Messages.translate(KEY_RELOAD));
        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(Commands.literal("myparkour")
            .then(Commands.literal("version")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_VERSION))
                .executes(MyParkourCommand::version)
            )
            .then(Commands.literal("reload")
                .requires(source -> CommandUtils.senderHasPermission(source, PERMISSION_RELOAD))
                .executes(MyParkourCommand::reload)
            )
            .build()
        );
    }

}
