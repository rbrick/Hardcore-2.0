package org.hcsoups.hardcore.spawn;

import lombok.AllArgsConstructor;
import lombok.Data;
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
}
