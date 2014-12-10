package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

/**
 * Created by Ryan on 11/21/2014
 * <p/>
 * Project: HCSoups
 */
public class Hq extends TeamSubCommand {
    public Hq() {
        super("hq");
    }

    @Override
    public void execute(Player p, String[] args) {
        if(args.length != 0) {
            p.sendMessage("&c/team hq");
            return;
        } else {
            TeamManager.getInstance().teamTeleport(p, "hq", TeamManager.getInstance().getPlayerTeam(p).getHq());
        }
    }
}
