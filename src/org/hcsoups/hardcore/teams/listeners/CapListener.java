package org.hcsoups.hardcore.teams.listeners;

import code.BreakMC.valeon.events.TeamCaptureFlagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.combattag.CombatTagHandler;
import org.hcsoups.hardcore.scoreboard.ScoreboardHandler;
import org.hcsoups.hardcore.scoreboard.ScoreboardTask;

/**
 * Created by Ryan on 1/9/2015
 * <p/>
 * Project: HCSoups
 */
public class CapListener implements Listener {

    @EventHandler
    public void onCap(final TeamCaptureFlagEvent event) {
        event.getTeam().setValorPoints(event.getTeam().getValorPoints() + 1);

        for (final String man : event.getTeam().getManagers()) {
            ScoreboardTask.addTask(man, ScoreboardHandler.getBoards().get(man));

            new BukkitRunnable() {
                @Override
                public void run() {
                    ScoreboardTask.removeTask(man);
                }
            }.runTaskLaterAsynchronously(Hardcore.getPlugin(Hardcore.class), 40L);
        }

        for (final String mem : event.getTeam().getMembers()) {
            ScoreboardTask.addTask(mem, ScoreboardHandler.getBoards().get(mem));

            new BukkitRunnable() {
                @Override
                public void run() {
                    ScoreboardTask.removeTask(mem);
                }
            }.runTaskLaterAsynchronously(Hardcore.getPlugin(Hardcore.class), 40L);
        }
    }

}
