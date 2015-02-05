package org.hcsoups.hardcore.teams;

import com.mongodb.*;
import lombok.AccessLevel;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;

import javax.annotation.Nonnull;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

public class TeamManagerUUID implements Listener {

    // The teams the players are in.
    HashMap<UUID, TeamUUID> inTeam = new HashMap<>();

    // The teams that exist
    ArrayList<TeamUUID> teams = new ArrayList<>();

    ArrayList<UUID> teamChat = new ArrayList<>();

    File inTeamFile = new File(Hardcore.getPlugin(Hardcore.class).getDataFolder(), "inteam.json");

    Pattern isJson = Pattern.compile("([a-zA-Z0-9_]\\.json)");

    Pattern isValid = Pattern.compile("^[A-Za-z0-9_]*$");


    HashMap<String, BukkitRunnable> dontMove = new HashMap<>();

    HashMap<String, Long> onCooldown = new HashMap<>();

    @Getter(AccessLevel.PUBLIC)
    HashSet<File> filesToDelete = new HashSet<>();

//    Jedis db = Hardcore.getPlugin(Hardcore.class).getJedis();

    DBCollection collection;
    DBCollection players;
    DBCollection inTeamColl;

    long interval = 600000;

    protected TeamManagerUUID() {
        collection = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("teams");
        players = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("players");
        inTeamColl = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("inteam");
    }


    private static TeamManagerUUID instance = new TeamManagerUUID();

    public static TeamManagerUUID getInstance() {
        return instance;
    }


