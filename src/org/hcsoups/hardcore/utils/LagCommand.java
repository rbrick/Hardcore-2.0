package org.hcsoups.hardcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.hcsoups.hardcore.command.BaseCommand;

/**
 * Created by Ryan on 1/3/2015
 * <p/>
 * Project: HCSoups
 */
public class LagCommand extends BaseCommand {

    public LagCommand() {
        super("lag", null, "tps", "elag", "etps");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ChatColor color = ChatColor.GREEN;
        int tps = ((int) Math.round(Lag.getTPS()));
        double lag = Math.round((1.0D - tps / 20.0D) * 100.0D);
        switch (tps) {
            case 20:
            case 19:
            case 18:
            case 17:
            case 16:
                break;
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
                color = ChatColor.GOLD;
                break;
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 0:
                color = ChatColor.DARK_RED;
                break;
            default:
                break;
        }
        sender.sendMessage("§cThere is currently " + color + lag + "% §clag.");
        sender.sendMessage("§cServer is currently running at " + color + tps + " TPS§c.");


    }
}
