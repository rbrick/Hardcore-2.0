package org.hcsoups.hardcore.teams;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ryan on 2/1/2015
 * <p/>
 * Project: HCSoups
 */
public class TeamConverter {

    public void convert() {
        Bukkit.broadcastMessage("§aLoading old teams...");
        TeamManager.getInstance().loadTeams();
        Bukkit.broadcastMessage("§aTeams have been loaded.");
        Bukkit.broadcastMessage("Starting Teams conversion...");
        ArrayList<TeamUUID> teams = new ArrayList<>();
        HashMap<UUID, TeamUUID> inTeam = new HashMap<>();
        for (Team t : TeamManager.getInstance().getTeams()) {
            List<UUID> members = new ArrayList<>();
            List<UUID> managers = new ArrayList<>();
            for (String mem : t.getMembers()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(mem);
                if (op == null) {
                    continue;
                }
                if (members.contains(op.getUniqueId())) {
                    continue;
                }
                members.add(op.getUniqueId());
            }

            for (String man : t.getManagers()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(man);
                if (op == null) {
                    continue;
                }

                if (members.contains(op.getUniqueId()) || managers.contains(op.getUniqueId())) {
                    continue;
                }
                managers.add(op.getUniqueId());
            }

            TeamUUID teamUUID = new TeamUUID(t.getName(), managers, members, t.getPassword());
            teamUUID.setFriendlyFire(t.isFriendlyFire());
            teamUUID.setHq(t.getHq());
            teamUUID.setRally(t.getRally());
            teamUUID.setValorPoints(t.getValorPoints());
            teams.add(teamUUID);
            for (UUID mans : teamUUID.getManagers()) {
                inTeam.put(mans, teamUUID);
            }
            for (UUID mems : teamUUID.getMembers()) {
                inTeam.put(mems, teamUUID);
            }
            Bukkit.broadcastMessage("Converted team " + teamUUID.getName() + " to UUIDs.");
            TeamManagerUUID.getInstance().saveTeam(teamUUID);
            TeamManagerUUID.getInstance().setTeams(teams); // for testing purposes.
        }

        TeamManagerUUID.getInstance().setTeams(teams);
        TeamManagerUUID.getInstance().setInTeam(inTeam);
        TeamManagerUUID.getInstance().saveInTeam();
        TeamManagerUUID.getInstance().saveTeams();


    }

}
