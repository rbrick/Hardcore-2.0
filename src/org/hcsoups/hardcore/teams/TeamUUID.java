package org.hcsoups.hardcore.teams;

import lombok.Data;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
@Data
public class TeamUUID {
    String name;

    List<UUID> managers;

    List<UUID> members;

    Location hq;

    Location rally;

    boolean friendlyFire;

    String password;

    int valorPoints = 0;

    public TeamUUID(String name, List<UUID> managers, List<UUID> members) {
        this(name, managers, members, "");
    }

    // home, leader, manager, member
    public TeamUUID(String name, List<UUID> managers, List<UUID> members, String password) {
        this.name = name;
        this.managers = managers;
        this.members = members;
        this.friendlyFire = false;
        this.password = password;
    }
}
