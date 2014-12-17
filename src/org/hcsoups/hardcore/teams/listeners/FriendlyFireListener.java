package org.hcsoups.hardcore.teams.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.hcsoups.hardcore.teams.Team;
import org.hcsoups.hardcore.teams.TeamManager;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class FriendlyFireListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player hitted = (Player) event.getDamager();
            Player hit = (Player) event.getEntity();

            if (TeamManager.getInstance().isOnTeam(hit.getName()) && TeamManager.getInstance().isOnTeam(hitted.getName())) {
                Team hittedTeam = TeamManager.getInstance().getPlayerTeam(hitted);
                Team hitTeam = TeamManager.getInstance().getPlayerTeam(hit);

                if (hitTeam.equals(hittedTeam)) {
                    if (!hitTeam.isFriendlyFire()) {
                        event.setCancelled(true);
                    }
                }

            }
        }

        if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
            Projectile hitt = (Projectile) event.getDamager();
            if (hitt.getShooter() instanceof Player) {
                Player hitted = (Player) hitt.getShooter();
                Player hit = (Player) event.getEntity();

                if (TeamManager.getInstance().isOnTeam(hit.getName()) && TeamManager.getInstance().isOnTeam(hitted.getName())) {
                    Team hittedTeam = TeamManager.getInstance().getPlayerTeam(hitted);
                    Team hitTeam = TeamManager.getInstance().getPlayerTeam(hit);

                    if (hitTeam.equals(hittedTeam)) {
                        if (!hitTeam.isFriendlyFire()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }


}
