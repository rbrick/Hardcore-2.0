package org.hcsoups.hardcore.teams.commands;


import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamManagerUUID;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class Chat extends TeamSubCommand {

    public Chat() {
        super("chat", Arrays.asList("c", "ch"));
    }

    @Override
    public void execute(Player p, String[] args) {
        if(TeamManagerUUID.getInstance().isOnTeam(p.getUniqueId())) {
           if(TeamManager.getInstance().getTeamChat().contains(p.getName())) {
               p.sendMessage("§3Now talking in public chat.");
               TeamManagerUUID.getInstance().getTeamChat().remove(p.getUniqueId());
           } else  {
               p.sendMessage("§3Now talking in team chat.");
               TeamManagerUUID.getInstance().getTeamChat().add(p.getUniqueId());
           }
        } else {
            p.sendMessage("§cYou are not on a team!");
        }
    }
}
