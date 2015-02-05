package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.*;

import java.util.Arrays;

/**
 * Created by Ryan on 11/22/2014
 * <p/>
 * Project: HCSoups
 */
public class Roster extends TeamSubCommand {
    public Roster() {
        super("roster", Arrays.asList("r"));
    }

    @Override
    public void execute(Player p, String[] args) {
        if(args.length != 1) {
            p.sendMessage("§c/team roster [Team]");
            return;
        }

        TeamUUID team = TeamManagerUUID.getInstance().matchTeam(args[0]);

        if(team == null) {
         p.sendMessage("§cTeam '" + args[0] + "' does not exist!");
         return;
        }
        TeamManagerUUID.getInstance().sendInfo(p, team);
    }
}
