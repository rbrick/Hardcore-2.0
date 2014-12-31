package org.hcsoups.hardcore.teams;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.command.BaseCommand;
import org.hcsoups.hardcore.zeus.annotations.Command;
import org.hcsoups.hardcore.zeus.annotations.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BaseTeamCommand extends BaseCommand {

    private Hardcore main;

    List<String> list = Arrays.asList("create", "join", "leave", "info", "roster", "chat", "hq", "rally", "ff", "password", "kick", "promote", "demote", "sethq", "setrally");

    String[] teamUsage = {"§7***§3Anyone§7***", "§7/team create <Name> [Password] - Creates a new team","§7/team join <Name> [Password] - Join a team","§7/team leave - Leave your current team",
            "§7/team info [Player] - Shows information about a player's team","§7/team roster [Team] - Shows information about a given team","§7/team chat - Toggle team chat mode",
            "§7/team hq - Teleports you to your team's headquarters","§7/team rally - Teleports you to your team's rally point","§7***§3Managers Only§7***","§7/team ff <On/Off> - Toggle friendly fire",
            "§7/team password <Password/None/Null/Nil> - Sets your team's password","§7/team kick <Player> - Kicks a player from the team","§7/team promote <Player> - Promote a player to manager",
            "§7/team demote <Player> - Demote a player to member","§7/team sethq - Sets the teams headquarters","§7/team setrally - Sets the teams rally point"};

    public BaseTeamCommand(Hardcore main) {
        super("team",null, "t");

        this.main = main;
        setMinArgs(1);
        setMaxArgs(3);
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < teamUsage.length; i++) {
            if (i < teamUsage.length - 1) {
                builder.append(teamUsage[i]).append('\n');
            } else {
                builder.append(teamUsage[i]);
            }
        }

        setUsage(builder.toString());

    }

    public TeamSubCommand getSubCommand(String key) {
        TeamSubCommand tc = null;
        for (TeamSubCommand sub : main.getTcommands()) {
            if (sub.getName().equalsIgnoreCase(key)) {
                tc = sub;
                return tc;
            } else {
                if (sub.getAliases().contains(key.toLowerCase())) {
                    tc = sub;
                    return tc;
                }
            }
        }
        return tc;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7***§3Anyone§7***");
            // /team create
            sender.sendMessage("§7/team create <Name> [Password] - Creates a new team");
            // team join
            sender.sendMessage("§7/team join <Name> [Password] - Join a team");
            // /team leave
            sender.sendMessage("§7/team leave - Leave your current team");
            // /team info
            sender.sendMessage("§7/team info [Player] - Shows information about a player's team");
            // /team roster
            sender.sendMessage("§7/team roster [Team] - Shows information about a given team");
            // /team chat
            sender.sendMessage("§7/team chat - Toggle team chat mode");
            // /team hq
            sender.sendMessage("§7/team hq - Teleports you to your team's headquarters");
            // /team rally
            sender.sendMessage("§7/team rally - Teleports you to your team's rally point");

            sender.sendMessage("§7***§3Managers Only§7***");
            // /t ff
            sender.sendMessage("§7/team ff <On/Off> - Toggle friendly fire");
            // /team password
            sender.sendMessage("§7/team password <Password/None/Null/Nil> - Sets your team's password");
            // /team kick
            sender.sendMessage("§7/team kick <Player> - Kicks a player from the team");
            // /team promote
            sender.sendMessage("§7/team promote <Player> - Promote a player to manager");
            // /team demote
            sender.sendMessage("§7/team demote <Player> - Demote a player to member");
            // /team sethq
            sender.sendMessage("§7/team sethq - Sets the teams headquarters");
            // /team setrally
            sender.sendMessage("§7/team setrally - Sets the teams rally point");
            return;
        } else {
            try {
                TeamSubCommand tc = getSubCommand(args[0]);
                if(tc == null) {
                    sender.sendMessage("§cUnrecognized team command!\nDo /team for help.");
                    return;
                }
                tc.execute(((Player) sender), fixArgs(args));
                return;
            } catch (Exception ex) {
               sender.sendMessage("§cAn unexpected error occured: " + ex.getLocalizedMessage() + "\nContact an admin!");
                ex.printStackTrace();
            }
        }
        return;

    }

    @Override
    public List<String> tabComplete(String[] args) {
        Collections.sort(list);

        if(args.length == 0) {
            return list;
        }

        if (args.length == 1) {
            List<String> list1 = new ArrayList<String>();
            for (String s : list) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list1.add(s);
                }
            }
            return list1;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("friendlyfire") || args[0].equalsIgnoreCase("ff") ||
                    args[0].equalsIgnoreCase("setfriendlyfire") || args[0].equalsIgnoreCase("setff")) {
                List<String> options = Arrays.asList("true", "on", "off", "false");
                List<String> list2return = new ArrayList<String>();
                for (String opt : options) {
                    if (opt.toLowerCase().startsWith(args[1])) {
                        list2return.add(opt);
                    }
                }
                Collections.sort(list2return);
                return list2return;
            }
        }
        return list;
    }

    public String[] fixArgs(String[] args) {
        String[] subArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            subArgs[i - 1] = args[i];
        }
      return subArgs;
    }



}