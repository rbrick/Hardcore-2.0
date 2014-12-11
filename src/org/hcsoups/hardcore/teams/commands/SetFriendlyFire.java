package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.Team;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class SetFriendlyFire extends TeamSubCommand {

     public SetFriendlyFire() {
         super("ff", true, Arrays.asList("friendlyfire", "sff","setfriendlyfire", "setff"), false);
     }

    @Override
    public void execute(Player p, String[] args) {
        if(args.length != 1) {
            p.sendMessage("§c/team ff <on/off>");
            return;
        }
        if(!TeamManager.getInstance().isOnTeam(p.getName())) {
            p.sendMessage("§cYou are not on a team.");
            return;
        } else if(!TeamManager.getInstance().isManager(p)) {
               p.sendMessage("§cYou must be at least a manager to perform this command.");
            return;
        } else {
            Team team = TeamManager.getInstance().getPlayerTeam(p);

            if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true")) {
                 if(team.isFriendlyFire()) {
                     p.sendMessage("§3Friendly fire is already turned on!");
                     return;
                 }
                 team.setFriendlyFire(true);
                 TeamManager.getInstance().messageTeam(team, "§3" + p.getName() + " has enabled friendly fire!");
            } else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false")) {
                team.setFriendlyFire(false);
                TeamManager.getInstance().messageTeam(team, "§3" + p.getName() + " has disabled friendly fire!");
            } else {
                p.sendMessage("§c/team ff <on/off>");
                return;
            }
        }

    }

}
