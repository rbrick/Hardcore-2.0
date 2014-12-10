package org.hcsoups.hardcore.teams.commands;


import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.TeamManager;
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
        if(TeamManager.getInstance().isOnTeam(p.getName())) {
           if(TeamManager.getInstance().getTeamChat().contains(p.getName())) {
               p.sendMessage("&3Now talking in public chat.");
               TeamManager.getInstance().getTeamChat().remove(p.getName());
           } else  {
               p.sendMessage("&3Now talking in team chat.");
               TeamManager.getInstance().getTeamChat().add(p.getName());
           }
        } else {
            p.sendMessage("&cYou are not on a team!");
        }


    }
}
