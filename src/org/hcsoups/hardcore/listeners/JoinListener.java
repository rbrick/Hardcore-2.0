package org.hcsoups.hardcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hcsoups.hardcore.stats.StatManager;

/**
 * Created by Ryan on 12/30/2014
 * <p/>
 * Project: HCSoups
 */
public class JoinListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        StatManager.getInstance().createStats(event.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        StatManager.getInstance().createStats(event.getPlayer()).save();
    }
}
