package org.hcsoups.hardcore.spawn;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

            List<Block> blocks = new ArrayList<>();

            if(viewingSpawn.contains(p.getName())) {
                for (int y = SpawnManager.getInstance().getSpawn().getY(); y < 256; y++) {
                    Location loc1 =  SpawnManager.getInstance().getSpawn().getCorner1();
                    Location loc2 =  SpawnManager.getInstance().getSpawn().getCorner2();
                    Location loc3 =  SpawnManager.getInstance().getSpawn().getCorner3();
                    Location loc4 =  SpawnManager.getInstance().getSpawn().getCorner4();

                    loc1.setY(y);
                    loc2.setY(y);
                    loc3.setY(y);
                    loc4.setY(y);

                    if(y % 5 == 0) {
                        p.sendBlockChange(loc1, Material.AIR, (byte) 0);
                        p.sendBlockChange(loc2, Material.AIR, (byte) 0);
                        p.sendBlockChange(loc3, Material.AIR, (byte) 0);
                        p.sendBlockChange(loc4, Material.AIR, (byte) 0);
                    } else {
                        p.sendBlockChange(loc1, Material.AIR, (byte) 0);
                        p.sendBlockChange(loc2, Material.AIR, (byte) 0);
                        p.sendBlockChange(loc3, Material.AIR, (byte) 0);
                        p.sendBlockChange(loc4, Material.AIR, (byte) 0);
                    }
                }

                viewingSpawn.remove(p.getName());
            } else {

               if(SpawnManager.getInstance().getSpawn() == null) {
                   sender.sendMessage("§cSpawn has not been set yet!");
                   return;
               }
                for(int x=0; x < 100; x++) {
                   for(int y = 90; y < 100; y++) {
                       for (int z = 0; z < 100; z++) {
                         Location loc = new Location(p.getWorld(), x,y, z);
                              p.sendBlockChange(loc, Material.GLASS, (byte) 0);

                       }
                   }
                }
//                for (int y = SpawnManager.getInstance().getSpawn().getY(); y < 256; y++) {
//                       Location loc1 =  SpawnManager.getInstance().getSpawn().getCorner1();
//                       Location loc2 =  SpawnManager.getInstance().getSpawn().getCorner2();
//                       Location loc3 =  SpawnManager.getInstance().getSpawn().getCorner3();
//                       Location loc4 =  SpawnManager.getInstance().getSpawn().getCorner4();
//
//                    loc1.setY(y);
//                    loc2.setY(y);
//                    loc3.setY(y);
//                    loc4.setY(y);
//
//                    if(y % 5 == 0) {
//                        p.sendBlockChange(loc1, Material.EMERALD_BLOCK, (byte) 0);
//                        p.sendBlockChange(loc2, Material.EMERALD_BLOCK, (byte) 0);
//                        p.sendBlockChange(loc3, Material.EMERALD_BLOCK, (byte) 0);
//                        p.sendBlockChange(loc4, Material.EMERALD_BLOCK, (byte) 0);
//                    } else {
//                        p.sendBlockChange(loc1, Material.GLASS, (byte) 0);
//                        p.sendBlockChange(loc2, Material.GLASS, (byte) 0);
//                        p.sendBlockChange(loc3, Material.GLASS, (byte) 0);
//                        p.sendBlockChange(loc4, Material.GLASS, (byte) 0);
//                    }
//                }

                viewingSpawn.add(p.getName());
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
