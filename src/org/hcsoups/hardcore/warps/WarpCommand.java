package org.hcsoups.hardcore.warps;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.hcsoups.hardcore.command.BaseCommandAnn;
import org.hcsoups.hardcore.command.IBaseCommand;
import org.hcsoups.hardcore.zeus.annotations.Command;

/**
 * Created by Ryan on 11/23/2014
 * <p/>
 * Project: HCSoups
 */
public class WarpCommand implements IBaseCommand {

    @BaseCommandAnn(name="warp", aliases = "go", minArgs = 1,maxArgs = 2, usage = "&cInvalid /<command> usage. Try:\n" +
            "&7/<command> <name> - Teleport to a warp, if it exists.\n" +
            "&7/<command> list - List all your warps.\n" +
            "&7/<command> set <name> - Set a warp at your current location.\n" +
            "&7/<command> del <name> - Delete a warp.")
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        if (args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                WarpManager.getInstance().listWarps(((Player) sender));
                return;
            } else {
                if (args[0].equalsIgnoreCase("set")) {
                  sender.sendMessage("&c/warp set <Name>");
                } else if(args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
                    sender.sendMessage("&c/warp del <Name>");
                } else {

                    if (canPerformWarpOrWarpSet(((Player) sender).getLocation())) {
                        sender.sendMessage("&cYou may not warp within 512 blocks of spawn.");
                        return;
                    }

                    WarpManager.getInstance().warp(((Player) sender), args[0]);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (canPerformWarpOrWarpSet(((Player) sender).getLocation())) {
                    sender.sendMessage("&cYou may not set warps within 512 blocks of spawn.");
                    return;
                }
                WarpManager.getInstance().setWarp(((Player) sender), args[1]);
                return;
            } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
                WarpManager.getInstance().delWarp(((Player) sender), args[1]);
            } else {
                sender.sendMessage("&cInvalid /warp usage. Try:\n" +
                        "&7/warp <name> - Teleport to a warp, if it exists.\n" +
                        "&7/warp list - List all your warps.\n" +
                        "&7/warp set <name> - Set a warp at your current location.\n" +
                        "&7/warp del <name> - Delete a warp.");
            }
        }
    }



    public boolean canPerformWarpOrWarpSet(Location location) {
        return location.getX() <= 512 && location.getX() >= -512 && location.getZ() <= 512 && location.getZ() >= -512;
    }

}
