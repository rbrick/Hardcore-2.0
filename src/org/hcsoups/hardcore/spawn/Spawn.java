package org.hcsoups.hardcore.spawn;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by Ryan on 11/26/2014
 * <p/>
 * Project: HCSoups
 */
@Data
@AllArgsConstructor
public class Spawn {
   int radius;
   int y;
    public boolean contains(Location location) {
        return location.getBlockX() < radius+1  && location.getBlockX() > -radius-1  && location.getBlockZ() < radius+1  && location.getBlockZ() >= -radius-1;
    }

    public Location getCorner1() {
        return new Location(Bukkit.getWorld("world"), radius, y, radius);
    }

    public Location getCorner2() {
        return new Location(Bukkit.getWorld("world"), radius, y, -radius);
    }

    public Location getCorner3() {
        return new Location(Bukkit.getWorld("world"), -radius, y, radius);
    }

    public Location getCorner4() {
        return new Location(Bukkit.getWorld("world"), -radius, y, -radius);
    }
}
