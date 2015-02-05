package org.hcsoups.hardcore.teams.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.teams.*;

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
              p.sendMessage("§c/team promote <Player>");
              return;
        }
        if(!TeamManagerUUID.getInstance().isOnTeam(p.getUniqueId())) {
            p.sendMessage("§cYou are not on a team!");
            return;
        } else if (!TeamManagerUUID.getInstance().isManager(p)) {
            p.sendMessage("§cYou must be at least a manager to perform this command.");
            return;

        } else {

           final TeamUUID playerTeam = TeamManagerUUID.getInstance().getPlayerTeam(p);

            TeamUUID argsTeam = TeamManagerUUID.getInstance().getPlayerTeam(args[0]);

            if(argsTeam == null) {
                p.sendMessage("§cThat player is not on a team.");

            } else if(!playerTeam.equals(argsTeam)) {
                p.sendMessage("§cThat player is not on your team!");

            } else {

                if(p.getName().equals(args[0])) {
                    p.sendMessage("§cYou cannot promote your self!");
                    return;
                }

                OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);

                // Trying to promote a manager
                if(playerTeam.getManagers().contains(op.getUniqueId())) {
                   p.sendMessage("§cPlayer '" + args[0] + "' is already promoted!");
                    return;
                }

                playerTeam.getMembers().remove(op.getUniqueId());

                playerTeam.getManagers().add(op.getUniqueId());

             //   TeamManagerUUID.getInstance().saveTeam(playerTeam);
                TeamManagerUUID.getInstance().messageTeam(playerTeam, "§3" + p.getName() + " has promoted " + args[0] + ".");

                return;
            }


        }


    }
}