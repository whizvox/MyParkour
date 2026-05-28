package me.whizvox.myparkour;

import me.whizvox.myparkour.course.CourseFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MyParkourEventHandler implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        MyParkour.inst().getRuns().getRun(player).ifPresent(run -> {
            event.setCancelled(true);
            player.heal(player.getHealth());
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

}
