package org.hcsoups.hardcore.zeus.registers.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.hcsoups.hardcore.zeus.BukkitZeusCommand;
import org.hcsoups.hardcore.zeus.BukkitZeusSubCommand;
import org.hcsoups.hardcore.zeus.annotations.Command;
import org.hcsoups.hardcore.zeus.annotations.SubCommand;
import org.hcsoups.hardcore.zeus.annotations.TabCompleter;
import org.hcsoups.hardcore.zeus.exceptions.InvalidMethodException;
import org.hcsoups.hardcore.zeus.registers.Registrar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BukkitRegistrar implements Registrar {

    // Registered Commands
    static HashMap<String, Method> registeredCommands = new HashMap<String, Method>();
    static HashMap<String, BukkitZeusCommand> registeredZeusCommands = new HashMap<String, BukkitZeusCommand>();

    // Parent commands paired with subcommands
    static HashMap<String, HashMap<String, BukkitZeusSubCommand>> registeredSubcommands = new HashMap<String, HashMap<String, BukkitZeusSubCommand>>();
    static HashMap<String, HashMap<String, Method>> rawRegisteredSubcommands = new HashMap<String, HashMap<String, Method>>();


    // SubCommands
    static HashMap<String, Method> realRegisteredSubcommands = new HashMap<String, Method>();
    static HashMap<String, BukkitZeusSubCommand> registeredZeusSubCommands = new HashMap<String, BukkitZeusSubCommand>();

    @Override
    public void registerCommand(String name, Object obj) {
        for(Method m : obj.getClass().getMethods()) {
            if(m.isAnnotationPresent(Command.class)) {
                Command command = (Command) m.getAnnotation(Command.class);
                if(command.name().equalsIgnoreCase(name)) {
                    // TODO Inject commands into Bukkit.
                    try {
                        Constructor<?> commandConstructor =  BukkitZeusCommand.class.getDeclaredConstructor(String.class, String.class, String.class, List.class, Object.class);
                        commandConstructor.setAccessible(true);
                        System.out.println("Successfully hooked into org.bukkit.command.Command");

                        BukkitZeusCommand command1 = (BukkitZeusCommand) commandConstructor.newInstance(command.name(), command.desc(), command.usage(), Arrays.asList(command.aliases()), obj);

                        command1.setPermission(command.permission());
                        command1.setPermissionMessage((command.permissionMsg().isEmpty() ? command1.getPermissionMessage() : command.permissionMsg()));
                        command1.setMaxArgs(command.maxArgs());
                        command1.setMinArgs(command.minArgs());


                        System.out.println("Successfully created new org.bukkit.command.Command.\nInjecting...");



                        // Confirm that the method is correct.
                        if(m.getParameterTypes() == null ||  !m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class) {
                            throw new InvalidMethodException("Invalid parameter types!");
                        }

                        registeredCommands.put(command.name(), m);
                        registeredZeusCommands.put(command.name(), command1);

                        System.out.println(m.getDeclaringClass().getName());

                        Field field =  Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
                        field.setAccessible(true);

                        CommandMap map = (CommandMap) field.get(Bukkit.getServer().getPluginManager());

                        map.register(command.name(), command1);




                        System.out.println("Successfully injected command '" + command.name() + "'.");



                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void registerAll(Object obj) {
        for(Method m : obj.getClass().getMethods()) {
            if(m.isAnnotationPresent(Command.class)) {
                Command command = (Command) m.getAnnotation(Command.class);
                // TODO Inject commands into Bukkit.
                try {
                    Constructor<?> commandConstructor =  BukkitZeusCommand.class.getDeclaredConstructor(String.class, String.class, String.class, List.class, Object.class);
                    commandConstructor.setAccessible(true);
                    System.out.println("Successfully hooked into org.bukkit.command.Command");

                    BukkitZeusCommand command1 = (BukkitZeusCommand) commandConstructor.newInstance(command.name(), command.desc(), command.usage(), Arrays.asList(command.aliases()), obj);
                    org.bukkit.command.TabCompleter completer = null;

                    command1.setPermission(command.permission());

                    command1.setPermissionMessage((command.permissionMsg().isEmpty() ? command1.getPermissionMessage() : command.permissionMsg()));

                    command1.setMaxArgs(command.maxArgs());
                    command1.setMinArgs(command.minArgs());

                    System.out.println("Successfully created new org.bukkit.command.Command.\nInjecting...");

                    // Confirm that the method is correct.
                    if(m.getParameterTypes() == null || !m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class) {
                        throw new InvalidMethodException("Invalid parameter types!");
                    }

                    registeredCommands.put(command.name(), m);
                    registeredZeusCommands.put(command.name(), command1);

                    System.out.println(m.getDeclaringClass().getName());

                    Field field =  Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
                    field.setAccessible(true);
                    CommandMap map = (CommandMap) field.get(Bukkit.getServer().getPluginManager());
                    map.register(command.name(), command1);

                    System.out.println("Successfully injected command '" + command.name() + "'.");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void registerAllSubCommands(Object obj) {
         for(Method m : obj.getClass().getMethods()) {
             if(m.isAnnotationPresent(SubCommand.class)) {
                 SubCommand sc = m.getAnnotation(SubCommand.class);

                 if(!getRegisteredCommands().containsKey(sc.parent()) && !registeredZeusCommands.containsKey(sc.parent())) {
                     System.err.println("Bad parent!");
                     return;
                 }
                 try {

                     if(m.getParameterTypes() == null || !m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class ) {
                         System.out.println("Bad parameters!");
                         return;
                     }

                     BukkitZeusSubCommand subCommand = new BukkitZeusSubCommand(sc.parent(), sc.name(), sc.aliases(), sc.permission(), obj);

                     realRegisteredSubcommands.put(sc.name(), m);

                     rawRegisteredSubcommands.put(sc.parent(), realRegisteredSubcommands);

                     registeredZeusSubCommands.put(sc.name(), subCommand);

                     registeredSubcommands.put(sc.parent(), registeredZeusSubCommands);

                     System.out.println("Registered subcommand '" + sc.name() + "' for command '" + sc.parent() + "'");

                 } catch (Exception ex) {
                     ex.printStackTrace();
                 }

             }
         }
    }

    @Override
    public void registerSubCommand(Object obj, String name) {
        for(Method m : obj.getClass().getMethods()) {
            if(m.isAnnotationPresent(SubCommand.class)) {
                SubCommand sc = m.getAnnotation(SubCommand.class);

              if(sc.name().equalsIgnoreCase(name)) {
                  if (!getRegisteredCommands().containsKey(sc.parent()) && !registeredZeusCommands.containsKey(sc.parent())) {
                      System.err.println("Bad parent!");
                      return;
                  }
                  try {

                      if (m.getParameterTypes() == null || !m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class) {
                          System.out.println("Bad parameters!");
                          return;
                      }

                      BukkitZeusSubCommand subCommand = new BukkitZeusSubCommand(sc.parent(), sc.name(), sc.aliases(), sc.permission(), obj);

                      realRegisteredSubcommands.put(sc.name(), m);

                      rawRegisteredSubcommands.put(sc.parent(), realRegisteredSubcommands);

                      registeredZeusSubCommands.put(sc.name(), subCommand);

                      registeredSubcommands.put(sc.parent(), registeredZeusSubCommands);

                      System.out.println("Registered subcommand '" + sc.name() + "' for command '" + sc.parent() + "'");

                  } catch (Exception ex) {
                      ex.printStackTrace();
                  }
              }
            }
        }
    }


    public static HashMap<String, Method> getRegisteredCommands() {
        return registeredCommands;
    }


    public static HashMap<String, HashMap<String, BukkitZeusSubCommand>> getRegisteredSubcommands() {
        return registeredSubcommands;
    }

    public static HashMap<String, HashMap<String, Method>> getRawRegisteredSubcommands() {
        return rawRegisteredSubcommands;
    }

    public static HashMap<String, BukkitZeusSubCommand> getRegisteredZeusSubCommands() {
        return registeredZeusSubCommands;
    }

}
