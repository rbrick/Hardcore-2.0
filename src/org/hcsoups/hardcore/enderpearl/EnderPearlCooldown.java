package org.hcsoups.hardcore.enderpearl;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by Ryan on 12/17/2014
 * <p/>
 * Project: HCSoups
 */
public class EnderPearlCooldown implements Listener {


    HashMap<String, Long> endercoolDown = new HashMap<>();
    HashMap<String, BukkitRunnable> endercoolDownR = new HashMap<>();


    @EventHandler
    public void on(PlayerInteractEvent event) {
         if(event.hasItem()) {
              if(event.getItem().getType() == Material.ENDER_PEARL) {
                  if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                      if (event.getPlayer().hasPermission("hardcore.cooldown.bypass")) {
                          return;
                       }

                       if(endercoolDown.containsKey(event.getPlayer().getName())) {
                           Long lastThrow = endercoolDown.get(event.getPlayer().getName());

                           Long now = System.currentTimeMillis();

                           Long nextthrowyo = lastThrow + (10 * 1000); // Talk to gabe

                            if(now < nextthrowyo) {
                               long YOUONCOOLDOWNBITCH = (nextthrowyo - now);
                               event.getPlayer().sendMessage("§cOn cooldown for " + formatTime(YOUONCOOLDOWNBITCH));
                            }
                           endercoolDown.put(event.getPlayer().getName(), System.currentTimeMillis());
                       }



                      endercoolDown.put(event.getPlayer().getName(), System.currentTimeMillis());
                  }

              }
         }
    }
    public String formatTime(long time) {

        long second = 1000;

        long minute = 60 * 1000;

        long minutes = time / minute;

        long seconds = (time-(minutes*minute))/second;

        if(seconds < 10) {
            String newSeconds = "0" + seconds;
            return minutes + ":" + newSeconds;
        }

        return minutes + ":" + seconds;
    }



}
