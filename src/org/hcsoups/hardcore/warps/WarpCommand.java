package org.hcsoups.hardcore.warps;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcsoups.hardcore.command.BaseCommand;
import org.hcsoups.hardcore.spawn.SpawnManager;

/**
 * Created by Ryan on 11/23/2014
 * <p/>
 * Project: HCSoups
 */
public class WarpCommand extends BaseCommand {

    public WarpCommand() {
        super("warp", null, "go");
        setMaxArgs(2);
        setMinArgs(1);
        setUsage("§cInvalid /<command> usage. Try:\n" +
                "§7/<command> <name> - Teleport to a warp, if it exists.\n" +
                "§7/<command> list - List all your warps.\n" +
                "§7/<command> set <name> - Set a warp at your current location.\n" +
                "§7/<command> del <name> - Delete a warp.");
    }
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        if (args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                WarpManagerUUID.getInstance().listWarps(((Player) sender));
                return;
            } else {
                if (args[0].equalsIgnoreCase("set")) {
                  sender.sendMessage("§c/warp set <Name>");
                } else if(args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
                    sender.sendMessage("§c/warp del <Name>");
                } else {

                    if (SpawnManager.getInstance().getSpawn().contains(((Player) sender).getLocation())) {
                        sender.sendMessage("§cYou may not warp within spawn.");
                        return;
                    }

                    WarpManagerUUID.getInstance().warp(((Player) sender), args[0]);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (SpawnManager.getInstance().getSpawn().contains(((Player) sender).getLocation())) {
                    sender.sendMessage("§cYou may not set warps within spawn.");
                    return;
                }
                WarpManagerUUID.getInstance().setWarp(((Player) sender), args[1]);
                return;
            } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
                WarpManagerUUID.getInstance().delWarp(((Player) sender), args[1]);
            } else {
                sender.sendMessage("§cInvalid /warp usage. Try:\n" +
                        "§7/warp <name> - Teleport to a warp, if it exists.\n" +
                        "§7/warp list - List all your warps.\n" +
                        "§7/warp set <name> - Set a warp at your current location.\n" +
                        "§7/warp del <name> - Delete a warp.");
            }
        }
    }



    public boolean canPerformWarpOrWarpSet(Location location) {
        return location.getX() <= 512 && location.getX() >= -512 && location.getZ() <= 512 && location.getZ() >= -512;
    }

}