    public TeamUUID createTeam(Player player, String name, String password) {
        if (!name.matches(isValid.pattern())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§cInvalid name!"));
            return null;
        }

        if (doesTeamExist(name)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§cThe team '" + name + "' already exists!"));
            return null;
        }

        if (isOnTeam(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§cYou are already on a team!"));
            return null;
        }

        ArrayList<UUID> mans = new ArrayList<UUID>();

        mans.add(player.getUniqueId());

        TeamUUID team1 = new TeamUUID(name, mans, new ArrayList<UUID>(), password);
        player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§7Team created!\n§7To learn more about teams, do /team"));
        inTeam.put(player.getUniqueId(), team1);
        saveInTeam();
        teams.add(team1);
        return team1;
    }


    public TeamUUID createTeam(Player player, String name) {
        return createTeam(player, name, "");
    }


    public boolean joinTeam(String name, String password, Player player) {
        TeamUUID team = matchTeam(name);

        if (isOnTeam(player.getUniqueId())) {
            player.sendMessage("§cYou are already on a team!");
            return false;
        }

        if (team == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§cCould not find team '" + name + "'."));
            return false;
        } else {
            if (!team.getPassword().isEmpty()) {
                if (password.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§cTeam requires a password!"));
                    return false;
                } else {
                    if (password.equals(team.getPassword())) {
                        messageTeam(team, "§7" + player.getName() + " has joined the team!");
                        player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§7You have successfully joined the team!"));
                        team.getMembers().add(player.getUniqueId());
                        inTeam.put(player.getUniqueId(), team);
                        //      saveInTeam();
                        //      saveTeam(team);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§cWrong password!"));
                        return false;
                    }
                }
            } else {
                messageTeam(team, "§7" + player.getName() + " has joined the team!");
                player.sendMessage(ChatColor.translateAlternateColorCodes('§', "§7You have successfully joined the team!"));
                team.getMembers().add(player.getUniqueId());
                inTeam.put(player.getUniqueId(), team);
                //   saveInTeam();
                //     saveTeam(team);
                return true;
            }
        }
    }


    public void messageTeam(TeamUUID team, String message) {
        message = ChatColor.translateAlternateColorCodes('§', message);
        for (UUID member : team.getMembers()) {
            Player p = Bukkit.getPlayer(member);
            if (p == null) {
                continue;
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('§', message));
            }
        }

        for (UUID member : team.getManagers()) {
            Player p = Bukkit.getPlayer(member);
            if (p == null) {
                continue;
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('§', message));
            }
        }
    }

    public TeamUUID getPlayerTeam(Player player) {
        // if for some reason our test in the command doesn't pass have this for back up! :)
        Validate.notNull(player, "Player cannot be null!");
        // Theoretically the player will be with in the hashmap...
        return inTeam.get(player.getUniqueId());

    }

    public TeamUUID getPlayerTeam(String player) {
        // if for some reason our test in the command doesn't pass have this for back up! :)
        Validate.notNull(player, "Player cannot be null!");
        // Theoretically the player will be with in the hashmap...
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return null;
        }
        return inTeam.get(op.getUniqueId());

    }


    public TeamUUID getPlayerTeam(UUID player) {
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

        if (options.size() <= 0) {
            // See if the player is
            if (getPlayerTeam(name) != null) {
                TeamUUID team = getPlayerTeam(name);

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
                        for (UUID man : team.getManagers()) {
                            Player manp = Bukkit.getPlayer(man);
                            if (manp != null) {
                                player.sendMessage(" §3" + manp.getName() + " §7- " + formatHealth((manp.getHealth())));
                            } else {
                                player.sendMessage(" §3" + Bukkit.getOfflinePlayer(man).getName() + " §7- Offline");
                            }
                        }
                    }
                    if (team.getMembers().size() >= 1) {
                        for (UUID mem : team.getMembers()) {
                            Player memp = Bukkit.getPlayer(mem);
                            if (memp != null) {
                                player.sendMessage(" §7" + memp.getName() + " - " + formatHealth((memp.getHealth())));
                            } else {
                                player.sendMessage(" §7" + Bukkit.getOfflinePlayer(mem).getName() + " - Offline");
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
                if (i < options.size() - 1) {
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


        TeamUUID team = getPlayerTeam(options.get(0));

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
                for (UUID man : team.getManagers()) {
                    Player manp = Bukkit.getPlayer(man);
                    if (manp != null) {
                        player.sendMessage(" §3" + manp.getName() + " §7- " + formatHealth((manp.getHealth())));
                    } else {
                        player.sendMessage(" §3" + Bukkit.getOfflinePlayer(man).getName() + " §7- Offline");
                    }
                }
            }
            if (team.getMembers().size() >= 1) {
                for (UUID mem : team.getMembers()) {
                    Player memp = Bukkit.getPlayer(mem);
                    if (memp != null) {
                        player.sendMessage(" §7" + memp.getName() + " - " + formatHealth((memp.getHealth())));
                    } else {
                        player.sendMessage(" §7" + Bukkit.getOfflinePlayer(mem).getName() + " - Offline");
                    }
                }
            }
            return;
        }

    }

    // For commands such as /t r <Name>
    public void sendInfo(Player player, TeamUUID team) {
        Validate.notNull(team);
        if (team == null) {
            player.sendMessage("§cTeam does not exist!");
        }
        if (getPlayerTeam(player) != null && getPlayerTeam(player).equals(team)) {
            sendInfo(player);
            return;
        } else {
            player.sendMessage("§7***§3" + team.getName() + "§7***");

            if (team.getManagers().size() >= 1) {
                for (UUID man : team.getManagers()) {
                    Player manp = Bukkit.getPlayer(man);
                    if (manp != null) {
                        player.sendMessage(" §3" + manp.getName() + " §7- " + formatHealth((manp.getHealth())));
                    } else {
                        player.sendMessage(" §3" + Bukkit.getOfflinePlayer(man).getName() + " §7- Offline");
                    }
                }
            }
            if (team.getMembers().size() >= 1) {
                for (UUID mem : team.getMembers()) {
                    Player memp = Bukkit.getPlayer(mem);
                    if (memp != null) {
                        player.sendMessage(" §7" + memp.getName() + " - " + formatHealth((memp.getHealth())));
                    } else {
                        player.sendMessage(" §7" + Bukkit.getOfflinePlayer(mem).getName() + " - Offline");
                    }
                }
            }
        }

    }

    public void sendInfo(Player player) {
        if (inTeam.get(player.getUniqueId()) == null) {
            player.sendMessage("§cYou are not on any team!");
            return;
        }
        TeamUUID team = inTeam.get(player.getUniqueId());

        player.sendMessage("§7***§3" + team.getName() + "§7***");
        player.sendMessage("§7Password: " + (team.getPassword().isEmpty() || team.getPassword().equals("") ? "§cNot Set" : "§a" + team.getPassword()));
        player.sendMessage("§7Valor Points: §b" + (team.getValorPoints()));
        player.sendMessage("§7Team HQ: " + (team.getHq() == null ? "§cNot Set" : "§aSet"));
        player.sendMessage("§7Team Rally: " + (team.getRally() == null ? "§cNot Set" : "§aSet"));
        player.sendMessage("§7Friendly Fire is " + (team.isFriendlyFire() ? "§con" : "§aoff"));
        player.sendMessage("§7Members: ");

        if (team.getManagers().size() >= 1) {
            for (UUID man : team.getManagers()) {
                Player manp = Bukkit.getPlayer(man);
                if (manp != null) {
                    player.sendMessage(" §3" + manp.getName() + " §7- " + formatHealth((manp.getHealth())));
                } else {
                    player.sendMessage(" §3" + Bukkit.getOfflinePlayer(man).getName() + " §7- Offline");
                }
            }
        }
        if (team.getMembers().size() >= 1) {
            for (UUID mem : team.getMembers()) {
                Player memp = Bukkit.getPlayer(mem);
                if (memp != null) {
                    player.sendMessage(" §7" + memp.getName() + " - " + formatHealth((memp.getHealth())));
                } else {
                    player.sendMessage(" §7" + Bukkit.getOfflinePlayer(mem).getName() + " - Offline");
                }
            }
        }
    }


    String formatHealth(double health) {
        double hearts = health / 2;
        DecimalFormat format = new DecimalFormat("#.#");

        if (hearts <= 10 && hearts >= 8) {
            return String.format("§a%s ❤", format.format(hearts));
        } else if (hearts <= 7 && hearts >= 4) {
            return String.format("§e%s ❤", format.format(hearts));
        } else {
            return String.format("§c%s ❤", format.format(hearts));
        }
    }

    public boolean doesTeamExist(String name) {
        for (TeamUUID team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public TeamUUID matchTeam(String name) {
        for (TeamUUID team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }


    public void loadTeams() {
        DBCursor cursor = collection.find();

        while (cursor.hasNext())  {
            loadTeam((BasicDBObject)cursor.next());
        }


    }


    // Fills the inTeam Map. Called after loadTeams()
    public void loadInTeam() {
       DBCursor cursor = inTeamColl.find();
       while (cursor.hasNext()) {
           BasicDBObject obj = (BasicDBObject) cursor.next();
           UUID id = UUID.fromString(obj.getString("id"));
           TeamUUID team = matchTeam(obj.getString("team"));
           if(team != null) {
               inTeam.put(id, team);
           }
       }

    }

    public void saveInTeam() {
        for(Map.Entry<UUID, TeamUUID> entry : inTeam.entrySet()) {
              BasicDBObject obj = new BasicDBObject()
                      .append("id", entry.getKey().toString())
                      .append("team", entry.getValue().getName());

            DBCursor cursor = inTeamColl.find(new BasicDBObject("id", entry.getKey().toString()));

            if(cursor.hasNext()) {
             inTeamColl.update(cursor.getQuery(), obj);
            } else {
              inTeamColl.insert(obj, WriteConcern.NORMAL);
            }


        }
    }


    /**
     * Loads a team into memory from a file.
     * <p/>
     * This uses the SimpleJSON Library.
     *
     * @param object
     */
    public void loadTeam(BasicDBObject object) {
        try {

            String name = object.getString("name");

            String password;

            if (object.get("password") != null) {
                password = object.getString("password");
            } else {
                password = "";
            }

            boolean friendlyFire = object.getBoolean("friendlyFire");


            BasicDBList members = (BasicDBList) object.get("members");

            ArrayList<UUID> memberss = new ArrayList<>();

            if(members != null) {
                for (Object obj : members) {
                    memberss.add(UUID.fromString((String) obj));

                }
            }


            BasicDBList managerss = (BasicDBList) object.get("managers");


            ArrayList<UUID> managers = new ArrayList<>();

            if(managerss != null) {
                for (Object obj : managerss) {
                    managers.add(UUID.fromString((String) obj));
                }
            }

            Location hq;
            if (object.get("hq") != null) {
                hq = locFromString(object.getString("hq"), ',');
            } else {
                hq = null;
            }

            Location rally;
            if (object.get("rally") != null) {
                rally = locFromString(object.getString("rally"), ',');
            } else {
                rally = null;
            }

            int vp = 0;
            if(object.get("valorpoints") != null) {
               vp = object.getInt("valorpoints");
            }


            TeamUUID team = new TeamUUID(name, managers, memberss, password);
            team.setFriendlyFire(friendlyFire);
            team.setRally(rally);
            team.setHq(hq);
            team.setValorPoints(vp);

            teams.add(team);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void saveTeam(TeamUUID team) {
        DBCursor cursor = collection.find(new BasicDBObject("name", team.getName()));
        if (cursor.hasNext()) {
            // in the database
            BasicDBObject obj = new BasicDBObject();
            BasicDBList mems = new BasicDBList();
            BasicDBList mans = new BasicDBList();
            obj.put("name", team.getName());
            for (UUID man : team.getManagers()) {
                mans.add(man.toString());
            }
            obj.put("managers", mans);
            for (UUID mem : team.getMembers()) {
                mems.add(mem.toString());
            }
            obj.put("members", mems);
            if (!team.getPassword().isEmpty()) {
                obj.put("password", team.getPassword());
            }
            obj.put("valorpoints", team.getValorPoints());
            obj.put("friendlyFire", team.isFriendlyFire());
            if (team.getHq() != null) {
                obj.put("hq", locToString(team.getHq(), ','));
            }
            if (team.getRally() != null) {
                obj.put("rally", locToString(team.getRally(), ','));
            }
            collection.update(cursor.getQuery(), obj);
        } else {
            // Not in the database
            BasicDBObject obj = new BasicDBObject();
            BasicDBList mems = new BasicDBList();
            BasicDBList mans = new BasicDBList();
            obj.put("name", team.getName());
            for (UUID man : team.getManagers()) {
                mans.add(man.toString());
            }
            obj.put("managers", mans);
            for (UUID mem : team.getMembers()) {
                mems.add(mem.toString());
            }
            obj.put("members", mems);
            if (!team.getPassword().isEmpty()) {
                obj.put("password", team.getPassword());
            }
            obj.put("valorpoints", team.getValorPoints());
            obj.put("friendlyFire", team.isFriendlyFire());
            if (team.getHq() != null) {
                obj.put("hq", locToString(team.getHq(), ','));
            }
            if (team.getRally() != null) {
                obj.put("rally", locToString(team.getRally(), ','));
            }
            collection.insert(obj, WriteConcern.NORMAL);
        }
    }

    public void leaveTeam(final Player player) {
        if (inTeam.get(player.getUniqueId()) == null) {
            player.sendMessage("§cYou are not in a team!");
        } else {
            final TeamUUID team = inTeam.get(player.getUniqueId());
            team.getMembers().remove(player.getUniqueId());
            team.getManagers().remove(player.getUniqueId());
            if (team.getManagers().size() <= 0 && team.getMembers().size() <= 0) {
                disbandTeam(team);

                inTeam.remove(player.getUniqueId());
                teamChat.remove(player.getUniqueId());
                player.sendMessage("§3Successfully left and disbanded team!");
                return;
            }
            messageTeam(team, "§3" + player.getName() + " has left the team!");
            player.sendMessage("§3You have left the team!");
            //    saveTeam(team);
            inTeam.remove(player.getUniqueId());
            //   saveInTeam();
            teamChat.remove(player.getUniqueId());
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
        for (TeamUUID team : teams) {
            saveTeam(team);
        }
    }

    public boolean isManager(Player player) {
        return getPlayerTeam(player) != null && getPlayerTeam(player).getManagers().contains(player.getUniqueId());
    }

    public String formatLocation(Location location) {
        DecimalFormat format = new DecimalFormat("#.##");
        return "X: " + format.format(location.getX()) + ", Y: " + format.format(location.getY()) + ", Z: " + format.format(location.getZ());
    }

    public boolean isOnTeam(UUID player) {
        return inTeam.get(player) != null;
    }

    public File getInTeamFile() {
        return inTeamFile;
    }

    public HashMap<UUID, TeamUUID> getInTeam() {
        return inTeam;
    }

    public ArrayList<UUID> getTeamChat() {
        return teamChat;
    }

    // Used when the team has no more players left.
    public void disbandTeam(TeamUUID team) {
        teams.remove(team);
         DBCursor cursor = collection.find(new BasicDBObject("name", team.getName()));
        if(cursor.hasNext()) {
            collection.remove(cursor.next());
        }
        cursor.close();

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

                if (dontMove.containsKey(p.getName())) {
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


    /**
     * Returns a list of online players that match the query
     *
     * @param arg
     * @return
     */
    public List<String> matchPlayer(String arg) {
        List<String> strings = new ArrayList<String>();

        if (arg.isEmpty()) {
            return strings;
        }

        for (UUID names : inTeam.keySet()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(names);
            if (op.getName().toLowerCase().equalsIgnoreCase(arg.toLowerCase())) {
                strings.clear(); // Clears any previous matches because this one matches
                strings.add(op.getName());
                break;
            } else if (op.getName().toLowerCase().startsWith(arg.toLowerCase())) {
                strings.add(op.getName());
            }
        }
        return strings;
    }

    public HashMap<String, BukkitRunnable> getDontMove() {
        return dontMove;
    }

    public ArrayList<TeamUUID> getTeams() {
        return teams;
    }

    public void setInTeam(HashMap<UUID, TeamUUID> inTeam) {
        this.inTeam = inTeam;
    }

    public void setTeams(ArrayList<TeamUUID> teams) {
        this.teams = teams;
    }
}
