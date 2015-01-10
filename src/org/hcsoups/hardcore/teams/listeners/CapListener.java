package org.hcsoups.hardcore.teams.listeners;

import code.BreakMC.valeon.events.TeamCaptureFlagEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Ryan on 1/9/2015
 * <p/>
 * Project: HCSoups
 */
public class CapListener implements Listener {

    @EventHandler
    public void onCap(TeamCaptureFlagEvent event) {
        event.getTeam().setValorPoints(event.getTeam().getValorPoints() + 1);
    }

}
