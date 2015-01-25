package org.hcsoups.hardcore.scoreboard;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.combattag.CombatTagHandler;
import org.hcsoups.hardcore.teams.TeamManager;
import org.hcsoups.hardcore.utils.TimeUtils;

/**
 * Created by Ryan on 1/11/2015
 * <p/>
 * Project: HCSoups
 */
public interface ScoreGetter {

    static ScoreGetter TEAM_GETTER = new ScoreGetter() {
        @Override
        public String getName() {
            return "§3§lTeam";
        }

        @Override
        public String getValue(Player player) {
            if(TeamManager.getInstance().isOnTeam(player.getName())) {
                return TeamManager.getInstance().getPlayerTeam(player).getName();
            } else {
                return "None";
            }
        }
    };

    static ScoreGetter VALOR_POINT_GETTER = new ScoreGetter() {
        @Override
        public String getName() {
            return "§a§lValor Points";
        }

        @Override
        public String getValue(Player player) {
            if(!TeamManager.getInstance().isOnTeam(player.getName())) {
                return 0 + "";
            }
            return TeamManager.getInstance().getPlayerTeam(player).getValorPoints() + ""; // Kills
        }
    };

    static ScoreGetter COMBAT_TAG_GETTER = new ScoreGetter() {
        @Override
        public String getName() {
            return "§c§lCombat Tag";
        }

        @Override
        public String getValue(Player player) {
            if(!CombatTagHandler.isTagged(player)) {
                ScoreboardTask.removeTask(player.getName());
                return "Not in combat.";
            }
            return TimeUtils.getMMSS((int)CombatTagHandler.getTag(player)/1000); // Kills
        }
    };

    String getName();

    String getValue(Player player);

//    int valuePosition(Player player);
//
//    int keyPosition(Player player);

       // position
  //  int getScore(Player player);
    public static ScoreGetter[] SCORES = {TEAM_GETTER, VALOR_POINT_GETTER, COMBAT_TAG_GETTER};
}
