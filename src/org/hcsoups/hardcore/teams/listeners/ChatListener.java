package org.hcsoups.hardcore.teams.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamManagerUUID;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(TeamManagerUUID.getInstance().getTeamChat().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            String name = event.getPlayer().getName();
          if(TeamManagerUUID.getInstance().isManager(event.getPlayer())) {
                   name = "§3" + name;
            } else {
                name = "§7" + name;
             }
            TeamManagerUUID.getInstance().messageTeam(TeamManagerUUID.getInstance().getPlayerTeam(event.getPlayer()), String.format("§3(%s) %s§f: %s", TeamManagerUUID.getInstance().getPlayerTeam(event.getPlayer()).getName(),name, event.getMessage()));
        }
    }
}
