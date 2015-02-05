package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.*;

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
        if(!TeamManagerUUID.getInstance().isOnTeam(p.getUniqueId())) {
            p.sendMessage("§cYou are not on a team.");
            return;
        } else if(!TeamManagerUUID.getInstance().isManager(p)) {
               p.sendMessage("§cYou must be at least a manager to perform this command.");
            return;
        } else {
            TeamUUID team = TeamManagerUUID.getInstance().getPlayerTeam(p);

            if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true")) {
                 if(team.isFriendlyFire()) {
                     p.sendMessage("§3Friendly fire is already turned on!");
                     return;
                 }
                 team.setFriendlyFire(true);
                TeamManagerUUID.getInstance().messageTeam(team, "§3" + p.getName() + " has enabled friendly fire!");
            } else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false")) {
                team.setFriendlyFire(false);
                TeamManagerUUID.getInstance().messageTeam(team, "§3" + p.getName() + " has disabled friendly fire!");
            } else {
                p.sendMessage("§c/team ff <on/off>");
                return;
            }
        }

    }

}
