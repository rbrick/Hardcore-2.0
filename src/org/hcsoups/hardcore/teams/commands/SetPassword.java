package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.Team;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class SetPassword extends TeamSubCommand {

    public SetPassword() {
        super("pass",true, Arrays.asList("spw", "spwd", "setpw", "setpwd", "pass", "password", "setpassword"), false);
    }

    @Override
    public void execute(Player p, String[] args) {
      if(args.length != 1) {
          p.sendMessage("§c/team pass <Password>");
          return;
      }
      if(!TeamManager.getInstance().isOnTeam(p.getName())) {
          p.sendMessage("§cYou are not on a team!");
          return;
      } else if(!TeamManager.getInstance().isManager(p)) {
          p.sendMessage("§cYou must be at least a manager to perform this command!");
          return;
      } else {
          Team team = TeamManager.getInstance().getPlayerTeam(p);
          if(args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("null") || args[0].equalsIgnoreCase("nil")) {
              team.setPassword("");
              TeamManager.getInstance().messageTeam(team, "§7" + p.getName() + " has turned off password protection!");
              TeamManager.getInstance().saveTeam(team);
              return;
          }
          String old_pass = team.getPassword();
          team.setPassword(args[0]);
          TeamManager.getInstance().messageTeam(team, "§7" + p.getName() + " has set the team password to '" + team.getPassword() + "'.");
          TeamManager.getInstance().saveTeam(team);
          return;
      }


    }
}
