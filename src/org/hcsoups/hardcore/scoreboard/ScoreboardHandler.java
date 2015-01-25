package org.hcsoups.hardcore.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ryan on 1/11/2015
 * <p/>
 * Project: HCSoups
 */
public class ScoreboardHandler {
    static HashMap<String, DaybreakBoard> boards = new HashMap<>();
    static ArrayList<String> isHidden = new ArrayList<>();

    public static void addBoard(Player p, DaybreakBoard board) {
        boards.put(p.getName(), board);
        ScoreboardTask.addTask(p.getName(), board);
    }

    public static void update(Player player) {
        if(boards.containsKey(player.getName())) {
            boards.get(player.getName()).update();
        }
    }

    public static void hide(Player p) {
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        boards.remove(p.getName());
        ScoreboardTask.removeTask(p.getName());
        isHidden.add(p.getName());
    }

    public static void show(Player p) {
        isHidden.remove(p.getName());
    }


    public static HashMap<String, DaybreakBoard> getBoards() {
        return boards;
    }

    public static boolean isHidden(Player p) {
        return isHidden.contains(p.getName());
    }
}
