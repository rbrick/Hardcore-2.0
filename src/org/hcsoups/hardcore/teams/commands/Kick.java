package org.hcsoups.hardcore.teams.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.TeamAction;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class Kick extends TeamSubCommand {
    public Kick() {
        super("kick", true, Arrays.asList("k"), false);
    }

    @Override
    public void execute(final Player p, final String[] args) {
        if (args.length == 0 || args.length > 1) {
            p.sendMessage("§c/team kick <Player>");
            return;
        }
        if (!TeamManager.getInstance().isOnTeam(p.getName())) {
            p.sendMessage("§cYou are not on a team!");
        } else {
            if (TeamManager.getInstance().isManager(p)) {
                if (!TeamManager.getInstance().isOnTeam(args[0]) || !TeamManager.getInstance().getPlayerTeam(args[0]).equals(TeamManager.getInstance().getPlayerTeam(p))) {
                    p.sendMessage("§cThis player is not on your team!");
                } else if (p.getName().equals(args[0])) {
                    p.sendMessage("§cYou cannot kick yourself!");
                } else {

                    TeamManager.getInstance().getPlayerTeam(p).getManagers().remove(args[0]);
                    TeamManager.getInstance().getPlayerTeam(p).getMembers().remove(args[0]);
                    TeamManager.getInstance().messageTeam(TeamManager.getInstance().getPlayerTeam(p), "§3" + p.getName() + " has kicked '" + args[0] + "' from the team!");
                    //     TeamManager.getInstance().saveTeam(TeamManager.getInstance().getPlayerTeam(p));
                    TeamManager.getInstance().removePlayer(args[0]); // bam
                    TeamManager.getInstance().getInTeam().remove(args[0]);
                    //    TeamManager.getInstance().saveInTeam();
                    TeamManager.getInstance().getTeamChat().remove(args[0]);
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Bukkit.getPlayer(args[0]).sendMessage("§3You have been kicked from the team!");
                    }

                    TeamManager.getInstance().updateTeam(TeamManager.getInstance().getPlayerTeam(p), TeamAction.UPDATE);



                }
            } else {
                p.sendMessage("§cYou must be at least a manager to perform this command!");
            }
        }
    }
}
