package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.teams.Team;
import org.hcsoups.hardcore.teams.TeamAction;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * Created by Ryan on 10/14/2014
 *
 * Project: DeathBan
 */
public class Promote extends TeamSubCommand {

    public Promote() {
        super("promote", true, Arrays.asList("p"));
    }

    @Override
    public void execute(Player p, String[] args) {
        if(args.length != 1) {
              p.sendMessage("&c/team promote <Player>");
              return;
        }
        if(!TeamManager.getInstance().isOnTeam(p.getName())) {
            p.sendMessage("&cYou are not on a team!");
            return;
        } else if (!TeamManager.getInstance().isManager(p)) {
            p.sendMessage("&cYou must be at least a manager to perform this command.");
            return;

        } else {

           final Team playerTeam = TeamManager.getInstance().getPlayerTeam(p);

            Team argsTeam = TeamManager.getInstance().getPlayerTeam(args[0]);

            if(argsTeam == null) {
                p.sendMessage("&cThat player is not on a team.");
                return;
            } else if(!playerTeam.equals(argsTeam)) {
                p.sendMessage("&cThat player is not on your team!");
                return;
            } else {

                if(p.getName().equals(args[0])) {
                    p.sendMessage("&cYou cannot promote your self!");
                    return;
                }

                // Trying to promote a manager
                if(playerTeam.getManagers().contains(args[0])) {
                   p.sendMessage("&cPlayer '" + args[0] + "' is already promoted!");
                    return;
                }

                playerTeam.getMembers().remove(args[0]);

                playerTeam.getManagers().add(args[0]);

                TeamManager.getInstance().saveTeam(playerTeam);
                TeamManager.getInstance().messageTeam(playerTeam, "&3" + p.getName() + " has promoted " + args[0] + ".");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TeamManager.getInstance().updateTeam(playerTeam, TeamAction.UPDATE);
                    }
                }.runTaskAsynchronously(Hardcore.getPlugin(Hardcore.class));

                return;
            }


        }


    }
}