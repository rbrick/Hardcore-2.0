package org.hcsoups.hardcore.teams.commands;


import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.teams.Team;
import org.hcsoups.hardcore.teams.TeamAction;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

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
            p.sendMessage("&c/team create <Name> [Password]");
            return;
        }

        if(TeamManager.getInstance().isOnTeam(p.getName())) {
            p.sendMessage("&cYou are already in a team!");
            return;
        }

        if (args.length == 1) {
            final Team team = TeamManager.getInstance().createTeam(p, args[0]);
            if (team != null) {
                //    TeamManager.getInstance().saveTeam(team);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TeamManager.getInstance().updatePlayer(p, team);
                        TeamManager.getInstance().updateTeam(team, TeamAction.UPDATE);
                    }
                }.runTaskAsynchronously(Hardcore.getPlugin(Hardcore.class));
            }


            return;
        }

        if (args.length == 2) {
            final Team team = TeamManager.getInstance().createTeam(p, args[0], args[1]);
            if (team != null) {
                // TeamManager.getInstance().saveTeam(team);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        TeamManager.getInstance().updatePlayer(p, team);
                        TeamManager.getInstance().updateTeam(team, TeamAction.UPDATE);
                    }
                }.runTaskAsynchronously(Hardcore.getPlugin(Hardcore.class));

            }

            return;
        }
    }
}
