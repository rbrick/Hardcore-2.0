package org.hcsoups.hardcore.warps;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

/**
 * Created by Ryan on 11/22/2014
 * <p/>
 * Project: HCSoups
 */
@Data
@AllArgsConstructor
public class Warp {
    String name;
    Location location;
}
