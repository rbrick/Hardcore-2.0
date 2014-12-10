package org.hcsoups.hardcore.zeus;

import org.bukkit.command.CommandSender;
import org.hcsoups.hardcore.zeus.registers.bukkit.BukkitRegistrar;

import java.lang.reflect.Method;

/**
 * Created by Ryan on 10/22/2014
 * <p/>
 * Project: Zeus
 */
public class BukkitZeusSubCommand {

   String parent;
   String name;
   String[] aliases;
   String permission;
   Object instance;


    public BukkitZeusSubCommand(String parent, String name, String[] aliases, String permission, Object instance) {
        this.name = name;
        this.parent = parent;
        this.aliases = aliases;
        this.permission = permission;
        this.instance = instance;
    }

		public void execute(CommandSender sender, String[] args) {
		   try {
			  Method method = BukkitRegistrar.getRawRegisteredSubcommands().get(parent).get(name);
			  method.invoke(instance, sender, args);
     	   } catch (Exception ex) {
			  ex.printStackTrace();
	     }
	  }

    public String getPermission() {
        return permission;
    }

    public String getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }
}
