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
 * <p/>
 * Project: DeathBan
 */
public class Demote extends TeamSubCommand {

    public Demote() {
        super("demote", true, Arrays.asList("d"));
    }

    @Override
    public void execute(Player p, String[] args) {
        if(args.length != 1) {
            p.sendMessage("§c/team demote <Player>");
            return;
        }
        if(!TeamManagerUUID.getInstance().isOnTeam(p.getUniqueId())) {
            p.sendMessage("§cYou are not on a team!");

        } else if (!TeamManagerUUID.getInstance().isManager(p)) {
            p.sendMessage("§cYou must be at least a manager to perform this command.");

        } else {

            final TeamUUID playerTeam = TeamManagerUUID.getInstance().getPlayerTeam(p);

            TeamUUID argsTeam = TeamManagerUUID.getInstance().getPlayerTeam(args[0]);

            if(argsTeam == null) {
                p.sendMessage("§cThat player is not on a team.");
            } else if(!playerTeam.equals(argsTeam)) {
                p.sendMessage("§cThat player is not on your team!");
            } else {

                if(p.getName().equals(args[0])) {
                    p.sendMessage("§cYou cannot demote your self!");
                    return;
                }

                OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);

                // Trying to promote a manager
                if(playerTeam.getMembers().contains(op.getUniqueId())) {
                    p.sendMessage("§cPlayer '" + args[0] + "' is already a member!");
                    return;
                }

                playerTeam.getManagers().remove(op.getUniqueId());
                playerTeam.getMembers().add(op.getUniqueId());

               // TeamManager.getInstance().saveTeam(playerTeam);
                TeamManagerUUID.getInstance().messageTeam(playerTeam, "§3" + p.getName() + " has demoted '" + args[0] + "'.");

            }


        }


    }
}
