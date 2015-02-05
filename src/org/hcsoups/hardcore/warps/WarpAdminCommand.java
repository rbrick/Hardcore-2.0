package org.hcsoups.hardcore.warps;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcsoups.hardcore.command.BaseCommand;

/**
 * Created by Ryan on 1/2/2015
 * <p/>
 * Project: HCSoups
 */
public class WarpAdminCommand extends BaseCommand {

    public WarpAdminCommand() {
        super("warpas", "warps.admin", "goas");
        setUsage("§c/<command> <player> [warp]");
        setMinArgs(1);
        setMaxArgs(2);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                String name = args[0];
                WarpManagerUUID.getInstance().listWarpsAdmin(((Player) sender), name);
            }
            if (args.length == 2) {
                String name = args[0];
                Warp warp = WarpManagerUUID.getInstance().matchWarp(name, args[1]);

                if (warp == null) {
                    sender.sendMessage("§cWarp '" + args[1] + "' does not exist for player '" + name + "'");
                } else {
                    WarpManagerUUID.getInstance().warpAdmin(((Player) sender), name, warp);
                }
            }

        }
    }
}
