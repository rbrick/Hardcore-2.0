package org.hcsoups.hardcore.warps;

import com.mongodb.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.zeus.annotations.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ryan on 11/22/2014
 * <p/>
 * Project: HCSoups
 */
public class WarpManagerUUID implements Listener {

    private HashMap<UUID, List<Warp>> warps = new HashMap<UUID, List<Warp>>();

    private HashMap<String, BukkitRunnable> overriding = new HashMap<String, BukkitRunnable>();

    private List<String> cantAttack = new ArrayList<>();

    static WarpManagerUUID instance = new WarpManagerUUID();

    private DBCollection warpc = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("warps");


    public void listWarps(Player player) {
        if(!warps.containsKey(player.getUniqueId()) || warps.containsKey(player.getUniqueId()) && warps.get(player.getUniqueId()).isEmpty()) {
            player.sendMessage("§7***Warp List(0/" + warpSize(player) + ")***\n[]");
            return;
        }
        player.sendMessage("§7***Warp List(" + warps.get(player.getUniqueId()).size() + "/" + warpSize(player) + ")***");

        StringBuilder builder = new StringBuilder();

        FancyMessage message = new FancyMessage("[").color(ChatColor.GRAY).then();

        List<Warp> pwarps = warps.get(player.getUniqueId());


        for (int i = 0; i < pwarps.size(); i++) {
            if(i < pwarps.size()-1) {
                message.text(pwarps.get(i).getName()).color(ChatColor.GRAY)
                        .tooltip("§aClick here to warp to '§f" + pwarps.get(i).getName() + "§a'.")
                        .command("/warp " + pwarps.get(i).getName())
                        .then(", ").color(ChatColor.GRAY).then();
            } else {
                message.text(pwarps.get(i).getName()).color(ChatColor.GRAY)
                        .tooltip("§aClick here to warp to '§f" + pwarps.get(i).getName() + "§a'.")
                        .command("/warp " + pwarps.get(i).getName()).then();
            }
        }

        message.text("]").color(ChatColor.GRAY);
        message.send(player);
    }

    public void warpAdmin(Player player,String toCheck, Warp warp) {
         player.sendMessage("§7Warped to " + toCheck +"'s " + warp.getName());
         player.teleport(warp.getLocation());
    }


    public void listWarpsAdmin(Player player, String name) {
         if (matchPlayer(name) == null) {
             player.sendMessage("§cCould not find player '" + name + "'.");
         } else {
             UUID playerS = matchPlayer(name);
             if(!warps.containsKey(playerS) || warps.containsKey(playerS) && warps.get(playerS).isEmpty()) {
                 player.sendMessage(String.format("§7Showing warps for %s: \n[]", playerS));
             } else {
                 StringBuilder builder = new StringBuilder();

                 for (Warp warp : warps.get(playerS)) {
                     builder.append(warp.getName()).append(", ");
                 }

                 String warpList = builder.toString().trim();

                 if (warpList.endsWith(",")) {
                     warpList = warpList.substring(0, warpList.length() - 1);
                 }

                 player.sendMessage(String.format("§7Showing warps for %s: \n[%s]", playerS, warpList));
             }
         }
    }


    public void setWarp(final Player player, String name) {

        if (!name.matches("^[A-Za-z0-9_+-]*$")) {
            player.sendMessage("§cInvalid warp name!");
            return;
        }

        if (matchWarp(player.getName(), name) != null) {
            TempWarp twarp = new TempWarp(matchWarp(player.getName(), name), player.getLocation());
            player.sendMessage(String.format("§eYou already have a warp with the name '§a%s§e'\n§eIf you would like to overwrite the warp type §2/yes\n§eor type §c/no §eto cancel the request.\n§eThis will expire in 10 seconds.", twarp.getWarp().getName()));
            player.setMetadata("warpToOverride", new FixedMetadataValue(Hardcore.getPlugin(Hardcore.class), twarp));
            overriding.put(player.getName(), new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage("§cDid not receive an answer in time! Canceling request.");
                }
            });

