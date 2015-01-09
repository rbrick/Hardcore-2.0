package org.hcsoups.hardcore.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Created by Ryan on 1/4/2015
 * <p/>
 * Project: HCSoups
 */
public class MobLimiter implements Listener {
    @EventHandler
    public void limitMobs(CreatureSpawnEvent e) {
        if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) {
            if(e.getEntityType().equals(EntityType.SHEEP) && e.getLocation().getWorld().getEntitiesByClass(EntityType.SHEEP.getEntityClass()).size() >= 200) {
                e.setCancelled(true);
            } else if (e.getEntityType().equals(EntityType.COW) && e.getLocation().getWorld().getEntitiesByClass(EntityType.COW.getEntityClass()).size() >= 200) {
                e.setCancelled(true);
            } else if (e.getEntityType().equals(EntityType.CHICKEN) && e.getLocation().getWorld().getEntitiesByClass(EntityType.CHICKEN.getEntityClass()).size() >= 200) {
                e.setCancelled(true);
            } else if(e.getEntityType().equals(EntityType.PIG) && e.getLocation().getWorld().getEntitiesByClass(EntityType.PIG.getEntityClass()).size() >= 200) {
                e.setCancelled(true);
            }
        }
    }
}
