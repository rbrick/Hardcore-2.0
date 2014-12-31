package org.hcsoups.hardcore.stats;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.bukkit.entity.Player;
import org.hcsoups.hardcore.Hardcore;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Ryan on 12/30/2014
 * <p/>
 * Project: HCSoups
 */
public class StatManager {

    HashMap<String, Stat> stats = new HashMap<>();

    DBCollection stat = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("statistics");

    public Stat createStats(Player player) {
        if(stats.containsKey(player.getName())) {
            return stats.get(player.getName());
        } else {
            Stat stat = new Stat(player.getName(), player.getUniqueId());
            stat.save();
            stats.put(player.getName(),stat);
            return stat;
        }
    }

    public void updateStats(String name,Stat stat) {
        stats.put(name, stat);
        stat.save();
    }

    public void loadStats() {
        DBCursor cursor = stat.find();
        while(cursor.hasNext()) {
            BasicDBObject object = (BasicDBObject) cursor.next();
            Stat statss = new Stat(object.getString("name"), UUID.fromString(object.getString("uuid")));
            statss.setKills(object.getInt("kills"));
            statss.setDeaths(object.getInt("deaths"));
            statss.setKdr();
            stats.put(statss.getName(), statss);
        }
    }

    public void saveStats() {
        for(Stat stat1 : stats.values()) {
            stat1.save();
        }
    }

    public HashMap<String, Stat> getStats() {
        return stats;
    }

    private StatManager () { }

    static StatManager instance = new StatManager();

    public static StatManager getInstance() {
        return instance;
    }
}
