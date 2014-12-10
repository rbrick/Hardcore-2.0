package org.hcsoups.hardcore.tracking;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;

public class TrackingMethods {

    int mx;
    int my;
    int mz;

    public void setLoc(int x, int y, int z) {
        this.mx = x;
        this.my = y;
        this.mz = z;
    }

    public boolean checkPlayer(Player pl, int x, int z) {
        int num = 0;
        if (x == 0) {
            int plz = pl.getLocation().getBlockZ();
            num = Math.abs(z);
            if (Math.abs(this.mz - plz) < num) {
                if (z < 0) {
                    if (plz <= this.mz)
                        return true;
                } else if (plz >= this.mz)
                    return true;
            }
        } else if (z == 0) {
            int plz = pl.getLocation().getBlockX();
            num = Math.abs(x);
            if (Math.abs(this.mx - plz) < num) {
                if (x < 0) {
                    if (plz <= this.mx)
                        return true;
                } else if (plz >= this.mx)
                    return true;
            }
        }
        return false;
    }

    public void TrackDir(Player player, int x, int z, Player player2) {
        String compass = "North";
        int num = Math.abs(x) + Math.abs(z);

        boolean can = checkPlayer(player2, x, z);
        if (z < 0) {
            compass = "North";
        } else if (z > 0) {
            compass = "South";
        }
        if (x < 0) {
            compass = "West";
        } else if (x > 0) {
            compass = "East";
        }
        if (player == player2) {
            player.sendMessage(player2.getDisplayName() + ChatColor.GREEN + " IS " + "within " + num + " blocks " + compass);
        } else {
            player.sendMessage(player2.getDisplayName() + (can ? ChatColor.GREEN + " IS " : ChatColor.RED + " IS NOT ") + "within " + num + " blocks " + compass);
        }
    }

    public int findBlock(World world, int x, int z, Material mat1, Material mat2) {
        boolean hasmat = true;
        int length = 0;
        for (int i = 1; i < 1000; i++) {
            Block block = world.getBlockAt(this.mx + x * i, this.my, this.mz + z * i);
            Material bmat = block.getType();
            if (hasmat) {
                  if (bmat == mat1) {
                      length++;
                      if (mat1 == Material.COBBLESTONE) {
                          block.setType(Material.AIR);
                      }
                  } else {

                      if (bmat == mat2) {
                          hasmat = false;
                          length++;
                          if (mat1 != Material.COBBLESTONE) {
                              break;
                          }
                          block.setType(Material.AIR);
                          break;
                      }
                      return 0;
                  }

            }
        }
        if ((length > 0) && (!hasmat))
            return length;
        return 0;
    }

    public void Track(Material mat1, Material mat2, Player player, Player player2) {

        Block block = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()).getBlock();
        boolean northDists = findEnd(player.getWorld(), 0, -1, mat1, mat2);
        boolean southDists = findEnd(player.getWorld(), 0, 1, mat1, mat2);
        boolean eastDists = findEnd(player.getWorld(), -1, 0, mat1, mat2);
        boolean westDists = findEnd(player.getWorld(), 1, 0, mat1, mat2);
        int northDist = findBlock(player.getWorld(), 0, -1, mat1, mat2);
        int southDist = findBlock(player.getWorld(), 0, 1, mat1, mat2);
        int eastDist = findBlock(player.getWorld(), -1, 0, mat1, mat2);
        int westDist = findBlock(player.getWorld(), 1, 0, mat1, mat2);

