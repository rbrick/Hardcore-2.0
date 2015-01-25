package org.hcsoups.hardcore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 11/29/2014
 * <p/>
 * Project: HCSoups
 */
public abstract class BaseCommand implements TabExecutor {

    String name;
    String permission;
    String[] aliases;
    String usage;
    int maxArgs;
    int minArgs;

    public BaseCommand(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (permission != null && !sender.hasPermission(this.permission)) {
            sender.sendMessage("§cNo permission!");
            return false;
        } else {

             if(!(maxArgs < 0)) {
                if(strings.length > maxArgs) {
                   if (getUsage() != null) {
                       sender.sendMessage(getUsage().replace("<command>", s));
                   }
                    return true;
                }
            }

            if(!(minArgs < 0)) {
                if(strings.length < minArgs) {
                    if (getUsage() != null) {
                        sender.sendMessage(getUsage().replace("<command>", s));
                    }
                    return true;
                }
            }

            execute(sender, strings);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return tabComplete(strings, sender);
    }

    public abstract void execute(CommandSender sender, String[] args);

    public List<String> tabComplete(String[] args, CommandSender sender) {
        return new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getUsage() {
        return usage;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }
}
