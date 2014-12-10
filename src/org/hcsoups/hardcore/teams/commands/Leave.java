package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class Leave extends TeamSubCommand {


    public Leave() {
        super("leave", Arrays.asList("l"));
    }

    @Override
    public void execute(final Player p, String[] args) {
         if(args.length > 0) {
             p.sendMessage("&c/team leave");
             return;
         } else {
            new BukkitRunnable() {
                public void run() {
                    TeamManager.getInstance().leaveTeam(p);

                }
            }.runTaskAsynchronously(Hardcore.getPlugin(Hardcore.class));

         }
    }
}
