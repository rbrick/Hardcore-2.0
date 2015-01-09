package org.hcsoups.hardcore.combattag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.hcsoups.hardcore.spawn.SpawnManager;

import java.util.HashMap;

/**
 * Created by Ryan on 1/1/2015
 * <p/>
 * Project: HCSoups
 */
public class CombatTagHandler implements Listener {

    public static HashMap<String, Long> inCombat = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
           // Bukkit.broadcastMessage("Cancelled");
        } else
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();

            if(SpawnManager.getInstance().hasSpawnProt(damaged)) {
                return;
            }

            //  Bukkit.broadcastMessage(((Player) event.getDamager()).getName() + " hit " + ((Player) event.getEntity()).getName());
            if (isTagged(damaged)) {
                inCombat.put(damaged.getName(), System.currentTimeMillis() + (60 * 1000));
                //Bukkit.broadcastMessage(((Player) event.getDamager()).getName() + " hit " + ((Player) event.getEntity()).getName());
            } else {
                inCombat.put(damaged.getName(), System.currentTimeMillis() + (60 * 1000));
                damaged.sendMessage("§aYou are now in combat!");
            }
            if (isTagged(damager)) {
                inCombat.put(damager.getName(), System.currentTimeMillis() + (60 * 1000));
                //  Bukkit.broadcastMessage(((Player) event.getDamager()).getName() + " hit " + ((Player) event.getEntity()).getName());
            } else {
                inCombat.put(damager.getName(), System.currentTimeMillis() + (60 * 1000));
                damager.sendMessage("§aYou are now in combat!");
            }

        }

    }


    public static boolean isTagged(Player player) {
        return inCombat.containsKey(player.getName()) && inCombat.get(player.getName()) >= System.currentTimeMillis();
    }


    public static void remove(final Player player) {
        inCombat.remove(player.getName());
    }

    public static long getTag(Player player) {
        return (inCombat.containsKey(player.getName()) ? inCombat.get(player.getName()) - System.currentTimeMillis() : -1);
    }

}
