package org.hcsoups.hardcore.teams.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.scoreboard.ScoreboardHandler;
import org.hcsoups.hardcore.scoreboard.ScoreboardTask;
import org.hcsoups.hardcore.teams.TeamAction;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamManagerUUID;
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
        if (!TeamManagerUUID.getInstance().isOnTeam(p.getUniqueId())) {
            p.sendMessage("§cYou are not on a team!");
        } else {
            if (TeamManagerUUID.getInstance().isManager(p)) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
                if (!TeamManagerUUID.getInstance().isOnTeam(op.getUniqueId()) || !TeamManagerUUID.getInstance().getPlayerTeam(args[0]).equals(TeamManagerUUID.getInstance().getPlayerTeam(p))) {
                    p.sendMessage("§cThis player is not on your team!");
                } else if (p.getName().equals(args[0])) {
                    p.sendMessage("§cYou cannot kick yourself!");
                } else {

                    TeamManagerUUID.getInstance().getPlayerTeam(p).getManagers().remove(op.getUniqueId());
                    TeamManagerUUID.getInstance().getPlayerTeam(p).getMembers().remove(op.getUniqueId());
                    TeamManagerUUID.getInstance().messageTeam(TeamManagerUUID.getInstance().getPlayerTeam(p), "§3" + p.getName() + " has kicked '" + args[0] + "' from the team!");
                    //     TeamManager.getInstance().saveTeam(TeamManager.getInstance().getPlayerTeam(p));
                    TeamManagerUUID.getInstance().getInTeam().remove(op.getUniqueId());
                    //    TeamManager.getInstance().saveInTeam();
                    TeamManagerUUID.getInstance().getTeamChat().remove(op.getUniqueId());
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Bukkit.getPlayer(args[0]).sendMessage("§3You have been kicked from the team!");
                        ScoreboardTask.addTask(Bukkit.getPlayer(args[0]).getName(), ScoreboardHandler.getBoards().get(Bukkit.getPlayer(args[0]).getName()));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ScoreboardTask.removeTask(Bukkit.getPlayer(args[0]).getName());
                            }
                        }.runTaskLaterAsynchronously(Hardcore.getPlugin(Hardcore.class), 40L);
                    }
                }
            } else {
                p.sendMessage("§cYou must be at least a manager to perform this command!");
            }
        }
    }
}
