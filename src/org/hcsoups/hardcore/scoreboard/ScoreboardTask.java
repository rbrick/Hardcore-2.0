package org.hcsoups.hardcore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.combattag.CombatTagHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Ryan on 1/11/2015
 * <p/>
 * Project: HCSoups
 */
public class ScoreboardTask extends BukkitRunnable {

    private static HashMap<String, DaybreakBoard> hasTask = new HashMap<>();
    private static HashSet<String> toRemove = new HashSet<>();

    @Override
    public void run() {
       if(hasTask.size() != 0) {
           for(int i = 0; i < hasTask.size(); i++) {
             Map.Entry<String, DaybreakBoard> b = (Map.Entry<String, DaybreakBoard>) hasTask.entrySet().toArray()[i];

            if(b.getValue() != null) {
                b.getValue().update();
            }

               if(toRemove.contains(b.getKey())) {
                   toRemove.remove(b.getKey());
                   hasTask.remove(b.getKey());
               }

           }

       }
     //   hasTask.clear();
    }

    public static void addTask(String name, DaybreakBoard board) {
        hasTask.put(name, board);
    }

    public static void removeTask(String name) {
       if(Bukkit.getPlayer(name)!= null && !CombatTagHandler.isTagged(Bukkit.getPlayer(name))) {
           toRemove.add(name);
       }
    }
}
