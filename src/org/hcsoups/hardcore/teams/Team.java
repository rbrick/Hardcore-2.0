package org.hcsoups.hardcore.teams;

import lombok.Data;
import org.bukkit.Location;

import java.util.List;

/**
 * This code is copyrighted by rbrick and the BreakMC Network.
 */
@Data
public  class Team {
    String name;

    List<String> managers;

    List<String> members;

    Location hq;

    Location rally;

    boolean friendlyFire;

    String password;

    int valorPoints = 0;

    public Team(String name, List<String> managers, List<String> members) {
        this(name, managers, members, "");
    }

    // home, leader, manager, member
    public Team(String name,List<String> managers, List<String> members, String password) {
        this.name = name;
        this.managers = managers;
        this.members = members;
        this.friendlyFire = false;
        this.password = password;
    }
}