        if (northDists && northDist > 0) {
            TrackDir(player, 0, -northDist * 25, player2);
        }
        if (eastDists && eastDist > 0) {
            TrackDir(player, -eastDist * 25, 0, player2);
        }
        if (southDists && southDist > 0) {
            TrackDir(player, 0, southDist * 25, player2);
        }
        if (westDists && westDist > 0) {
            TrackDir(player, westDist * 25, 0, player2);
        }
        if ((block.getType() == Material.OBSIDIAN)) {
            player.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getData());
            block.breakNaturally(new ItemStack(Material.AIR));
        }
    }

    public void Track(Player player, Player player2) {
        Block block = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()).getBlock();
        if (block.getType() == Material.DIAMOND_BLOCK && isPerm(block)) {
            Track(Material.OBSIDIAN, Material.GOLD_BLOCK, player, player2);
        } else if (block.getType() == Material.OBSIDIAN && isTemp( block)) {
            Track(Material.COBBLESTONE, Material.STONE, player, player2);
        } else {
            player.sendMessage(ChatColor.RED + "You need to be on a tracking block");
        }
    }

    public boolean isTemp(Block b1) {
        boolean istemp = false;

        if (b1.getType() == Material.OBSIDIAN) {
            double left = b1.getLocation().getX() + 1;
            double left2 = b1.getLocation().getX() + 2;

            double right = b1.getLocation().getX() - 1;
            double right2 = b1.getLocation().getX() - 2;

            double front = b1.getLocation().getZ() - 1;
            double front2 = b1.getLocation().getZ() - 2;

            double back = b1.getLocation().getZ() + 1;
            double back2 = b1.getLocation().getZ() + 2;
            Block leftb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), left, b1.getLocation().getY(), b1.getLocation().getZ()));
            Block leftb2 = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), left2, b1.getLocation().getY(), b1.getLocation().getZ()));

            Block rightb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), right, b1.getLocation().getY(), b1.getLocation().getZ()));
            Block rightb2 = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), right2, b1.getLocation().getY(), b1.getLocation().getZ()));

            Block frontb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), b1.getLocation().getX(), b1.getLocation().getY(), front));
            Block frontb2 = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), b1.getLocation().getX(), b1.getLocation().getY(), front2));

            Block backb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), b1.getLocation().getX(), b1.getLocation().getY(), back));
            Block backb2 = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), b1.getLocation().getX(), b1.getLocation().getY(), back2));

            if (leftb.getType() == Material.STONE || leftb.getType() == Material.COBBLESTONE) {
                istemp = true;
            } else if (rightb.getType() == Material.STONE || rightb.getType() == Material.COBBLESTONE) {
                istemp = true;
            } else if (frontb.getType() == Material.STONE || frontb.getType() == Material.COBBLESTONE ) {
                istemp = true;
            } else if (backb.getType() == Material.STONE || backb.getType() == Material.COBBLESTONE) {
                istemp = true;
            } else {
                istemp = false;
            }

        }
        return istemp;
    }

    public boolean isPerm(Block b1) {
        boolean isperm = false;

        if (b1.getType() == Material.DIAMOND_BLOCK) {
            double left = b1.getLocation().getX() + 1;

            double right = b1.getLocation().getX() - 1;

            double front = b1.getLocation().getZ() - 1;

            double back = b1.getLocation().getZ() + 1;
            Block leftb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), left, b1.getLocation().getY(), b1.getLocation().getZ()));

            Block rightb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), right, b1.getLocation().getY(), b1.getLocation().getZ()));

            Block frontb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), b1.getLocation().getX(), b1.getLocation().getY(), front));

            Block backb = Bukkit.getWorld(b1.getWorld().getName()).getBlockAt(new Location(b1.getWorld(), b1.getLocation().getX(), b1.getLocation().getY(), back));

            if (leftb.getType() == Material.GOLD_BLOCK || leftb.getType() == Material.OBSIDIAN) {
                isperm = true;
            } else if (rightb.getType() == Material.GOLD_BLOCK || rightb.getType() == Material.OBSIDIAN) {
                isperm = true;
            } else if (frontb.getType() == Material.GOLD_BLOCK || frontb.getType() == Material.OBSIDIAN) {
                isperm = true;
            } else if (backb.getType() == Material.GOLD_BLOCK || backb.getType() == Material.OBSIDIAN ) {
                isperm = true;
            } else {
                isperm = false;
            }

        }
        return isperm;
    }

    public void TrackDirAll(Player player, int x, int z, Player player2) {
        StringBuilder sbm;
        String compass = "West";
        List<String> in = new ArrayList<String>();
        int num = Math.abs(x) + Math.abs(z);
        if (player2 == null) {
            for (int i = 0; i < Bukkit.getOnlinePlayers().length; i++) {

                Player pl = Bukkit.getOnlinePlayers()[i];
                boolean can = checkPlayer(pl, x, z);
                if (can) {
                    in.add(pl.getName());
                }
            }
        } else {
            boolean can = checkPlayer(player2, x, z);
            if (can) {
                in.add(player2.getName());
            }
        }
        // String str = "";
        // for (int i = 0; i < in.size(); i++) {
        // str = str + ((Player)in.get(i)).getName() + ", ";
        // }

        if (z < 0) {
            compass = "North";
            if (in.size() == 0) {
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): ");
            } else {
                // sbm = new StringBuilder("{\"text\":\"" + compass + " (" + num
                // + "):\",\"color\":\"dark_aqua\",\"extra\":[{\"text\":\" " +
                // ChatColor.stripColor(in.get(0))+
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"&aClick here to track &f'"
                // + in.get(0)+
                // "&f'&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // +ChatColor.stripColor(in.get(0)) + "\"}}");
                // if (in.size() > 1) {
                // for (int i = 1; i < in.size(); i++) {
                // sbm.append(",{\"text\":\", \",\"color\":\"gray\"}").append(",{\"text\":\""
                // + ChatColor.stripColor(in.get(i)) +
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"&aClick here to track &f'"
                // + in.get(i) +
                // "&f'&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // + ChatColor.stripColor(in.get(i)) + "\"}}");
                // }
                // }
                // //lastly we append the end
                // sbm.append("]}");
                //
                // cbc = ChatSerializer.a(sbm.toString());
                //
                // PacketPlayOutChat packet = new PacketPlayOutChat(cbc);
                //
                // ((CraftPlayer)
                // player).getHandle().playerConnection.sendPacket(packet);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < in.size(); i++) {
                    builder.append(in.get(i)).append(", ");
                }

                String str = builder.toString().trim();
                str = str.substring(0, str.length() - 1);
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): " + ChatColor.GRAY + str);
            }
        } else if (x > 0) {
            compass = "East";
            if (in.size() == 0) {
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): ");
            } else {
                // sbm = new StringBuilder("{\"text\":\"" + compass + " (" + num
                // + "):\",\"color\":\"dark_aqua\",\"extra\":[{\"text\":\" " +
                // ChatColor.stripColor(in.get(0))+
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Â&aClick here to track Â&f'"
                // + in.get(0)+
                // "Â&f'Â&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // +ChatColor.stripColor(in.get(0)) + "\"}}");
                // if (in.size() > 1) {
                // for (int i = 1; i < in.size(); i++) {
                // sbm.append(",{\"text\":\", \",\"color\":\"gray\"}").append(",{\"text\":\""
                // + ChatColor.stripColor(in.get(i)) +
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Â&aClick here to track Â&f'"
                // + in.get(i) +
                // "Â&f'Â&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // + ChatColor.stripColor(in.get(i)) + "\"}}");
                // }
                // }
                // //lastly we append the end
                // sbm.append("]}");
                //
                // cbc = ChatSerializer.a(sbm.toString());
                //
                // PacketPlayOutChat packet = new PacketPlayOutChat(cbc);
                //
                // ((CraftPlayer)
                // player).getHandle().playerConnection.sendPacket(packet);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < in.size(); i++) {
                    builder.append(in.get(i)).append(", ");
                }

                String str = builder.toString().trim();
                str = str.substring(0, str.length() - 1);
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): " + ChatColor.GRAY + str);
            }
        } else if (z > 0) {
            compass = "South";
            if (in.size() == 0) {
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): ");
            } else {
                // sbm = new StringBuilder("{\"text\":\"" + compass + " (" + num
                // + "):\",\"color\":\"dark_aqua\",\"extra\":[{\"text\":\" " +
                // ChatColor.stripColor(in.get(0))+
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Â&aClick here to track Â&f'"
                // + in.get(0)+
                // "Â&f'Â&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // +ChatColor.stripColor(in.get(0)) + "\"}}");
                // if (in.size() > 1) {
                // for (int i = 1; i < in.size(); i++) {
                // sbm.append(",{\"text\":\", \",\"color\":\"gray\"}").append(",{\"text\":\""
                // + ChatColor.stripColor(in.get(i)) +
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Â&aClick here to track Â&f'"
                // + in.get(i) +
                // "Â&f'Â&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // + ChatColor.stripColor(in.get(i)) + "\"}}");
                // }
                // }
                // //lastly we append the end
                // sbm.append("]}");
                //
                // cbc = ChatSerializer.a(sbm.toString());
                //
                // PacketPlayOutChat packet = new PacketPlayOutChat(cbc);
                //
                // ((CraftPlayer)
                // player).getHandle().playerConnection.sendPacket(packet);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < in.size(); i++) {
                    builder.append(in.get(i)).append(", ");
                }

                String str = builder.toString().trim();
                str = str.substring(0, str.length() - 1);
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): " + ChatColor.GRAY + str);
            }
        } else if (x < 0) {
            compass = "West";
            if (in.size() == 0) {
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): ");
            } else {
                // sbm = new StringBuilder("{\"text\":\"" + compass + " (" + num
                // + "):\",\"color\":\"dark_aqua\",\"extra\":[{\"text\":\" " +
                // ChatColor.stripColor(in.get(0))+
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Â&aClick here to track Â&f'"
                // + in.get(0)+
                // "Â&f'Â&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // +ChatColor.stripColor(in.get(0)) + "\"}}");
                // if (in.size() > 1) {
                // for (int i = 1; i < in.size(); i++) {
                // sbm.append(",{\"text\":\", \",\"color\":\"gray\"}").append(",{\"text\":\""
                // + ChatColor.stripColor(in.get(i)) +
                // "\",\"color\":\"gray\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Â&aClick here to track Â&f'"
                // + in.get(i) +
                // "Â&f'Â&a.\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/track "
                // + ChatColor.stripColor(in.get(i)) + "\"}}");
                // }
                // }
                // //lastly we append the end
                // sbm.append("]}");
                //
                // cbc = ChatSerializer.a(sbm.toString());
                //
                // PacketPlayOutChat packet = new PacketPlayOutChat(cbc);
                //
                // ((CraftPlayer)
                // player).getHandle().playerConnection.sendPacket(packet);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < in.size(); i++) {
                    builder.append(in.get(i)).append(", ");
                }

                String str = builder.toString().trim();
                str = str.substring(0, str.length() - 1);
                player.sendMessage(ChatColor.DARK_AQUA + compass + " (" + num + "): " + ChatColor.GRAY + str);
            }
        }

    }

    public void TrackAll(final Material mat1,final Material mat2, final Player player, final Player player2) {

        new BukkitRunnable() {
            @Override
            public void run() {

                int northDist = findBlock(player.getWorld(), 0, -1, mat1, mat2);
                int southDist = findBlock(player.getWorld(), 0, 1, mat1, mat2);
                int eastDist = findBlock(player.getWorld(), -1, 0, mat1, mat2);
                int westDist = findBlock(player.getWorld(), 1, 0, mat1, mat2);
                boolean northDists = findEnd(player.getWorld(), 0, -1, mat1, mat2);
                boolean southDists = findEnd(player.getWorld(), 0, 1, mat1, mat2);
                boolean eastDists = findEnd(player.getWorld(), -1, 0, mat1, mat2);
                boolean westDists = findEnd(player.getWorld(), 1, 0, mat1, mat2);

                player.sendMessage(ChatColor.DARK_AQUA + "Results:");
                if (northDist > 0 && northDists) {
                    TrackDirAll(player, 0, -northDist * 25, player2);
                }

                if (eastDist > 0 && eastDists) {
                    TrackDirAll(player, -eastDist * 25, 0, player2);
                }
                if (southDist > 0 && southDists) {

                    TrackDirAll(player, 0, southDist * 25, player2);
                }
                if (westDist > 0 && westDists) {
                    TrackDirAll(player, westDist * 25, 0, player2);
                }
            }
        }.runTask(Hardcore.getPlugin(Hardcore.class));

//        Block block = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()).getBlock();
//        int northDist = findBlock(player.getWorld(), 0, -1, mat1, mat2);
//        int southDist = findBlock(player.getWorld(), 0, 1, mat1, mat2);
//        int eastDist = findBlock(player.getWorld(), -1, 0, mat1, mat2);
//        int westDist = findBlock(player.getWorld(), 1, 0, mat1, mat2);
//        boolean northDists = findEnd(player.getWorld(), 0, -1, mat1, mat2);
//        boolean southDists = findEnd(player.getWorld(), 0, 1, mat1, mat2);
//        boolean eastDists = findEnd(player.getWorld(), -1, 0, mat1, mat2);
//        boolean westDists = findEnd(player.getWorld(), 1, 0, mat1, mat2);
//
//        player.sendMessage(ChatColor.DARK_AQUA + "Results:");
//        if (northDist > 0 && northDists) {
//            TrackDirAll(player, 0, -northDist * 25, player2);
//        }
//
//        if (eastDist > 0 && eastDists) {
//            TrackDirAll(player, -eastDist * 25, 0, player2);
//        }
//        if (southDist > 0 && southDists) {
//
//            TrackDirAll(player, 0, southDist * 25, player2);
//        }
//        if (westDist > 0 && westDists) {
//            TrackDirAll(player, westDist * 25, 0, player2);
//        }
        // Cannot track all on a temp tracker
        // if ((block.getType() == Material.OBSIDIAN) && isTemp(player, block))
        // {
        // player.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND,
        // block.getData());
        // block.breakNaturally(new ItemStack(Material.AIR));
        // }
    }

    public void TrackAll(Player player, Player player2) {
        Block block = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() - 1, player.getLocation().getBlockZ()).getBlock();
        if (block.getType() == Material.DIAMOND_BLOCK && isPerm(block)) {
            TrackAll(Material.OBSIDIAN, Material.GOLD_BLOCK, player, player2);
        } else if (isTemp(block) && block.getType().equals(Material.OBSIDIAN)) {
            player.sendMessage(ChatColor.RED + "You cannot /track all on this type of tracker");
        } else {
            player.sendMessage(ChatColor.RED + "You need to be on a tracking block");
        }
    }

    // mat1 never used :P
    public boolean findEnd(final World world,final int x, final int z, Material mat1, Material mat2) {
        final HasEnd hasEnd = new HasEnd();
        hasEnd.setYes(false);
        // int dist = findBlock(world, x, z ,mat1, mat2);
                for (int i = 1; i < 1000; i++) {
                    Block block = world.getBlockAt(this.mx + x * i, this.my, this.mz + z * i);
                    Material bmat = block.getType();

                    if (bmat == mat2) {
                        hasEnd.setYes(true);
                        break;
                    }
                }
        return hasEnd.isYes();

    }

    @Data
    private class HasEnd {
        boolean yes;
    }
}
