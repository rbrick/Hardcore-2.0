package org.hcsoups.hardcore.combattag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.hcsoups.hardcore.Hardcore;

import java.util.HashMap;


public class CombatTag implements Listener {
    private HashMap<String, Integer> inCombat = new HashMap<String, Integer>();
    private HashMap<String, BukkitRunnable> inCombatTag = new HashMap<String, BukkitRunnable>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            System.out.println("Event is cancelled. Ignoring...");
            return;
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Player damaged = (Player) e.getEntity();
            final Player damager = (Player) e.getDamager();
            if(!inCombat.containsKey(damaged.getName())) {
                damaged.sendMessage(ChatColor.RED + "You are now in combat!");
                final Scoreboard scoreboard = damaged.getScoreboard();
                Objective objective = scoreboard.getObjective("ct") == null ? scoreboard.registerNewObjective("ct", "dummy") : scoreboard.getObjective("ct");
                objective.setDisplayName("&6Timers");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                final Score combatTag = objective.getScore("&aCombat Tag");
                combatTag.setScore(60);
                // damaged.setScoreboard(scoreboard);
                inCombatTag.put(damaged.getName(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (inCombat.get(damaged.getName()) != null) {
                            if (inCombat.get(damaged.getName()) > 0) {
                                inCombat.put(damaged.getName(), inCombat.get(damaged.getName()) - 1);
                                combatTag.setScore(inCombat.get(damaged.getName()));
                            } else {
                                inCombat.remove(damaged.getName());
                                damaged.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                scoreboard.resetScores("&aCombat Tag");
                                this.cancel();
                                inCombatTag.remove(damager.getName());
                            }
                        }
                    }
                });
                inCombatTag.get(damaged.getName()).runTaskTimerAsynchronously(Hardcore.getPlugin(Hardcore.class), 20L, 20L);
            }
            if(!inCombat.containsKey(damager.getName())) {
                damager.sendMessage(ChatColor.RED + "You are now in combat!");
                final Scoreboard scoreboard2 = damager.getScoreboard();
                Objective objective2 = scoreboard2.getObjective("ct") == null ? scoreboard2.registerNewObjective("ct", "dummy") : scoreboard2.getObjective("ct");
                objective2.setDisplayName("&6Timers");
                objective2.setDisplaySlot(DisplaySlot.SIDEBAR);
                final Score combatTag2 = objective2.getScore("&aCombat Tag");
                combatTag2.setScore(60);
            //    damager.setScoreboard(scoreboard2);
                inCombatTag.put(damager.getName(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (inCombat.get(damager.getName()) != null) {
                            if (inCombat.get(damager.getName()) > 0) {
                                inCombat.put(damager.getName(), inCombat.get(damager.getName()) - 1);
                                combatTag2.setScore(inCombat.get(damager.getName()));

                            } else {
                                inCombat.remove(damager.getName());
                                damager.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                scoreboard2.resetScores("&aCombat Tag");
                                this.cancel();
                                inCombatTag.remove(damager.getName());
                            }
                        }
                    }
                });
                inCombatTag.get(damager.getName()).runTaskTimerAsynchronously(Hardcore.getPlugin(Hardcore.class), 20L, 20L);
            }
            inCombat.put(damaged.getName(), 60);
            inCombat.put(damager.getName(), 60);
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
            final Player damaged = (Player) e.getEntity();
            Projectile projectile = (Projectile) e.getDamager();


            if (projectile.getShooter() instanceof Player) {
                final Player damager = (Player) projectile.getShooter();

                 if (damager.equals(damaged)) {
                     return;
                 }
                
                if (!inCombat.containsKey(damaged.getName())) {
                    damaged.sendMessage(ChatColor.RED + "You are now in combat!");
                    final Scoreboard scoreboard = damaged.getScoreboard();
                    Objective objective = scoreboard.getObjective("ct") == null ? scoreboard.registerNewObjective("ct", "dummy") : scoreboard.getObjective("ct");
                    objective.setDisplayName("&6Timers");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    final Score combatTag = objective.getScore("&aCombat Tag");
                    combatTag.setScore(60);
                    // damaged.setScoreboard(scoreboard);
                    inCombatTag.put(damaged.getName(), new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (inCombat.get(damaged.getName()) != null) {
                                if (inCombat.get(damaged.getName()) > 0) {
                                    inCombat.put(damaged.getName(), inCombat.get(damaged.getName()) - 1);
                                    combatTag.setScore(inCombat.get(damaged.getName()));
                                } else {
                                    inCombat.remove(damaged.getName());
                                    damaged.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                    scoreboard.resetScores("&aCombat Tag");
                                    this.cancel();
                                    inCombatTag.remove(damager.getName());
                                }
                            }
                        }
                    });
                    inCombatTag.get(damaged.getName()).runTaskTimerAsynchronously(Hardcore.getPlugin(Hardcore.class), 20L, 20L);
                }
                if (!inCombat.containsKey(damager.getName())) {
                    damager.sendMessage(ChatColor.RED + "You are now in combat!");
                    final Scoreboard scoreboard2 = damager.getScoreboard();
                    Objective objective2 = scoreboard2.getObjective("ct") == null ? scoreboard2.registerNewObjective("ct", "dummy") : scoreboard2.getObjective("ct");
                    objective2.setDisplayName("&6Timers");
                    objective2.setDisplaySlot(DisplaySlot.SIDEBAR);
                    final Score combatTag2 = objective2.getScore("&aCombat Tag");
                    combatTag2.setScore(60);
                    //    damager.setScoreboard(scoreboard2);
                    inCombatTag.put(damager.getName(), new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (inCombat.get(damager.getName()) != null) {
                                if (inCombat.get(damager.getName()) > 0) {
                                    inCombat.put(damager.getName(), inCombat.get(damager.getName()) - 1);
                                    combatTag2.setScore(inCombat.get(damager.getName()));

                                } else {
                                    inCombat.remove(damager.getName());
                                    damager.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                    scoreboard2.resetScores("&aCombat Tag");
                                    this.cancel();
                                    inCombatTag.remove(damager.getName());
                                }
                            }
                        }
                    });
                    inCombatTag.get(damager.getName()).runTaskTimerAsynchronously(Hardcore.getPlugin(Hardcore.class), 20L, 20L);
                }
                inCombat.put(damaged.getName(), 60);
                inCombat.put(damager.getName(), 60);
            }
        }




    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String name = p.getName();

        if(inCombat.containsKey(p.getName()) && inCombatTag.containsKey(name)) {
            if(p.hasPermission("hcsoups.combattag.bypass")) {
                return;
            }

           p.setHealth(0);
           inCombat.remove(p.getName());
           inCombatTag.get(name).cancel();
           inCombatTag.remove(name);
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        e.getPlayer().setScoreboard(scoreboard);
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (inCombat.containsKey(event.getEntity().getName()) && inCombatTag.containsKey(event.getEntity().getName())) {
            inCombat.remove(event.getEntity().getName());
            inCombatTag.get(event.getEntity().getName()).cancel();
            inCombatTag.remove(event.getEntity().getName());
            event.getEntity().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }
    }
}
