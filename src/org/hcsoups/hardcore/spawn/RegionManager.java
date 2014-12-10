package org.hcsoups.hardcore.spawn;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ryan on 11/26/2014
 * <p/>
 * Project: HCSoups
 */
public class RegionManager implements Listener {

    HashMap<String, List<Location>> player_points = new HashMap<String, List<Location>>();

    List<String> selectionMode = new ArrayList<String>();


    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if(selectionMode.contains(event.getPlayer().getName())) {
           if(event.getItem() != null && event.getItem().equals(Material.STICK)) {

           }
        }
    }

}
