package org.hcsoups.hardcore.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.hcsoups.hardcore.utils.TimeUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ryan on 1/1/2015
 * <p/>
 * Project: HCSoups
 */
public class DaybreakBoard {

    private Player player;
    private Objective obj;
    private Set<String> displayScores;


    public DaybreakBoard(final Player player) {
        super();
        this.displayScores = new HashSet<String>();
        this.player = player;
        final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        (this.obj = board.registerNewObjective("Daybreak", "dummy")).setDisplayName("§6§lTimers");
        this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.update();
        player.setScoreboard(board);
    }


    public void update() {
        int nextVal = 14;
        for (final ScoreboardGetter getter : ScoreboardGetter.SCORES) {
            final int seconds = getter.getTime(this.player);
            final String title = getter.getTitle(this.player);
            if (seconds == -1) {
                if (this.displayScores.contains(title)) {
//                    if(title.contains("§a§lCombat Tag")) {
//                       player.sendMessage("§aYou are no longer in combat!");
//                    }

                    this.obj.getScoreboard().resetScores(title);
                    this.displayScores.remove(title);
                }
            } else {
                this.displayScores.add(title);
                this.obj.getScore(title).setScore(nextVal);
                this.getTeam(title, seconds).addPlayer(Bukkit.getOfflinePlayer(title));
            //    System.out.println(getter.getTitle(this.player) + " - " + getter.getTime(this.player));
                --nextVal;
            }
        }
        if (nextVal < 14) {
            this.obj.getScore(ChatColor.RESET + " ").setScore(15);
        }
        else {
            this.obj.getScoreboard().resetScores(ChatColor.RESET + " ");
        }
    }

    private Team getTeam(final String title, final int seconds) {
        final String name = ChatColor.stripColor(title);
        Team team = this.obj.getScoreboard().getTeam(name);
        if (team == null) {
            team = this.obj.getScoreboard().registerNewTeam(name);
        }
        final String time = TimeUtils.getMMSS(seconds);
        team.setSuffix(ChatColor.GRAY + ": " + ChatColor.RED + time);
        return team;
    }



}
