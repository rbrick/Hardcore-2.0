package org.hcsoups.hardcore.spawn;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcsoups.hardcore.zeus.annotations.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 11/26/2014
 * <p/>
 * Project: HCSoups
 */
public class SpawnCommand  {

    List<String> viewingSpawn  = new ArrayList<>();

    @Command(name = "setspawn", aliases = {"ss"}, minArgs = 2, maxArgs = 2, usage = "§cUsage: /<command> <radius> <height>", permission = "hcsoups.hardcore.setspawn")
    public void setspawn(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
           Player player = (Player) sender;
            try {
                int radius = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                Spawn spawn = new Spawn(radius,y);
                SpawnManager.getInstance().setSpawn(spawn);
                player.sendMessage(String.format("§aSpawn has been set with a radius of '%d' with a height of '%d'.", radius, y));
            } catch (NumberFormatException ex) {
                player.sendMessage("§c'" + args[0] + "' is not a number!");
            }
        }
    }

    @Command(name = "spawn")
    public void spawn(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            SpawnManager.getInstance().spawnTeleport(player);
        }
    }

    @Command(name="spawnview")
    public void spawnview(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            String anim= "|";
            String inital = "|";
            for (int x =0 ; x < 100 ; x++) {
                inital += "=";
               p.sendMessage(inital + "\r");
            }
            }

    }



//    @Command(name="clearspawn", aliases = {"cs"})
//    public void clearspawn(CommandSender sender, String[] args) {
//        Spawn spawn = SpawnManager.getInstance().getSpawn();
//
//        if (spawn == null) {
//            sender.sendMessage("§cSpawn not set up yet!");
//            return;
//        }
//
//        for (int x = -20; x < 20; x++) {
//            for (int y = 62; y < 80; y++) {
//                for(int z = -20; z < 20; z++) {
//                    Block block = Bukkit.getWorld("world").getBlockAt(x,y,z);
//                    if (spawn.contains(block.getLocation())) {
//                        block.setType(Material.QUARTZ);
//                        continue;
//                    }
//                    block.setType(Material.AIR);
//                }
//            }
//        }
//    }
}
