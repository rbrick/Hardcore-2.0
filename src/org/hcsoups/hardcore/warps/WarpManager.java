package org.hcsoups.hardcore.warps;

import lombok.AllArgsConstructor;
import lombok.Data;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.zeus.annotations.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ryan on 11/22/2014
 * <p/>
 * Project: HCSoups
 */
public class WarpManager {

    private HashMap<String, List<Warp>> warps = new HashMap<String, List<Warp>>();

    private HashMap<String, BukkitRunnable> overriding = new HashMap<String, BukkitRunnable>();

    static WarpManager instance = new WarpManager();


    public void listWarps(Player player) {
        if(!warps.containsKey(player.getName()) || warps.containsKey(player.getName()) && warps.get(player.getName()).isEmpty()) {
            player.sendMessage("&7***Warp List(0/" + warpSize(player) + ")***\n[]");
            return;
        }
        player.sendMessage("&7***Warp List(" + warps.get(player.getName()).size() + "/" + warpSize(player) + ")***");

        StringBuilder builder = new StringBuilder();

        FancyMessage message = new FancyMessage("[").color(ChatColor.GRAY).then();

        List<Warp> pwarps = warps.get(player.getName());


        for (int i = 0; i < pwarps.size(); i++) {
            if(i < pwarps.size()-1) {
                message.text(pwarps.get(i).getName()).color(ChatColor.GRAY)
                        .tooltip("&aClick here to warp to '&f" + pwarps.get(i).getName() + "&a'.")
                        .command("/warp " + pwarps.get(i).getName())
                        .then(", ").color(ChatColor.GRAY).then();
            } else {
                message.text(pwarps.get(i).getName()).color(ChatColor.GRAY)
                        .tooltip("&aClick here to warp to '&f" + pwarps.get(i).getName() + "&a'.")
                        .command("/warp " + pwarps.get(i).getName()).then();
            }
        }

        message.text("]").color(ChatColor.GRAY);
        message.send(player);
    }


    public void listWarpsAdmin(Player player, String name) {
         if (matchPlayer(name) == null) {
             player.sendMessage("&cCould not find player '" + name + "'.");
         } else {
             String playerS = matchPlayer(name);
             if(!warps.containsKey(playerS) || warps.containsKey(playerS) && warps.get(playerS).isEmpty()) {
                 player.sendMessage(String.format("&7Showing warps for %s: \n[]", playerS));
             } else {
                 StringBuilder builder = new StringBuilder();

                 for (Warp warp : warps.get(player.getName())) {
                     builder.append(warp.getName()).append(", ");
                 }

                 String warpList = builder.toString().trim();

                 if (warpList.endsWith(",")) {
                     warpList = warpList.substring(0, warpList.length() - 1);
                 }

                 player.sendMessage(String.format("&7Showing warps for %s: \n[%s]", playerS, warpList));
             }
         }
    }


    public void setWarp(final Player player, String name) {

        if (!name.matches("^[A-Za-z0-9_]*$")) {
            player.sendMessage("&cInvalid warp name!");
            return;
        }

        if (matchWarp(player.getName(), name) != null) {
            TempWarp twarp = new TempWarp(matchWarp(player.getName(), name), player.getLocation());
            player.sendMessage(String.format("&eYou already have a warp with the name '&a%s&e'\n&eIf you would like to overwrite the warp type &2/yes\n&eor type &c/no &eto cancel the request.\n&eThis will expire in 10 seconds.", twarp.getWarp().getName()));
            player.setMetadata("warpToOverride", new FixedMetadataValue(Hardcore.getPlugin(Hardcore.class), twarp));
            overriding.put(player.getName(), new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage("&cDid not receive an answer in time! Canceling request.");
                }
            });

