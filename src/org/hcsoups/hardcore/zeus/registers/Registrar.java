package org.hcsoups.hardcore.zeus.registers;


public interface Registrar {

    /**
     * Registers only the command that is equal to the name.
     *
     * @param obj
     * @param name
     */
    void registerCommand(String name, Object obj);


    /**
     * Registers all commands in a class that has the @Command annotation.
     *
     * @param obj
     */
    void registerAll(Object obj);


    /**
     * Registers all subcommands
     *
     * @param obj
     */
     void registerAllSubCommands(Object obj);

    /**
     * Registers only one subcommand.
     *
     * @param obj
     * @param name
     */
     void registerSubCommand(Object obj, String name);

}
