package org.hcsoups.hardcore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hcsoups.hardcore.combattag.CombatTagHandler;
import org.hcsoups.hardcore.scoreboard.DaybreakBoard;
import org.hcsoups.hardcore.scoreboard.ScoreboardHandler;
import org.hcsoups.hardcore.stats.StatManager;
import org.hcsoups.hardcore.teams.TeamManager;

/**
 * Created by Ryan on 12/30/2014
 * <p/>
 * Project: HCSoups
 */
public class JoinListener implements Listener {

    @EventHandler
    public void join(final PlayerJoinEvent event) {
        StatManager.getInstance().createStats(event.getPlayer());
        //event.getPlayer().setDisplayName("§c§l" + event.getPlayer().getName());

       // Hardcore.getPlugin(Hardcore.class).getHandler().add(event.getPlayer());

        DaybreakBoard board = new DaybreakBoard(event.getPlayer());
        board.update();
        ScoreboardHandler.addBoard(event.getPlayer(), board);


        event.setJoinMessage(null);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        StatManager.getInstance().createStats(event.getPlayer()).save();

        if (CombatTagHandler.isTagged(event.getPlayer())) {
            event.getPlayer().setHealth(0d);
            CombatTagHandler.inCombat.remove(event.getPlayer().getName());
        }

        //Hardcore.getPlugin(Hardcore.class).getHandler().remove(event.getPlayer());
        event.setQuitMessage(null);

    }
}
