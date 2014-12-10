package org.hcsoups.hardcore;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.MongoClient;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.hcsoups.hardcore.combattag.CombatTag;
import org.hcsoups.hardcore.command.Register;
import org.hcsoups.hardcore.spawn.SpawnCommand;
import org.hcsoups.hardcore.spawn.SpawnManager;
import org.hcsoups.hardcore.teams.BaseTeamCommand;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;
import org.hcsoups.hardcore.teams.commands.*;
import org.hcsoups.hardcore.teams.listeners.ChatListener;
import org.hcsoups.hardcore.teams.listeners.FriendlyFireListener;
import org.hcsoups.hardcore.tracking.TrackingMethods;
import org.hcsoups.hardcore.warps.WarpCommand;
import org.hcsoups.hardcore.warps.WarpManager;
import org.hcsoups.hardcore.zeus.annotations.Command;
import org.hcsoups.hardcore.zeus.registers.bukkit.BukkitRegistrar;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.xml.bind.DataBindingException;
import java.io.File;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Ryan on 11/20/2014
 * <p/>
 * Project: HCSoups
 */
public class Hardcore extends JavaPlugin {

    public List<TeamSubCommand> tcommands = new LinkedList<TeamSubCommand>();

    BukkitRegistrar registrar;
    Register register;
    File teamsFolder = new File(getDataFolder() + File.separator + "teams" + File.separator);
    File warpsFolder = new File(getDataFolder() + File.separator + "warps" + File.separator);
    Jedis jedis = new Jedis("localhost");
    DB db = null;


    @Override
    public void onEnable() {
        super.onEnable();
        registrar = new BukkitRegistrar();
        register = new Register();
        setupTeamCommands();
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new FriendlyFireListener(), this);
        manager.registerEvents(new ChatListener(), this);
        manager.registerEvents(TeamManager.getInstance(), this);
        manager.registerEvents(new CombatTag(), this);
        registrar.registerAll(WarpManager.getInstance());
        registrar.registerAll(new SpawnCommand());
        registrar.registerAll(this);
        try {
            register.registerCommand("team", new BaseTeamCommand(this));
            register.registerCommand("warp", register.constructFromAnnotation(new WarpCommand()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        if(!teamsFolder.exists()) {
            teamsFolder.mkdir();
        }

        if(!warpsFolder.exists()) {
            warpsFolder.mkdir();
        }
        if(!TeamManager.getInstance().getInTeamFile().exists()) {
            try {
                TeamManager.getInstance().getInTeamFile().createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Loading teams into memory...");
        TeamManager.getInstance().loadTeams();
        System.out.println("Loading inTeams into memory...");
        TeamManager.getInstance().loadInTeam();

        System.out.println("Loading warps into memory...");
        WarpManager.getInstance().loadWarps();

        System.out.println("Loading spawn into memory...");
        SpawnManager.getInstance().loadSpawn();

        try {
            jedis.connect();
        } catch (JedisConnectionException ex) {
            ex.printStackTrace();
        }

        try {
           db = MongoClient.connect(new DBAddress("localhost", "hardcore"));
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        System.out.println("Connected to database!");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("Hardcore is ready...");


    }


    void setupTeamCommands() {
        tcommands.add(new Create());
        tcommands.add(new Info());
        tcommands.add(new Join());
        tcommands.add(new Chat());
        tcommands.add(new SetPassword());
        tcommands.add(new Kick());
        tcommands.add(new SetFriendlyFire());
        tcommands.add(new Leave());
        tcommands.add(new Demote());
        tcommands.add(new Promote());
        tcommands.add(new Rally());
        tcommands.add(new SetRally());
        tcommands.add(new Hq());
        tcommands.add(new SetHq());
        tcommands.add(new Roster());
    }

    @Override
    public void onDisable() {
        // Just in case
        TeamManager.getInstance().saveInTeam();
        TeamManager.getInstance().saveTeams();

        WarpManager.getInstance().saveWarps();

        SpawnManager.getInstance().saveSpawn();
        jedis.save();
        jedis.disconnect();
        db.getMongo().close();
    }

    @Command(name="track", usage = "§c/track [Player/All]", minArgs = 1)
    public void track(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            return;
        } else {
            Player p = (Player) sender;

            if(p.getWorld().getEnvironment().equals(World.Environment.NETHER) || p.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                String env = p.getWorld().getEnvironment().equals(World.Environment.NETHER) ? "the nether." : "the end.";
                p.sendMessage("§cTracking is disabled in " + env);
                return;
            }

            TrackingMethods track = new TrackingMethods();
            if (args[0].equalsIgnoreCase("all")) {
                track.setLoc(p.getLocation().getBlockX(), p.getLocation().getBlockY() - 1, p.getLocation().getBlockZ());
                track.TrackAll(p, null);
            } else {
                Player tracked = Bukkit.getPlayer(args[0]);
                if (tracked == null) {
                    sender.sendMessage("§cCould not find \"" + args[0] + "\"");
                } else {
                    track.setLoc(p.getLocation().getBlockX(), p.getLocation().getBlockY() - 1, p.getLocation().getBlockZ());
                    track.Track(p, tracked);
                }
            }
        }
    }

    public List<TeamSubCommand> getTcommands() {
        return tcommands;
    }


    public Jedis getJedis() {
        return jedis;
    }

    public File getTeamsFolder() {
        return teamsFolder;
    }

    public File getWarpsFolder() {
        return warpsFolder;
    }

    public DB getMongo() {
        return db;
    }


}
