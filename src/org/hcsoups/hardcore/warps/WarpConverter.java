package org.hcsoups.hardcore.warps;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ryan on 1/25/2015
 * <p/>
 * Project: HCSoups
 */
public class WarpConverter {
    public void convert(CommandSender player) {
        Bukkit.broadcastMessage("§aLoading old warps...");
        WarpManager.getInstance().loadWarps();
        Bukkit.broadcastMessage("§aWarps have been loaded.");
        HashMap<String, List<Warp>> oldWarps = WarpManager.getInstance().getWarps();
        HashMap<UUID, List<Warp>> newWarps = new HashMap<>();
        Bukkit.broadcastMessage("Starting warp conversion...");
        for(String n : oldWarps.keySet()) {
            UUID id = Bukkit.getOfflinePlayer(n).getUniqueId();
            player.sendMessage("Converting " + n + " -> " + id.toString());
            newWarps.put(id, oldWarps.get(n));
            WarpManagerUUID.getInstance().setWarps(newWarps); // lol
        }
        WarpManagerUUID.getInstance().setWarps(newWarps);
        WarpManagerUUID.getInstance().saveWarps();
        Bukkit.broadcastMessage("Warps have been converted successfully!");
    }
}
