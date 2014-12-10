package org.hcsoups.hardcore.command;

import org.bukkit.command.CommandSender;

/**
 * Created by Ryan on 11/30/2014
 * <p/>
 * Project: HCSoups
 *
 * This interface tells a plugin that it is going to be made into a BaseCommand, and allows you to have the {@link org.hcsoups.hardcore.command.BaseCommandAnn}
 * annotation.
 */
public interface IBaseCommand {
    public void execute(CommandSender sender, String[] args);
}
