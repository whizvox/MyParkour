package me.whizvox.myparkour.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.myparkour.Messages;
import me.whizvox.myparkour.MyParkour;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static me.whizvox.myparkour.Messages.KEY_COMMAND_VERSION;

public class MyParkourCommand {

    private static Permission perm(String name) {
        return new Permission("myparkour." + name);
    }

    private static final Permission
        PERM_VERSION = perm("version"),
        PERM_CREATE = perm("create");

    private static boolean senderHasPermission(CommandSourceStack source, Permission permission) {
        return source.getSender().hasPermission(permission);
    }

    private static boolean playerHasPermission(CommandSourceStack source, Permission permission) {
        return source.getSender() instanceof Player && senderHasPermission(source, permission);
    }

    private static int version(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(Messages.translate(KEY_COMMAND_VERSION, Map.of("version", MyParkour.inst().getPluginMeta().getVersion())));
        return SINGLE_SUCCESS;
    }

    private static int createNewCourse(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        if (MyParkour.inst().getEdits().isPlayerEditing(player)) {

        }
        String name = StringArgumentType.getString(context, "name");

        return SINGLE_SUCCESS;
    }

    public static void register(Commands commands) {
        commands.register(Commands.literal("myparkour")
            .then(Commands.literal("version")
                .requires(source -> senderHasPermission(source, PERM_VERSION))
                .executes(MyParkourCommand::version)
            )
            .then(Commands.literal("create")
                .requires(source -> playerHasPermission(source, PERM_CREATE))
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(MyParkourCommand::createNewCourse)
                )
            )
            .build()
        );
    }

}