            overriding.get(player.getName()).runTaskLater(Hardcore.getPlugin(Hardcore.class), 200L);
        } else {
            if (warps.get(player.getUniqueId()) != null && warps.get(player.getUniqueId()).size() >= warpSize(player)) {
                player.sendMessage("§cYou have set the max amount of warps!");
                return;
            }
            List<Warp> pwarps = warps.get(player.getUniqueId());

            if(pwarps == null) {
                pwarps = new ArrayList<Warp>();
            }
            Warp warp = new Warp(name, player.getLocation());
            pwarps.add(warp);
            warps.put(player.getUniqueId(), pwarps);
            player.sendMessage(String.format("§7Warp '%s' has been set!", warp.getName()));
        }
    }

    public void delWarp(Player player, String name) {
          if (matchWarp(player.getName(), name) == null || !warps.containsKey(player.getUniqueId()) || warps.containsKey(player.getUniqueId()) && warps.get(player.getUniqueId()).isEmpty()) {
              player.sendMessage(String.format("§cWarp '%s' does not exist!", name));
          } else {
            List<Warp> warpsp = warps.get(player.getUniqueId());
            Warp warp2rem = matchWarp(player.getName(), name);
            warpsp.remove(warp2rem);
            warps.put(player.getUniqueId(), warpsp);
            player.sendMessage(String.format("§7Warp '%s' has been deleted.", warp2rem.getName()));
          }
    }


    public void warp(final Player player, String warp) {
        if (matchWarp(player.getName(), warp) == null || !warps.containsKey(player.getUniqueId()) || warps.containsKey(player.getUniqueId()) && warps.get(player.getUniqueId()).isEmpty()) {
            player.sendMessage(String.format("§cWarp '%s' does not exist!", warp));
            return;
        }
        warpTeleport(player, matchWarp(player.getName(), warp));
//        player.sendMessage("§7You cannot attack for 10 seconds!");
//        cantAttack.add(player.getName());
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//              cantAttack.remove(player.getName());
//            }
//        }.runTaskLater(Hardcore.getPlugin(Hardcore.class), 10 * 20L);

    }


    public void warpTeleport(final Player p, final Warp warp) {
            if(canTeleport(p)) {
                p.teleport(warp.getLocation());
                p.sendMessage(String.format("§7Warped to %s!", warp.getName()));
            } else {
                if(TeamManager.getInstance().getDontMove().containsKey(p.getName())) {
                    TeamManager.getInstance().getDontMove().get(p.getName()).cancel();
                    System.out.println("Cancelling timer for " + p.getName());
                }
                TeamManager.getInstance().getDontMove().put(p.getName(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.teleport(warp.getLocation());
                        p.sendMessage(String.format("§7Warped to %s!", warp.getName()));
                        TeamManager.getInstance().getDontMove().remove(p.getName());
                    }
                });
                TeamManager.getInstance().getDontMove().get(p.getName()).runTaskLater(Hardcore.getPlugin(Hardcore.class), 10*20L);
                p.sendMessage("§7Someone is nearby! Warping in 10 seconds! Do not move!");
            }
    }

    public boolean canTeleport(Player p) {
        for(Entity ent : p.getNearbyEntities(40, 20, 40)) {
            if(ent instanceof Player) {

                Player near = (Player) ent;
                if(near.equals(p)) continue;

                if(TeamManager.getInstance().isOnTeam(near.getName()) && TeamManager.getInstance().isOnTeam(p.getName())) {
                    if(TeamManager.getInstance().getPlayerTeam(p).equals(TeamManager.getInstance().getPlayerTeam(near))) {
                        continue;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }



    public int warpSize(Player player) {
        int warpCount = 0;
//        if(player.hasPermission("hcsoups.warps.pro")) {
//            warpCount = 30;
//        } else if(player.hasPermission("hcsoups.warps.mvp")) {
//            warpCount = 20;
//        } else if(player.hasPermission("hcsoups.warps.vip")) {
//            warpCount = 10;
//        } else {
//            warpCount = 5;
//        }
//        if (player.hasPermission("hcsoups.warps.registered")) {
//            warpCount += 3;
//        }

        if(player.isOp() || player.hasPermission("warps.admin")) {
            warpCount = 100;
        } else {

            for (int i = 0; i < 100; i++) {
                if (player.hasPermission("warps." + i)) {
                    warpCount = i;
                }
            }
        }

        return warpCount;
    }

    private WarpManagerUUID() {
    }

    public static WarpManagerUUID getInstance() {
        return instance;
    }


    public Warp matchWarp(String player, String name) {
        UUID id = matchPlayer(player);

        if (id == null) {
            return null;
        }
        for(Warp warp : warps.get(id)) {
            if (name.equalsIgnoreCase(warp.getName())) {
                return warp;
            }
        }
     return null;
    }

    public UUID matchPlayer(String name) {
       UUID player = null;
        for (UUID id : warps.keySet()) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(id);
            if(p != null && p.getName().equalsIgnoreCase(name)) {
                   player = p.getUniqueId();
            }
        }
        return player;
    }

    @Command(name = "yes", aliases = "y")
    public void yes(CommandSender sender, String[] args) {
       if (overriding.containsKey(sender.getName())) {
           TempWarp warp = (TempWarp) ((Player) sender).getMetadata("warpToOverride").get(0).value();
           warp.getWarp().setLocation(warp.getLoc());
           warps.get(((Player) sender).getUniqueId()).set(warps.get(((Player) sender).getUniqueId()).indexOf(warp.getWarp()), warp.getWarp());
           overriding.get(sender.getName()).cancel();
           overriding.remove(sender.getName());

           ((Player) sender).removeMetadata("warpToOverride", Hardcore.getPlugin(Hardcore.class));

           sender.sendMessage(String.format("§aWarp '%s' has been overridden.", warp.getWarp().getName()));

       } else {
           sender.sendMessage("§cNo warp to overwrite!");
       }
    }

    @Command(name = "no", aliases = "n")
    public void no(CommandSender sender, String[] args) {
        if (overriding.containsKey(sender.getName())) {
            TempWarp warp = (TempWarp) ((Player) sender).getMetadata("warpToOverride").get(0).value();
            overriding.get(sender.getName()).cancel();
            overriding.remove(sender.getName());
            ((Player) sender).removeMetadata("warpToOverride", Hardcore.getPlugin(Hardcore.class));

            sender.sendMessage(String.format("§cNot overriding warp '%s'.", warp.getWarp().getName()));
        } else {
            sender.sendMessage("§cNo warp to overwrite!");
        }
    }

    public BasicDBObject serializeWarp(Warp warp) {
        BasicDBObject obj = new BasicDBObject();
        obj.put("name", warp.getName());
        obj.put("location", TeamManager.getInstance().locToString(warp.getLocation(), ','));
        return obj;
    }

    public Warp loadWarp(BasicDBObject object) {
        String name = (String) object.getString("name");
        Location location = TeamManager.getInstance().locFromString(object.getString("location"), ',');
        return new Warp(name, location);
    }


    public void savePlayer(UUID id) {
        List<Warp> warpsList = warps.get(id);
        if (warpsList.isEmpty()) {
            DBCursor cursor = warpc.find(new BasicDBObject("uuid", id.toString()));

            if(cursor.hasNext()) {
                BasicDBList li = new BasicDBList();

                BasicDBObject obj = new BasicDBObject()
                        .append("uuid", id.toString())
                        .append("warps", li);

                warpc.update(cursor.getQuery(), obj);

            } else {
                BasicDBObject obj = new BasicDBObject()
                        .append("uuid", id.toString())
                        .append("warps", new BasicDBList());
                warpc.insert(obj, WriteConcern.NORMAL);
            }
        } else {
            BasicDBList list = new BasicDBList();

            for(Warp w : warpsList) {
                list.add(serializeWarp(w));
            }
            DBCursor cursor = warpc.find(new BasicDBObject("uuid", id.toString()));
            if(cursor.hasNext()) {
                BasicDBObject obj = new BasicDBObject()
                        .append("uuid", id.toString())
                        .append("warps", list);
                warpc.update(cursor.getQuery(), obj);
            } else {
                BasicDBObject obj = new BasicDBObject()
                        .append("uuid", id.toString())
                        .append("warps", list);
                warpc.insert(obj, WriteConcern.NORMAL);
            }

        }

    }

    public void loadPlayer(BasicDBObject obj) {
        UUID id = UUID.fromString(obj.getString("uuid"));

        List<Warp> wlist = new ArrayList<>();

        BasicDBList list = (BasicDBList) obj.get("warps");

        if(list.isEmpty()) {
            warps.put(id, wlist);
        } else {
            for (Object ob : list) {
                BasicDBObject objs = (BasicDBObject)ob;
                Warp w = loadWarp(objs);
                wlist.add(w);
            }
            warps.put(id, wlist);
        }
    }
    public void saveWarps() {
        for (UUID name : warps.keySet()) {
            savePlayer(name);
        }
    }
    public void loadWarps() {
        DBCursor cursor = warpc.find();
        while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            loadPlayer(obj);
        }
    }

    @Data
    @AllArgsConstructor
    private class TempWarp {
        Warp warp;
        Location loc;
    }


    @EventHandler
    public void atck(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if(cantAttack.contains(((Player) event.getDamager()).getName())) {
                event.setCancelled(true);
            }
        }
    }


    public void setWarps(HashMap<UUID, List<Warp>> warps) {
        this.warps = warps;
    }

    public HashMap<UUID, List<Warp>> getWarps() {
        return warps;
    }
}
