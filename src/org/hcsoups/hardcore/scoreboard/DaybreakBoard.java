package org.hcsoups.hardcore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ryan on 1/11/2015
 * <p/>
 * Project: HCSoups
 */
public class DaybreakBoard {

    Player p;

    Scoreboard scoreboard;

    Objective obj;

    HashMap<String, Integer> scores = new HashMap<>();



    public DaybreakBoard(Player p) {
        this.p = p;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.obj = scoreboard.registerNewObjective("daybreak", "dummy");
        obj.setDisplayName("§7- §b§lAdvancedPvP §7-");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }


    public void resetByScore(int score) {
        if(!scores.containsValue(score)) {
            throw new IllegalArgumentException("Score does not exists!");
        } else {
            for (Map.Entry<String, Integer> emap : scores.entrySet()) {
                  if(emap.getValue() == score) {
                      scoreboard.resetScores(emap.getKey());
                  }
            }
        }
    }

//    public int previous() {
//        int previous = max +1;
//        return previous;
//    }
//
//    public int next() {
//        int next = max -1;
//        return next;
//    }

    public void add(String name) {
        scores.put(ChatColor.translateAlternateColorCodes('&', name), -1);

    }

    public void update() {
        int max = 15;

         for(String score : scoreboard.getEntries()) {
              scoreboard.resetScores(score);
         }

        String spacer = "";

         for (ScoreGetter getter : ScoreGetter.SCORES) {
             scoreboard.resetScores(getter.getName());
             scoreboard.resetScores(getter.getValue(p));
             obj.getScore(getter.getName()).setScore(max);
             max -= 1;
             obj.getScore(getter.getValue(p)).setScore(max);
             max -= 1;
             obj.getScore(spacer).setScore(max);
             max -= 1;
             spacer += " ";
         }

        p.setScoreboard(scoreboard);
    }

}
