package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.scoreboard.ScoreboardHandler;
import org.hcsoups.hardcore.scoreboard.ScoreboardTask;
import org.hcsoups.hardcore.teams.TeamAction;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class Join extends TeamSubCommand {


    public Join() {
        super("join", Arrays.asList("j"));
    }

    @Override
    public void execute(final Player p, String[] args) {
        if (args.length == 0 || args.length > 2) {
            p.sendMessage("§c/team join <Team> [Password]");
            return;
        } else {
            if (args.length == 1) {
                boolean success = TeamManager.getInstance().joinTeam(args[0], "", p);
                if (success) {
                    TeamManager.getInstance().updatePlayer(p, TeamManager.getInstance().getPlayerTeam(p));
                    TeamManager.getInstance().updateTeam(TeamManager.getInstance().getPlayerTeam(p), TeamAction.UPDATE);
                    p.remove();
                    ScoreboardTask.addTask(p.getName(), ScoreboardHandler.getBoards().get(p.getName()));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ScoreboardTask.removeTask(p.getName());
                        }
                    }.runTaskLaterAsynchronously(Hardcore.getPlugin(Hardcore.class), 40L);
                }
                return;
            }
            if (args.length == 2) {
                boolean success = TeamManager.getInstance().joinTeam(args[0], args[1], p);
                if (success) {
                    TeamManager.getInstance().updatePlayer(p, TeamManager.getInstance().getPlayerTeam(p));
                    TeamManager.getInstance().updateTeam(TeamManager.getInstance().getPlayerTeam(p), TeamAction.UPDATE);

                   ScoreboardTask.addTask(p.getName(), ScoreboardHandler.getBoards().get(p.getName()));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ScoreboardTask.removeTask(p.getName());
                        }
                    }.runTaskLaterAsynchronously(Hardcore.getPlugin(Hardcore.class), 40L);


                }
            }
        }

    }

}