            overriding.get(player.getName()).runTaskLater(Hardcore.getPlugin(Hardcore.class), 200L);
        } else {
            if (warps.get(player.getName()) != null && warps.get(player.getName()).size() >= warpSize(player)) {
                player.sendMessage("&cYou have set the max amount of warps!");
                return;
            }
            List<Warp> pwarps = warps.get(player.getName());

            if(pwarps == null) {
                pwarps = new ArrayList<Warp>();
            }
            Warp warp = new Warp(name, player.getLocation());
            pwarps.add(warp);
            warps.put(player.getName(), pwarps);
            player.sendMessage(String.format("&7Warp '%s' has been set!", warp.getName()));
        }
    }

    public void delWarp(Player player, String name) {
          if (matchWarp(player.getName(), name) == null || !warps.containsKey(player.getName()) || warps.containsKey(player.getName()) && warps.get(player.getName()).isEmpty()) {
              player.sendMessage(String.format("&cWarp '%s' does not exist!", name));
          } else {
            List<Warp> warpsp = warps.get(player.getName());
            Warp warp2rem = matchWarp(player.getName(), name);
            warpsp.remove(warp2rem);
            warps.put(player.getName(), warpsp);
            player.sendMessage(String.format("&7Warp '%s' has been deleted.", warp2rem.getName()));
          }
    }


    public void warp(Player player, String warp) {
        if (matchWarp(player.getName(), warp) == null || !warps.containsKey(player.getName()) || warps.containsKey(player.getName()) && warps.get(player.getName()).isEmpty()) {
            player.sendMessage(String.format("&cWarp '%s' does not exist!", warp));
            return;
        }
        warpTeleport(player, matchWarp(player.getName(), warp));
    }


    public void warpTeleport(final Player p, final Warp warp) {
            if(canTeleport(p)) {
                p.teleport(warp.getLocation());
                p.sendMessage(String.format("&7Warped to %s!", warp.getName()));
            } else {
                TeamManager.getInstance().getDontMove().put(p.getName(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.teleport(warp.getLocation());
                        p.sendMessage(String.format("&7Warped to %s!", warp.getName()));
                        TeamManager.getInstance().getDontMove().remove(p.getName());
                    }
                });
                TeamManager.getInstance().getDontMove().get(p.getName()).runTaskLater(Hardcore.getPlugin(Hardcore.class), 10*20L);
                p.sendMessage("&7Someone is nearby! Warping in 10 seconds! Do not move!");
            }
    }

    public boolean canTeleport(Player p) {
        for(Entity ent : p.getNearbyEntities(40, 20, 40)) {
            if(ent instanceof Player) {

                Player near = (Player) ent;
                if(near.equals(p)) continue;

                if(TeamManager.getInstance().getPlayerTeam(near) != null) {
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
        int warpCount;
        if(player.hasPermission("hcsoups.warps.pro")) {
            warpCount = 30;
        } else if(player.hasPermission("hcsoups.warps.mvp")) {
            warpCount = 20;
        } else if(player.hasPermission("hcsoups.warps.vip")) {
            warpCount = 10;
        } else {
            warpCount = 5;
        }
        if (player.hasPermission("hcsoups.warps.registered")) {
            warpCount += 3;
        }
        return warpCount;
    }

    private WarpManager() {
    }

    public static WarpManager getInstance() {
        return instance;
    }


    public Warp matchWarp(String player, String name) {
        player = matchPlayer(player);

        if (player == null) {
            return null;
        }
        for(Warp warp : warps.get(player)) {
            if (name.equalsIgnoreCase(warp.getName())) {
                return warp;
            }
        }
     return null;
    }

    public String matchPlayer(String name) {
       String player = null;
        for (String players : warps.keySet()) {
            if (players.equalsIgnoreCase(name)) {
                player = players;
            }
        }
        return player;
    }

    @Command(name = "yes", aliases = "y")
    public void yes(CommandSender sender, String[] args) {
       if (overriding.containsKey(sender.getName())) {
           TempWarp warp = (TempWarp) ((Player) sender).getMetadata("warpToOverride").get(0).value();
           warp.getWarp().setLocation(warp.getLoc());
           warps.get(sender.getName()).set(warps.get(sender.getName()).indexOf(warp.getWarp()), warp.getWarp());
           overriding.get(sender.getName()).cancel();
           overriding.remove(sender.getName());

           ((Player) sender).removeMetadata("warpToOverride", Hardcore.getPlugin(Hardcore.class));

           sender.sendMessage(String.format("&aWarp '%s' has been overridden.", warp.getWarp().getName()));

       } else {
           sender.sendMessage("&cNo warp to overwrite!");
       }
    }

    @Command(name = "no", aliases = "n")
    public void no(CommandSender sender, String[] args) {
        if (overriding.containsKey(sender.getName())) {
            TempWarp warp = (TempWarp) ((Player) sender).getMetadata("warpToOverride").get(0).value();
            overriding.get(sender.getName()).cancel();
            overriding.remove(sender.getName());
            ((Player) sender).removeMetadata("warpToOverride", Hardcore.getPlugin(Hardcore.class));

            sender.sendMessage(String.format("&cNot overriding warp '%s'.", warp.getWarp().getName()));
        } else {
            sender.sendMessage("&cNo warp to overwrite!");
        }
    }

    public JSONObject serializeWarp(Warp warp) {
        JSONObject warpobject = new JSONObject();
        warpobject.put("name", warp.getName());
        warpobject.put("location", TeamManager.getInstance().locToString(warp.getLocation(), ','));
        return warpobject;
    }

    public Warp loadWarp(JSONObject object) {
        String name = (String) object.get("name");
        Location location = TeamManager.getInstance().locFromString((String) object.get("location"), ',');
        return new Warp(name, location);
    }


    public void savePlayer(String name) {
        List<Warp> warpsList = warps.get(name);
        if (warpsList.isEmpty()) {
            return;
        }

        File nfile = new File(Hardcore.getPlugin(Hardcore.class).getWarpsFolder(), name + ".json");

        if (nfile.exists()) {
            try {
                nfile.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JSONArray warpsa = new JSONArray();

        for (Warp warp : warpsList) {
            warpsa.add(serializeWarp(warp));
        }

       try {
           FileWriter writer = new FileWriter(nfile);
           warpsa.writeJSONString(writer);
           writer.flush();
           writer.close();
       } catch (Exception ex) {
           ex.printStackTrace();
       }
    }

    public void loadPlayer(File file) {
        JSONParser parser = new JSONParser();
        String name = file.getName().split("\\.")[0];
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(file));

            List<Warp> warpList = new ArrayList<Warp>();

            for (Object object : jsonArray) {
                   JSONObject obj = (JSONObject) object;
                   warpList.add(loadWarp(obj));
            }
            warps.put(name, warpList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void saveWarps() {
        for (String name : warps.keySet()) {
            savePlayer(name);
        }
    }
    public void loadWarps() {
        File[] warpFiles = Hardcore.getPlugin(Hardcore.class).getWarpsFolder().listFiles();
        for (File warp : warpFiles) {
            loadPlayer(warp);
        }
    }

    @Data
    @AllArgsConstructor
    private class TempWarp {
        Warp warp;
        Location loc;
    }

}
