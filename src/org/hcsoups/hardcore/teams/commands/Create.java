package org.hcsoups.hardcore.teams.commands;


import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.scoreboard.ScoreboardHandler;
import org.hcsoups.hardcore.scoreboard.ScoreboardTask;
import org.hcsoups.hardcore.teams.*;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class Create extends TeamSubCommand {

    public Create() {
        super("create");
    }

    @Override
    public void execute(final Player p, String[] args) {
        // 0 = create, 1 = name, 2 = pass
        if (args.length == 0 || args.length > 2) {
            p.sendMessage("§c/team create <Name> [Password]");
            return;
        }

        if(TeamManagerUUID.getInstance().isOnTeam(p.getUniqueId())) {
            p.sendMessage("§cYou are already in a team!");
            return;
        }

        if (args.length == 1) {
            final TeamUUID team = TeamManagerUUID.getInstance().createTeam(p, args[0]);
            if (team != null) {
                //    TeamManager.getInstance().saveTeam(team);

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
            final TeamUUID team = TeamManagerUUID.getInstance().createTeam(p, args[0], args[1]);
            if (team != null) {
                // TeamManager.getInstance().saveTeam(team);

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
