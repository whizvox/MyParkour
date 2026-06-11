package me.whizvox.myparkour;

import me.whizvox.myparkour.course.CourseFlag;
import me.whizvox.myparkour.sign.*;
import me.whizvox.myparkour.util.BlockLocation;
import me.whizvox.myparkour.util.CommandUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.Optional;

@NotNullByDefault
public class MyParkourEventListener implements Listener {

    public static final Permission
        PERMISSION_SIGN_PLACE = CommandUtils.createPermission("sign.place");

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        MyParkour.inst().getRuns().getRun(player).ifPresent(run -> {
            event.setCancelled(true);
            player.heal(player.getHealth());
            player.setFireTicks(0);
            run.teleportToLastCheckpoint();
        });
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            MyParkour.inst().getRuns().getRun(player).ifPresent(run -> {
                if (!run.getCourse().flags().contains(CourseFlag.FALL_DAMAGE)) {
                    event.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MyParkour.inst().getNames().update(event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign || event.getBlock().getState() instanceof WallSign) {
            BlockLocation location = new BlockLocation(event.getBlock().getLocation());
            if (MyParkour.inst().getSigns().exists(location) && event.getPlayer().hasPermission(PERMISSION_SIGN_PLACE)) {
                MyParkour.inst().getSigns().delete(location);
                event.getPlayer().sendMessage(Messages.translate("myparkour.sign.break"));
            }
        }
    }

    @EventHandler
    public void onSignChanged(SignChangeEvent event) {
        BlockLocation loc = new BlockLocation(event.getBlock().getLocation());;
        if (MyParkour.inst().getSigns().exists(loc)) {
            event.setCancelled(true);
        } else {
            List<String> lines = event.lines().stream()
                .map(comp -> PlainTextComponentSerializer.plainText().serialize(comp))
                .toList();
            if (!lines.isEmpty()) {
                if (lines.getFirst().equals("[Parkour]")) {
                    if (lines.size() > 1) {
                        Optional<ParkourSign> pSignOp = switch (lines.get(1).toLowerCase()) {
                            case "exit" -> Optional.of(new ParkourExitSign());
                            case "run" -> {
                                if (lines.size() > 2) {
                                    yield MyParkour.inst().getCourses().get(lines.get(2)).map(ParkourRunSign::new);
                                } else {
                                    yield Optional.empty();
                                }
                            }
                            case "coursetimes" -> {
                                if (lines.size() > 2) {
                                    yield MyParkour.inst().getCourses().get(lines.get(2)).map(ParkourCourseTimesSign::new);
                                } else {
                                    yield Optional.empty();
                                }
                            }
                            case "selftimes" -> Optional.of(new ParkourSelfTimesSign());
                            default -> Optional.empty();
                        };
                        pSignOp.ifPresent(pSign -> {
                            event.line(0, MiniMessage.miniMessage().deserialize("<aqua><bold>[Parkour]</bold></aqua>"));
                            boolean shouldReplace = MyParkour.inst().getSigns().exists(loc);
                            if (shouldReplace) {
                                MyParkour.inst().getSigns().delete(loc);
                            }
                            MyParkour.inst().getSigns().register(loc, pSign);
                            event.getPlayer().sendMessage(Messages.translate("myparkour.sign." + (shouldReplace ? "replace" : "place")));
                        });
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock() && (event.getClickedBlock().getState() instanceof Sign || event.getClickedBlock().getState() instanceof WallSign)) {
            BlockLocation loc = new BlockLocation(event.getClickedBlock().getLocation());
            MyParkour.inst().getSigns().get(loc).ifPresent(sign -> {
                event.setCancelled(true);
                sign.action(event.getPlayer());
            });
        }
    }

}
