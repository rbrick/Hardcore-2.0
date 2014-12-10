package org.hcsoups.hardcore.teams.commands;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;

import java.util.Arrays;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
public class Info extends TeamSubCommand {
    public Info() {
        super("info", Arrays.asList("i"));
    }

    @Override
    public void execute(Player p, String[] args) {
        if(args.length > 1) {
            p.sendMessage("&c/team info [Player]");
            return;
        }

       if(args.length == 0) {
           TeamManager.getInstance().sendInfo(p);
           return;
       } else {
           if(args.length == 1) {
               TeamManager.getInstance().sendInfo(p, args[0]);
               return;
           }
       }


    }
}
