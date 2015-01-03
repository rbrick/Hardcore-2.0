package org.hcsoups.hardcore.mobcapture;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MobCapture implements Listener {

	@EventHandler
	public void onCap(EntityDamageByEntityEvent e) {
		  if(!e.isCancelled()) {
		if ((e.getDamager() instanceof Egg)) {

			Egg egg = (Egg) e.getDamager();
			Player egg1 = (Player) egg.getShooter();

			SpawnEggType spawnEggType = SpawnEggType.getByEntityType(e.getEntity().getType());
			if (spawnEggType == null) {
				egg1.sendMessage(ChatColor.RED + "You cannot capture this type of mob!");
				return;
			}
			if (egg1.getLevel() < spawnEggType.getCost() && !e.getEntityType().equals(EntityType.MUSHROOM_COW)) {
				egg1.sendMessage(ChatColor.RED + "You need " + spawnEggType.getCost() + " xp levels to capture this mob!");
			} else {
				if (e.getEntity() instanceof Ageable) {
					Ageable ageable = (Ageable) e.getEntity();
					if (!ageable.isAdult()) {
						return;
					}
				}
//				if (e.getEntity() instanceof Villager) {
//					Villager v = (Villager) e.getEntity();
//					if (CombatLog.items.containsKey(v)) {
//						return;
//					}
//				}
				// Random rand = new Random();
				// // Need to work backwards, chance is chance to fail.
				// int chance;
				// if(egg1.getLevel() <= 10) {
				// // 1 in 100
				// chance = 100;
				// }
				//
				// // 10 - 1 in 100
				// // 15 - 25 in 100
				// // 20 - 50 in 100
				// // 25 - 100 in 100
				// if(egg1.getLevel() <= 15) {
				//
				// }

				if (egg1.getLevel() >= 25) {
					/**
					 * Monster = All Hostilemobs
					 */
					if (e.getEntity() instanceof Monster || e.getEntity() instanceof Animals && !e.getEntity().getType().equals(EntityType.MUSHROOM_COW) && !e.getEntityType().equals(EntityType.VILLAGER)) {
						e.getEntity().remove();
						Location location = e.getEntity().getLocation();
						World world = location.getWorld();
						ItemStack item = new ItemStack(Material.MONSTER_EGG, 1, (short) spawnEggType.getId());
						world.dropItem(location, item);
						egg1.sendMessage(ChatColor.RED + "Capturing this '" + spawnEggType.getName() + "' has drained 0 xp levels from you.");
						return;
					}
				}
				if (e.getEntity().getType().equals(EntityType.MUSHROOM_COW)) {
					Random rand = new Random();
					// 50 + (egg.getLevel() - 25)*5;
					if (egg1.getLevel() < 25) {
						egg1.setLevel(0);
						egg1.setExp(0);
						egg1.sendMessage("�cFailed to capture this mob!");
						return;
					}
					int chance = egg1.getLevel() == 25 ? 50 : (egg1.getLevel() >= 35 ? 100 : (egg1.getLevel() - 25) * 5);
					if (chance == 100) {
						e.getEntity().remove();
						Location location = e.getEntity().getLocation();
						World world = location.getWorld();
						ItemStack item = new ItemStack(Material.MONSTER_EGG, 1, (short) spawnEggType.getId());
						world.dropItem(location, item);
						egg1.setLevel(0);
						egg1.setExp(0);
						egg1.sendMessage(ChatColor.RED + "Capturing this '" + spawnEggType.getName() + "' has drained all your xp levels from you.");
						return;
					} else {
						int realchance = chance == 50 ? 50 : 100 / chance;
						System.out.println("" + realchance);
						boolean success = rand.nextInt(realchance) == 0;
						if (!success) {
							egg1.setLevel(0);
							egg1.setExp(0);

							egg1.sendMessage("�cFailed to capture this mob!");

							return;
						} else {
							egg1.setLevel(0);
							egg1.setExp(0);
							egg1.sendMessage(ChatColor.RED + "Capture success!");
							e.getEntity().remove();
							Location location = e.getEntity().getLocation();
							World world = location.getWorld();
							egg1.sendMessage(realchance + "");
							ItemStack item = new ItemStack(Material.MONSTER_EGG, 1, (short) spawnEggType.getId());
							world.dropItem(location, item);
							return;
						}
					}

				}

				e.getEntity().remove();
				Location location = e.getEntity().getLocation();
				World world = location.getWorld();
				ItemStack item = new ItemStack(Material.MONSTER_EGG, 1, (short) spawnEggType.getId());
				world.dropItem(location, item);
				egg1.setLevel(egg1.getLevel() - spawnEggType.getCost());
				egg1.sendMessage(ChatColor.RED + "Capturing this '" + spawnEggType.getName() + "' has drained " + spawnEggType.getCost() + " xp levels from you.");

			}
		  }
		}
	}
}
