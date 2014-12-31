package org.hcsoups.hardcore.teams;

import com.mongodb.*;
import lombok.AccessLevel;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

public class TeamManager implements Listener {

    // The teams the players are in.
    HashMap<String, Team> inTeam = new HashMap<String, Team>();

    // The teams that exist
    ArrayList<Team> teams = new ArrayList<Team>();

    ArrayList<String> teamChat = new ArrayList<String>();

    File inTeamFile = new File(Hardcore.getPlugin(Hardcore.class).getDataFolder(), "inteam.json");

    Pattern isJson = Pattern.compile("([a-zA-Z0-9_]\\.json)");

    Pattern isValid = Pattern.compile("^[A-Za-z0-9_]*$");


    HashMap<String, BukkitRunnable> dontMove = new HashMap<String, BukkitRunnable>();

    HashMap<String, Long> onCooldown = new HashMap<String, Long>();

   @Getter(AccessLevel.PUBLIC)
   HashSet<File> filesToDelete = new HashSet<>();

//    Jedis db = Hardcore.getPlugin(Hardcore.class).getJedis();

    DBCollection collection;
    DBCollection players;

    long interval = 600000;

    protected TeamManager() {
        collection = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("teams");
        players = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("players");
    }

    
    private static TeamManager instance = new TeamManager();

    public static TeamManager getInstance() {
        return instance;
    }
    
    
    public Team createTeam(Player player, String name, String password) {
        if (!name.matches(isValid.pattern())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§cInvalid name!"));
            return null;
        }

        if (doesTeamExist(name)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§cThe team '" + name + "' already exists!"));
            return null;
        }

        if (isOnTeam(player.getName())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§cYou are already on a team!"));
            return null;
        }

        ArrayList<String> mans = new ArrayList<String>();

        mans.add(player.getName());

        Team team1 = new Team(name, mans, new ArrayList<String>(), password);
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§7Team created!\n§7To learn more about teams, do /team"));
        inTeam.put(player.getName(), team1);
        saveInTeam();
        teams.add(team1);
        return team1;
    }


    public Team createTeam(Player player, String name) {
        return createTeam(player, name, "");
    }


