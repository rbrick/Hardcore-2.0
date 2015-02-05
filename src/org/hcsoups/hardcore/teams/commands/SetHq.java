package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamManagerUUID;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * Created by Ryan on 11/21/2014
 * <p/>
 * Project: HCSoups
 */
public class SetHq extends TeamSubCommand {
    public SetHq() {
        super("sethq", true, Arrays.asList("shq", "seth"), false);
    }

    @Override
    public void execute(Player p, String[] args) {
        if(TeamManagerUUID.getInstance().getPlayerTeam(p) == null) {
            p.sendMessage("§cYou are not on a team!");
        } else {
            if( TeamManagerUUID.getInstance().isManager(p)) {
                TeamManagerUUID.getInstance().getPlayerTeam(p).setHq(p.getLocation());
              //  TeamManager.getInstance().saveTeam(TeamManager.getInstance().getPlayerTeam(p));
                TeamManagerUUID.getInstance().messageTeam(TeamManagerUUID.getInstance().getPlayerTeam(p), "§3" + p.getName() + " has updated the teams HQ!");
            } else {
                p.sendMessage("§cYou must be at least a manager to perform this command.");
            }
        }
    }
}
