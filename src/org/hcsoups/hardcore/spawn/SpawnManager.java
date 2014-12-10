package org.hcsoups.hardcore.spawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.teams.TeamManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ryan on 11/26/2014
 * <p/>
 * Project: HCSoups
 */
public class SpawnManager implements Listener {

    Spawn spawn;

    HashSet<String> haveSpawnprot = new HashSet<String>();

    File spawn_file = new File(Hardcore.getPlugin(Hardcore.class).getDataFolder(), "spawn.json");


    @EventHandler (ignoreCancelled = true)
    public void onEntityDamageEvent(EntityDamageByEntityEvent e) {
        // Only call these methods if the attacked is a player
        if (e.getEntity() instanceof Player) {
            Player attacked = (Player) e.getEntity();
            // Only fire if the damage is caused by a entity
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                if (e.getDamager() instanceof Player) {
                    Player attacker = (Player) e.getDamager();

                    if (haveSpawnprot.contains(attacked.getName()) && attacker.getWorld().getName().equals("world")) {
                        e.setCancelled(true);
                        attacker.sendMessage(ChatColor.RED + "This player has spawn protection!");
                        return;
                    }

                    if (haveSpawnprot.contains(attacker.getName()) && !haveSpawnprot.contains(attacked.getName()) && attacker.getWorld().getName().equals("world")) {
                        attacker.sendMessage(ChatColor.GRAY + "You have lost spawn protection!");
                        haveSpawnprot.remove(attacker.getName());
                        return;
                    }
                }
            }
            // Only fire if the damage is caused by a projectile
            if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                // Only fire if it was caused by a arrow
                if (e.getDamager() instanceof Projectile) {
                    Projectile a = (Projectile) e.getDamager();
                    if (a.getShooter() instanceof Player) {
                        Player attacker = (Player) a.getShooter();
                        if (haveSpawnprot.contains(attacked.getName()) && attacker.getWorld().getName().equals("world")) {
                            e.setCancelled(true);
                            attacker.sendMessage(ChatColor.RED + "This player has spawn protection!");
                            return;
                        }

                        if (haveSpawnprot.contains(attacker.getName()) && !haveSpawnprot.contains(attacked) && attacker.getWorld().getName().equals("world")) {
                            attacker.sendMessage(ChatColor.GRAY + "You have lost spawn protection!");
                            haveSpawnprot.remove(attacker.getName());
                            return;
                        }

                    }
                }
            }

        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
       if(spawn != null) {
           if (!spawn.contains(event.getPlayer().getLocation()) && haveSpawnprot.contains(event.getPlayer().getName())) {
               haveSpawnprot.remove(event.getPlayer().getName());
               event.getPlayer().sendMessage("&7You no longer have spawn protection!");
           }
       }
    }
    @EventHandler
    public void onSpawn(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) {
            return;
        }
       if(spawn != null) {
           event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0, spawn.getY(), 0));
           haveSpawnprot.add(event.getPlayer().getName());
           event.getPlayer().sendMessage("&7You have regained spawn protection!");
       }
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                return;
            }
            Player p = (Player) e.getEntity();
            if (p.getWorld().getName().equalsIgnoreCase("world")) {
                if (haveSpawnprot.contains(p.getName())) {
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (spawn.contains(event.getEntity().getLocation()) && haveSpawnprot.contains(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (spawn.contains(event.getBlock().getLocation())) {
             if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("hcsoups.spawn.build")) {
                 event.setCancelled(true);
             }
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (spawn.contains(event.getBlock().getLocation())) {
            if (!event.getPlayer().isOp() || !event.getPlayer().hasPermission("hcsoups.spawn.build")) {
                event.setCancelled(true);
            }
        }
    }


    // north -1 z
    // south +1 z
    // west -1 x
    // east +1 x
    @EventHandler
    public void onPistonPush(BlockPistonExtendEvent event) {
        List<Block> blockList = event.getBlocks();
        boolean cancel = false;
         BlockFace face = event.getDirection();
        for (Block block : blockList) {
            Location location = block.getLocation();
            if (face.equals(BlockFace.NORTH)) {
               location =  block.getLocation().subtract(0, 0, 1);
            } else if (face.equals(BlockFace.SOUTH)) {
               location = block.getLocation().add(0,0,1);
            } else if (face.equals(BlockFace.WEST)) {
                location = block.getLocation().subtract(1, 0 ,0);
            } else if (face.equals(BlockFace.EAST)) {
                location = block.getLocation().add(1, 0, 0);
            }

            if (spawn.contains(location)) {
                cancel = true;
            }
        }
        event.setCancelled(cancel);

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        boolean cancel = false;
        for (Block block : event.blockList()) {
             if(spawn.contains(block.getLocation())) {
                  cancel = true;
             }
        }
        event.setCancelled(cancel);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if(event.getPlayer().getBedSpawnLocation() != null) {
            return;
        }
        event.setRespawnLocation(new Location(Bukkit.getWorld("world"), 0, spawn.getY(), 0));
        haveSpawnprot.add(event.getPlayer().getName());
    }

    public void spawnTeleport(final Player p) {
        if (spawn == null) {
            p.sendMessage("&cSpawn has not been set up yet!");
            return;
        }
        if(TeamManager.getInstance().canTeleport(p)) {
            p.teleport(new Location(Bukkit.getWorld("world"), 0, spawn.getY(), 0));
            p.sendMessage("&7You have regained spawn protection!");
            haveSpawnprot.add(p.getName());
        } else {
            TeamManager.getInstance().getDontMove().put(p.getName(), new BukkitRunnable() {
                @Override
                public void run() {
                    p.teleport(new Location(Bukkit.getWorld("world"), 0, spawn.getY(), 0));
                    p.sendMessage("&7You have regained spawn protection!");
                    haveSpawnprot.add(p.getName());
                    TeamManager.getInstance().getDontMove().remove(p.getName());
                }
            });
            TeamManager.getInstance().getDontMove().get(p.getName()).runTaskLater(Hardcore.getPlugin(Hardcore.class), 10*20L);
            p.sendMessage("&7Someone is nearby! Warping in 10 seconds! Do not move!");
        }
    }

    public void saveSpawn() {
        if(spawn == null) {
            return;
        } else {
            JSONObject object = new JSONObject();
            object.put("radius", spawn.getRadius());
            object.put("ylevel", spawn.getY());

            JSONArray players = new JSONArray();

            for (String player : haveSpawnprot) {
                players.add(player);
            }

            object.put("haveSpawnprot", players);

            try {
                if (!spawn_file.exists()) {
                    spawn_file.createNewFile();
                }

                FileWriter writer = new FileWriter(spawn_file);

                object.writeJSONString(writer);
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    public void loadSpawn() {
        if (!spawn_file.exists()) {
            return;
        }
        JSONParser parser = new JSONParser();
        try {
            FileReader reader = new FileReader(spawn_file);
            JSONObject object = (JSONObject) parser.parse(reader);
            int radius = Integer.parseInt(String.valueOf(Long.valueOf((Long) object.get("radius"))));
            int ylevel = Integer.parseInt(String.valueOf(Long.valueOf((Long) object.get("ylevel"))));

            JSONArray array = (JSONArray) object.get("haveSpawnprot");
            for (Object nm : array) {
                String name = (String) nm;
                haveSpawnprot.add(name);
            }

            setSpawn(new Spawn(radius, ylevel));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }



    private SpawnManager() {}

    private static SpawnManager instance = new SpawnManager();

    public static SpawnManager getInstance() {
        return instance;
    }

    public void setSpawn(Spawn spawn) {
        this.spawn = spawn;
        Bukkit.getServer().getPluginManager().registerEvents(this, Hardcore.getPlugin(Hardcore.class));
    }

    public Spawn getSpawn() {
        return spawn;
    }

}