    public boolean joinTeam(String name, String password, Player player) {
        Team team = matchTeam(name);

        if (team == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§cCould not find team '" + name + "'."));
            return false;
        } else {
            if (!team.getPassword().isEmpty()) {
                if (password.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§cTeam requires a password!"));
                    return false;
                } else {
                    if (password.equals(team.getPassword())) {
                        messageTeam(team, "§7" + player.getName() + " has joined the team!");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§7You have successfully joined the team!"));
                        team.getMembers().add(player.getName());
                        inTeam.put(player.getName(), team);
                        //      saveInTeam();
                        //      saveTeam(team);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§cWrong password!"));
                        return false;
                    }
                }
            } else {
                messageTeam(team, "§7" + player.getName() + " has joined the team!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('§',"§7You have successfully joined the team!"));
                team.getMembers().add(player.getName());
                inTeam.put(player.getName(), team);
                //   saveInTeam();
                //     saveTeam(team);
                return true;
            }
        }
    }



    public void messageTeam(Team team, String message) {
        message = ChatColor.translateAlternateColorCodes('§', message);
        for (String member : team.getMembers()) {
            Player p = Bukkit.getPlayerExact(member);
            if (p == null) {
                continue;
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('§', message));
            }
        }

        for (String member : team.getManagers()) {
            Player p = Bukkit.getPlayerExact(member);
            if (p == null) {
                continue;
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('§', message));
            }
        }
    }

    public Team getPlayerTeam(Player player) {
        // if for some reason our test in the command doesn't pass have this for back up! :)
        Validate.notNull(player, "Player cannot be null!");
        // Theoretically the player will be with in the hashmap...
        return inTeam.get(player.getName());

    }


    public Team getPlayerTeam(String player) {
        Validate.notNull(player, "Player cannot be null!");

        if (inTeam.get(player) == null) {
            // if it is null, either the player is not in the list(offline/error occured) or they are not on a team
            // More checking is required but i am tired and want to get this out quickly.
            return null;
        }
        return inTeam.get(player);
    }

    // used for /t i <playername>
    public void sendInfo(Player player, String name) {
        List<String> options = matchPlayer(name);

        if(options.size() <= 0) {
            // See if the player is
            if(getPlayerTeam(name) != null) {
                Team team = getPlayerTeam(name);

                if (team == null) { // always false
                    player.sendMessage("§cPlayer '" + name + "' is not on a team.");
                    return;
                }

                if (getPlayerTeam(player) != null && getPlayerTeam(player).equals(team)) {
                    sendInfo(player);
                    return;
                } else {
                    player.sendMessage("§7***§3" + team.getName() + "§7***");

                    if (team.getManagers().size() >= 1) {
                        for (String man : team.getManagers()) {
                            Player manp = Bukkit.getPlayer(man);
                            if (manp != null) {
                                player.sendMessage(" §3" + man + " §7- " + formatHealth((manp.getHealth())));
                            } else {
                                player.sendMessage(" §3" + man + " §7- Offline");
                            }
                        }
                    }
                    if (team.getMembers().size() >= 1) {
                        for (String mem : team.getMembers()) {
                            Player memp = Bukkit.getPlayer(mem);
                            if (memp != null) {
                                player.sendMessage(" §7" + mem + " - " + formatHealth((memp.getHealth())));
                            } else {
                                player.sendMessage(" §7" + mem + " - Offline");
                            }
                        }
                    }
                    return;
                }
            } else {
                player.sendMessage("§cCould not find player '" + name + "'.");
                return;

            }
         }

        if (options.size() > 1) {
            FancyMessage message = new FancyMessage("Did you mean: ").color(ChatColor.DARK_AQUA).then();
            for (int i = 0; i < options.size(); i++) {
                if(i < options.size()-1) {
                    message.text(options.get(i)).color(ChatColor.GRAY)
                            .tooltip("§aClick here to view '§f" + options.get(i) + " §a' team.")
                            .command("/team info " + options.get(i))
                            .then(", ").color(ChatColor.GRAY).then();
                } else {
                    message.text(options.get(i)).color(ChatColor.GRAY)
                            .tooltip("§aClick here to view '§f" + options.get(i) + " §a' team.")
                            .command("/team info " + options.get(i));
                }
            }
            message.send(player);
            return;
        }



        Team team = getPlayerTeam(options.get(0));

        if (team == null) {
            player.sendMessage("§cPlayer '" + name + "' is not on a team.");
            return;
        }

        if (getPlayerTeam(player) != null && getPlayerTeam(player).equals(team)) {
            sendInfo(player);
            return;
        } else {
            player.sendMessage("§7***§3" + team.getName() + "§7***");

            if (team.getManagers().size() >= 1) {
                for (String man : team.getManagers()) {
                    Player manp = Bukkit.getPlayer(man);
                    if (manp != null) {
                        player.sendMessage(" §3" + man + " §7- " + formatHealth((manp.getHealth())));
                    } else {
                        player.sendMessage(" §3" + man + " §7- Offline");
                    }
                }
            }
            if (team.getMembers().size() >= 1) {
                for (String mem : team.getMembers()) {
                    Player memp = Bukkit.getPlayer(mem);
                    if (memp != null) {
                        player.sendMessage(" §7" + mem + " - " + formatHealth((memp.getHealth())));
                    } else {
                        player.sendMessage(" §7" + mem + " - Offline");
                    }
                }
            }
            return;
        }

    }

    // For commands such as /t r <Name>
    public void sendInfo(Player player, Team team) {
        if (team == null) {
            player.sendMessage("§cTeam does not exist!");
        }
        if (getPlayerTeam(player) != null && getPlayerTeam(player).equals(team)) {
            sendInfo(player);
            return;
        } else {
            player.sendMessage("§7***§3" + team.getName() + "§7***");

            if (team.getManagers().size() >= 1) {
                for (String man : team.getManagers()) {
                    Player manp = Bukkit.getPlayer(man);
                    if (manp != null) {
                        player.sendMessage(" §3" + man + " §7- " + formatHealth((manp.getHealth())));
                    } else {
                        player.sendMessage(" §3" + man + " §7- Offline");
                    }
                }
            }
            if (team.getMembers().size() >= 1) {
                for (String mem : team.getMembers()) {
                    Player memp = Bukkit.getPlayer(mem);
                    if (memp != null) {
                        player.sendMessage(" §7" + mem + " - " + formatHealth((memp.getHealth())));
                    } else {
                        player.sendMessage(" §7" + mem + " - Offline");
                    }
                }
            }
        }

    }

    public void sendInfo(Player player) {
        if (inTeam.get(player.getName()) == null) {
            player.sendMessage("§cYou are not on any team!");
            return;
        }
        Team team = inTeam.get(player.getName());

        player.sendMessage("§7***§3" + team.getName() + "§7***");
        player.sendMessage("§7Password: " + (team.getPassword().isEmpty() || team.getPassword().equals("") ? "§cNot Set" : "§a" + team.getPassword()));
        player.sendMessage("§7Team HQ: " + (team.getHq() == null ? "§cNot Set" : "§aSet"));
        player.sendMessage("§7Team Rally: " + (team.getRally() == null ? "§cNot Set" : "§aSet"));
        player.sendMessage("§7Friendly Fire is " + (team.isFriendlyFire() ? "§con" : "§aoff"));
        player.sendMessage("§7Members: ");

        if (team.getManagers().size() >= 1) {
            for (String man : team.getManagers()) {
                Player manp = Bukkit.getPlayer(man);
                if (manp != null) {
                    player.sendMessage(" §3" + man + " §7- " + formatHealth((manp.getHealth())));
                } else {
                    player.sendMessage(" §3" + man + " §7- Offline");
                }
            }
        }
        if (team.getMembers().size() >= 1) {
            for (String mem : team.getMembers()) {
                Player memp = Bukkit.getPlayer(mem);
                if (memp != null) {
                    player.sendMessage(" §7" + mem + " - " + formatHealth((memp.getHealth())));
                } else {
                    player.sendMessage(" §7" + mem + " - Offline");
                }
            }
        }
    }



    String formatHealth(double health) {
        double hearts = health/2;
        DecimalFormat format = new DecimalFormat("#.#");

        if(hearts <= 10 && hearts >= 8) {
             return String.format("§a%s ❤", format.format(hearts));
        } else if (hearts <= 7 && hearts >= 4) {
            return String.format("§e%s ❤", format.format(hearts));
        } else {
            return String.format("§c%s ❤", format.format(hearts));
        }
    }

    public boolean doesTeamExist(String name) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Team matchTeam(String name) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }


    public void loadTeams() {
        File[] teamsFiles = Hardcore.getPlugin(Hardcore.class).getTeamsFolder().listFiles();
        try {
            for (File file : teamsFiles) {
                loadTeam(file);
                System.gc();
                file.delete();
            }
        } catch (Exception ex) {
            return;
        }


    }


    // Fills the inTeam Map. Called after loadTeams()
    public void loadInTeam() {
        if (!inTeamFile.exists()) {
            return;
        }
        JSONParser parser = new JSONParser();

        try {
            JSONObject object = (JSONObject) parser.parse(new FileReader(inTeamFile));

            JSONArray array = (JSONArray) object.get("inTeam");

            for (Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;

                String name = (String) jsonObject.get("name");
                Team team = matchTeam((String) jsonObject.get("team"));
                inTeam.put(name, team);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveInTeam() {
        JSONObject object = new JSONObject();

        JSONArray array = new JSONArray();

        for (Map.Entry<String, Team> entry : inTeam.entrySet()) {
            JSONObject object1 = new JSONObject();
            object1.put("name", entry.getKey());
            object1.put("team", entry.getValue().getName());
            array.add(object1);
        }

        object.put("inTeam", array);

        try {
            FileWriter writer = new FileWriter(inTeamFile);

            object.writeJSONString(writer);

            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Loads a team into memory from a file.
     * <p/>
     * This uses the SimpleJSON Library.
     *
     * @param file
     */
    public void loadTeam(File file) {
        if (!file.getName().endsWith(".json")) {
            throw new UnsupportedOperationException("Unsupported file extension!");
        }
        try {
            FileReader fileReader = new FileReader(file);

            JSONParser parser = new JSONParser();

            // Loads
            org.json.simple.JSONObject object = (org.json.simple.JSONObject) parser.parse(fileReader);

            String name = (String) object.get("name");

            String password;

            if (object.get("password") != null) {
                password = (String) object.get("password");
            } else {
                password = "";
            }

            boolean friendlyFire = (Boolean) object.get("friendlyFire");


            org.json.simple.JSONArray members = (org.json.simple.JSONArray) object.get("members");

            ArrayList<String> memberss = new ArrayList<String>();

            for (Object obj : members) {
                String pnames = (String) obj;
                memberss.add(pnames);

            }

            org.json.simple.JSONArray managerss = (org.json.simple.JSONArray) object.get("managers");

            ArrayList<String> managers = new ArrayList<String>();

            for (Object obj : managerss) {
                managers.add((String) obj);
            }

            Location hq;
            if (object.get("hq") != null) {
                hq = locFromString((String) object.get("hq"), ',');
            } else {
                hq = null;
            }

            Location rally;
            if (object.get("rally") != null) {
                rally = locFromString((String) object.get("rally"), ',');
            } else {
                rally = null;
            }


            Team team = new Team(name, managers, memberss, password);
            team.setFriendlyFire(friendlyFire);
            team.setRally(rally);
            team.setHq(hq);


            teams.add(team);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void saveTeam(Team team) {
        File file = new File(Hardcore.getPlugin(Hardcore.class).getTeamsFolder(), team.getName() + ".json");


        if (file.exists()) {
            // Prevents lag.
            if(filesToDelete.contains(file)) {
                return;
            }

            try {
                JSONObject object = new JSONObject();
                JSONArray members = new JSONArray();
                JSONArray managers = new JSONArray();

                object.put("name", team.getName());
                if (!team.getPassword().isEmpty()) {
                    object.put("password", team.getPassword());
                }
                object.put("friendlyFire", team.isFriendlyFire());


                // puts a empty array 'members:[]'
                if (team.getMembers().isEmpty()) {
                    object.put("members", members);
                } else {
                    for (String member : team.getMembers()) {
                        members.add(member);
                    }

                    // When done add the list
                    object.put("members", members);
                }

                if (team.getManagers().isEmpty()) {
                    object.put("managers", managers);
                } else {
                    for (String manager : team.getManagers()) {
                        managers.add(manager);
                    }

                    object.put("managers", managers);
                }

                if (team.getHq() != null) {
                    object.put("hq", locToString(team.getHq(), ','));
                }

                if (team.getRally() != null) {
                    object.put("rally", locToString(team.getRally(), ','));
                }

                FileWriter fw = new FileWriter(file);
                object.writeJSONString(fw);
                fw.flush();
                fw.close();


            } catch (Exception ex) {
                ex.printStackTrace();
            }


        } else {
            try {
                /// i mean y0l0 right?
                file.createNewFile();

                JSONObject object = new JSONObject();
                JSONArray members = new JSONArray();
                JSONArray managers = new JSONArray();

                object.put("name", team.getName());
                if (!team.getPassword().isEmpty()) {
                    object.put("password", team.getPassword());
                }
                object.put("friendlyFire", team.isFriendlyFire());


                // puts a empty array 'members:[]'
                if (team.getMembers().isEmpty()) {
                    object.put("members", members);
                } else {
                    for (String member : team.getMembers()) {
                        members.add(member);
                    }

                    // When done add the list
                    object.put("members", members);
                }

                if (team.getManagers().isEmpty()) {
                    object.put("managers", managers);
                } else {
                    for (String manager : team.getManagers()) {
                        managers.add(manager);
                    }

                    object.put("managers", managers);
                }

                if (team.getHq() != null) {
                    object.put("hq", locToString(team.getHq(), ','));
                }

                if (team.getRally() != null) {
                    object.put("rally", locToString(team.getRally(), ','));
                }


                FileWriter writer = new FileWriter(file);

                object.writeJSONString(writer);
                writer.flush();
                writer.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    public void leaveTeam(final Player player) {
        if (inTeam.get(player.getName()) == null) {
            player.sendMessage("§cYou are not in a team!");
        } else {
            final Team team = inTeam.get(player.getName());
            team.getMembers().remove(player.getName());
            team.getManagers().remove(player.getName());
            if (team.getManagers().size() <= 0 && team.getMembers().size() <= 0) {
                disbandTeam(team);
                removePlayer(player.getName());
                inTeam.remove(player.getName());
                teamChat.remove(player.getName());
                player.sendMessage("§3Successfully left and disbanded team!");
                return;
            }
            messageTeam(team, "§3" + player.getName() + " has left the team!");
            player.sendMessage("§3You have left the team!");
            removePlayer(player.getName());
            //    saveTeam(team);
            inTeam.remove(player.getName());
            //   saveInTeam();
            teamChat.remove(player.getName());

            updateTeam(team, TeamAction.UPDATE);
        }
    }

    // Turns a location into a string(via magic ofc)
    public String locToString(Location loc, char delim) {
        // f**k googles preconditions
        Validate.notNull(loc, "Location cannot be null!");
        StringBuilder builder = new StringBuilder();
        builder.append(loc.getWorld().getName())
                .append(delim)
                .append(loc.getX())
                .append(delim)
                .append(loc.getY())
                .append(delim)
                .append(loc.getZ())
                .append(delim)
                .append(loc.getYaw())
                .append(delim)
                .append(loc.getPitch());
        return builder.toString();
    }

    public Location locFromString(String serialized, char delim) {
        Validate.notNull(serialized, "Serialized location string cannot be null!");
        Validate.notNull(delim, "Character delimeter cannot be null!");
        // escape just incase its needed.
        String[] splitedLoc = serialized.split("" + delim);
        // cannot be null. that would be bad now wouldn't it
        @Nonnull World world = Bukkit.getWorld(splitedLoc[0]);
        double x = Double.valueOf(splitedLoc[1]);
        double y = Double.valueOf(splitedLoc[2]);
        double z = Double.valueOf(splitedLoc[3]);
        float yaw = Float.valueOf(splitedLoc[4]);
        float pitch = Float.valueOf(splitedLoc[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void saveTeams() {
        for (Team team : teams) {
            saveTeam(team);
        }
    }

    public boolean isManager(Player player) {
        return getPlayerTeam(player) != null && getPlayerTeam(player).getManagers().contains(player.getName());
    }

    public String formatLocation(Location location) {
        DecimalFormat format = new DecimalFormat("#.##");
        return "X: " + format.format(location.getX()) + ", Y: " + format.format(location.getY()) + ", Z: " + format.format(location.getZ());
    }

    public boolean isOnTeam(String player) {
        return inTeam.get(player) != null;
    }

    public File getInTeamFile() {
        return inTeamFile;
    }

    public HashMap<String, Team> getInTeam() {
        return inTeam;
    }

    public ArrayList<String> getTeamChat() {
        return teamChat;
    }

    // Used when the team has no more players left.
    public void disbandTeam(Team team) {
        File teamFile = new File(Hardcore.getPlugin(Hardcore.class).getTeamsFolder(), team.getName() + ".json");
        updateTeam(team, TeamAction.REMOVE);
        teams.remove(team);
//        saveTeams();
  //      System.gc();
        if (teamFile.exists()) {
//            if(teamFile.delete()) {
//                System.out.println("Deleted file!");
//            }
             filesToDelete.add(teamFile);
        } else {
            System.out.println("No team file found. Not deleting.");
        }

    }

    public void teamTeleport(final Player p, final String locName, final Location loc) {
        if (getPlayerTeam(p) == null) {
            p.sendMessage("§cYou are not on a team!");
            return;
        } else if (loc == null) {
            p.sendMessage("§cTeam " + locName + " is not set!");
            return;
        } else {
            if (canTeleport(p)) {
                p.teleport(loc);
                p.sendMessage(String.format("§7Warped to your team's %s!", locName));


            } else {

                if(dontMove.containsKey(p.getName())) {
                    dontMove.get(p.getName()).cancel();
                    System.out.println("Cancelling timer for " + p.getName());

                }
                dontMove.put(p.getName(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.teleport(loc);
                        dontMove.remove(p.getName());
                        p.sendMessage(String.format("§7Warped to your team's %s!", locName));
                    }
                });
                dontMove.get(p.getName()).runTaskLater(Hardcore.getPlugin(Hardcore.class), 10 * 20L);
                p.sendMessage("§7Someone is nearby! Warping in 10 seconds! Do not move!");
            }
        }

    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (dontMove.containsKey(e.getPlayer().getName())) {
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                dontMove.get(e.getPlayer().getName()).cancel();
                dontMove.remove(e.getPlayer().getName());
                e.getPlayer().sendMessage("§cYou moved! Warping cancelled!");
            }
        }
    }


    /**
     * For home.
     *
     * @param p
     * @return
     */
    public boolean canTeleport(Player p) {
        boolean canTeleport = true;
        for (Entity ent : p.getNearbyEntities(40, 20, 40)) {
            if (ent instanceof Player) {
                Player near = (Player) ent;
                if (near.equals(p)) {
                    continue;
                }
                if (getPlayerTeam(near) != null && getPlayerTeam(p) != null) {
                    if (getPlayerTeam(p).equals(getPlayerTeam(near))) {
                        continue;
                    } else {
                        canTeleport = false;
                    }
                } else {
                    canTeleport = false;
                }
            }
        }
        return canTeleport;
    }

    public String formatTime(long time) {

        long second = 1000;

        long minute = 60 * 1000;

        long minutes = time / minute;

        long seconds = (time - (minutes * minute)) / second;

        if (seconds < 10) {
            String newSeconds = "0" + seconds;
            return minutes + "m" + newSeconds + "s";
        }

        return minutes + "m" + seconds + "s";
    }

    public void updatePlayer(Player player, Team team) {
        updatePlayer(player.getName(), team);
    }

    public void updatePlayer(String player, Team team) {
          DBCursor cursor = players.find(new BasicDBObject(player, team.getName()));
          if(cursor.hasNext()) {
              DBObject object = cursor.next();
              object.put(player, team.getName());
              players.update(cursor.getQuery(), object);
          } else {
              BasicDBObject object = new BasicDBObject();
              object.put(player, team.getName());
              players.insert(object);
          }

//        db.hset("team_players", player, team.getName());
//        db.save();
    }

    public void removePlayer(String player) {
         Team team = getPlayerTeam(player);
        if(team == null) {
            System.out.println(player + " was not found in the database!");
        } else {
            DBCursor cursor = players.find(new BasicDBObject(player, team.getName()));
            boolean next = cursor.hasNext();
            System.out.println(next);
            if (cursor.hasNext()) {
                players.remove(cursor.next());
            } else {
                System.out.println(player + " was not found in the database!");
            }

        }

    }

    public void updateTeam(Team team, TeamAction action) {
        if (action.equals(TeamAction.UPDATE)) {
            DBCursor cursor = collection.find(new BasicDBObject("name", team.getName()));
            if (cursor.hasNext()) {
                DBObject teamObject = cursor.next();
                BasicDBList membersList = new BasicDBList();
                BasicDBList managersList = new BasicDBList();
                if (!team.getMembers().isEmpty()) {
                    for (String members : team.getMembers()) {
                        membersList.add(members);
                    }
                }
                if(!team.getManagers().isEmpty()) {
                    for (String managers : team.getManagers()) {
                        managersList.add(managers);
                    }
                }
                teamObject.put("members", membersList);
                teamObject.put("managers", managersList);
                cursor.close();
                collection.update(cursor.getQuery(),teamObject);
            } else {
                cursor.close();
                BasicDBObject teamObject = new BasicDBObject();

                teamObject.append("name", team.getName());

                BasicDBList membersList = new BasicDBList();
                BasicDBList managersList = new BasicDBList();
                if (!team.getMembers().isEmpty()) {
                    for (String members : team.getMembers()) {
                        membersList.add(members);
                    }
                }

                if(!team.getManagers().isEmpty()) {
                    for (String managers : team.getManagers()) {
                        managersList.add(managers);
                    }
                }
                teamObject.append("members", membersList);
                teamObject.append("managers", managersList);
                collection.insert(teamObject);
            }

        }

        if (action.equals(TeamAction.REMOVE)) {

            DBCursor cursor = collection.find(new BasicDBObject("name", team.getName()));

            if(cursor.hasNext()) {
                collection.remove(cursor.next());
            } else {
                System.out.println(team.getName() + " was not in the database!");
            }
        }

    }

    /**
     * Returns a list of online players that match the query
     *
     * @param arg
     * @return
     */
    public List<String> matchPlayer(String arg) {
        List<String> strings = new ArrayList<String>();

        if(arg.isEmpty()) {
            strings.addAll(inTeam.keySet());
            return strings;
        }

        for (String names : inTeam.keySet()) {
            if (names.toLowerCase().equalsIgnoreCase(arg.toLowerCase())) {
                strings.clear(); // Clears any previous matches because this one matches
                strings.add(names);
                break;
            } else if(names.toLowerCase().startsWith(arg.toLowerCase())) {
                 strings.add(names);
                 continue;
             }
        }
        return strings;
    }

    public HashMap<String, BukkitRunnable> getDontMove() {
        return dontMove;
    }


}
