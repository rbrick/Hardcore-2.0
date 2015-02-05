package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamManagerUUID;
import org.hcsoups.hardcore.teams.TeamSubCommand;

/**
 * Created by Ryan on 11/21/2014
 * <p/>
 * Project: HCSoups
 */
public class Rally extends TeamSubCommand{

    public Rally() {
        super("rally");
    }

    @Override
    public void execute(Player p, String[] args) {
        if(args.length != 0) {
            p.sendMessage("§c/team rally");
            return;
        } else {

            if(!TeamManagerUUID.getInstance().isOnTeam(p.getUniqueId())) {
                p.sendMessage("§cYou are not on a team!");
                return;
            }
            TeamManagerUUID.getInstance().teamTeleport(p, "rally", TeamManagerUUID.getInstance().getPlayerTeam(p).getRally());
        }
    }
}
