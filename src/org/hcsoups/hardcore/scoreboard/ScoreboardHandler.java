package org.hcsoups.hardcore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcsoups.hardcore.Hardcore;

import java.util.HashMap;

/**
 * Created by Ryan on 1/1/2015
 * <p/>
 * Project: HCSoups
 */
public class ScoreboardHandler {

    private HashMap<String, DaybreakBoard> boards = new HashMap<>();

    public ScoreboardHandler() {
        new BukkitRunnable() {
            @Override
            public void run() {
               for(Player player : Bukkit.getOnlinePlayers()) {
                   ScoreboardHandler.this.tick(player);
                  // System.out.println("Updated for: " + player.getName());
               }
            }
        }.runTaskTimer(Hardcore.getPlugin(Hardcore.class), 20, 20);
    }

    public void tick(final Player player) {
        if (this.boards.containsKey(player.getName())) {
            this.boards.get(player.getName()).update();
        }
        else {
            this.boards.put(player.getName(), new DaybreakBoard(player));
        }
    }

    public void remove(final Player player) {
        this.boards.remove(player.getName());


    }

    public void add(final Player player) {
        this.boards.put(player.getName(), new DaybreakBoard(player));
    }
}
