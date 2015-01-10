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
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.combattag.CombatTagHandler;
import org.hcsoups.hardcore.command.Register;
import org.hcsoups.hardcore.entities.CustomEntityType;
import org.hcsoups.hardcore.listeners.DeathListener;
import org.hcsoups.hardcore.listeners.JoinListener;
import org.hcsoups.hardcore.mobcapture.MobCapture;
import org.hcsoups.hardcore.salvaging.Salvage;
import org.hcsoups.hardcore.scoreboard.ScoreboardHandler;
import org.hcsoups.hardcore.spawn.SpawnCommand;
import org.hcsoups.hardcore.spawn.SpawnManager;
import org.hcsoups.hardcore.stats.StatManager;
import org.hcsoups.hardcore.teams.BaseTeamCommand;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.teams.TeamSubCommand;
import org.hcsoups.hardcore.teams.commands.*;
import org.hcsoups.hardcore.teams.listeners.CapListener;
import org.hcsoups.hardcore.teams.listeners.ChatListener;
import org.hcsoups.hardcore.teams.listeners.FriendlyFireListener;
import org.hcsoups.hardcore.tracking.TrackingMethods;
import org.hcsoups.hardcore.utils.Lag;
import org.hcsoups.hardcore.utils.LagCommand;
import org.hcsoups.hardcore.warps.WarpAdminCommand;
import org.hcsoups.hardcore.warps.WarpCommand;
import org.hcsoups.hardcore.warps.WarpManager;
import org.hcsoups.hardcore.xpbottles.XPBottles;
import org.hcsoups.hardcore.zeus.annotations.Command;
import org.hcsoups.hardcore.zeus.registers.bukkit.BukkitRegistrar;

import java.io.File;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ryan on 11/20/2014
 * <p/>
 * Project: HCSoups
 */
public class Hardcore extends JavaPlugin {

    /**
     * TODO:
     * Add death hologram ✓
     * Improve scoreboards. ✓
     * Fix combat tag(Part of improving scoreboards) ✓
     * Fix tab completion for teams.
     * Add tab completing for warps.
     */

    public List<TeamSubCommand> tcommands = new LinkedList<TeamSubCommand>();

    BukkitRegistrar registrar;
    Register register;
    File teamsFolder = new File(getDataFolder() + File.separator + "teams" + File.separator);
    File warpsFolder = new File(getDataFolder() + File.separator + "warps" + File.separator);
    DB db;

    ScoreboardHandler handler;

    static TeamManager tm;


    @Override
    public void onEnable() {
        super.onEnable();
        registrar = new BukkitRegistrar();
        register = new Register();
        //      handler = new ScoreboardHandler();

        setupTeamCommands();
        try {
            db = MongoClient.connect(new DBAddress("localhost", "hardcore"));
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }

        tm = TeamManager.getInstance();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new FriendlyFireListener(), this);
        manager.registerEvents(new ChatListener(), this);
        manager.registerEvents(getInstance(), this);
        manager.registerEvents(new CombatTagHandler(), this);
        manager.registerEvents(new DeathListener(), this);
        manager.registerEvents(new JoinListener(), this);
        manager.registerEvents(new MobCapture(), this);
        manager.registerEvents(new XPBottles(), this);
        manager.registerEvents(new Salvage(), this);
        manager.registerEvents(new CapListener(), this);
        // manager.registerEvents(new MobLimiter(), this);
        manager.registerEvents(WarpManager.getInstance(), this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);

        registrar.registerAll(WarpManager.getInstance());
        registrar.registerAll(new SpawnCommand());
        registrar.registerAll(this);
        try {
            register.registerCommand("team", new BaseTeamCommand(this));
            register.registerCommand("warp", new WarpCommand());
            register.registerCommand("warpas", new WarpAdminCommand());
            register.registerCommand("lag", new LagCommand());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        StatManager.getInstance().loadStats();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        if (!teamsFolder.exists()) {
            teamsFolder.mkdir();
        }

        if (!warpsFolder.exists()) {
            warpsFolder.mkdir();
        }
        if (!getInstance().getInTeamFile().exists()) {
            try {
                getInstance().getInTeamFile().createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

      //  CustomEntityType.registerEntities();
        XPBottles.createRecipes();

        System.out.println("Loading teams into memory...");
        getInstance().loadTeams();
        System.out.println("Loading inTeams into memory...");
        try {
            getInstance().loadInTeam();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        System.out.println("Loading warps into memory...");
        WarpManager.getInstance().loadWarps();

        System.out.println("Loading spawn into memory...");
        SpawnManager.getInstance().loadSpawn();


        System.out.println("Connected to database!");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("Hardcore is ready...");

        new BukkitRunnable() {
            @Override
            public void run() {
                 TeamManager.getInstance().saveInTeam();
                 TeamManager.getInstance().saveTeams();
                 WarpManager.getInstance().saveWarps();
                 SpawnManager.getInstance().saveSpawn();

                 if(Bukkit.getPlayer("rbrick") != null) {
                     Player player = Bukkit.getPlayer("rbrick");
                     player.sendMessage("§a§m---------------------------------------");
                     player.sendMessage("          §c§lSaving warps & teams...");
                     player.sendMessage("          §c§lMay cause lag.");
                     player.sendMessage("§a§m---------------------------------------");
                 }
            }
        }.runTaskTimerAsynchronously(this, 20L, 36000);
//72000 = 30 minutes
        //36000
    }

    @Override
    public void onLoad() {

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
        getInstance().saveInTeam();
        getInstance().saveTeams();

        WarpManager.getInstance().saveWarps();

        SpawnManager.getInstance().saveSpawn();

        StatManager.getInstance().saveStats();

        for (File f : getInstance().getFilesToDelete()) {
            if (f.delete()) { //
                System.out.println("File deleted!");
                System.out.println("File deleted!");
                System.out.println("File deleted!");
            }
        }
     //   CustomEntityType.unregisterEntities();
        db.getMongo().close();
    }

    @Command(name = "track", usage = "§c/track [Player/All]", minArgs = 1)
    public void track(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return;
        } else {
            Player p = (Player) sender;

            if (p.getWorld().getEnvironment().equals(World.Environment.NETHER) || p.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
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


    public File getTeamsFolder() {
        return teamsFolder;
    }

    public File getWarpsFolder() {
        return warpsFolder;
    }

    public DB getMongo() {
        return db;
    }

    public static TeamManager getInstance() {
        return tm;
    }

    public ScoreboardHandler getHandler() {
        return handler;
    }
}
