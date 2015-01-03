package org.hcsoups.hardcore.scoreboard;

import org.bukkit.entity.Player;
import org.hcsoups.hardcore.combattag.CombatTagHandler;

/**
 * Created by Ryan on 1/1/2015
 * <p/>
 * Project: HCSoups
 */
public interface ScoreboardGetter {

    public static ScoreboardGetter COMBAT_TAG = new ScoreboardGetter() {
        @Override
        public String getTitle(Player p) {
            return "§a§lCombat Tag";
        }

        @Override
        public int getTime(Player p) {
            long diff;
            if(CombatTagHandler.isTagged(p)) {
                diff = CombatTagHandler.getTag(p);
                if (diff >= 0L) {
                    return (int)diff / 1000;
                }
            }
            return -1;
        }
    };

    public static ScoreboardGetter TELEPORT_WAIT = new ScoreboardGetter() {
        @Override
        public String getTitle(Player p) {
            return "§7§lTeleport";
        }

        @Override
        public int getTime(Player p) {
            return -1;
        }
    };


    public static ScoreboardGetter[] SCORES = {
        COMBAT_TAG,
        TELEPORT_WAIT
    };

    String getTitle(Player p);
    int getTime(Player p);
}
