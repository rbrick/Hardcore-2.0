package org.hcsoups.hardcore.xpbottles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class XPBottles implements Listener {
	@Deprecated
	public int xpFromLevel(int currentLevel) {
		if (currentLevel >= 30) {
			return 62 + (currentLevel - 30) * 7;
		}
		if (currentLevel >= 15) {
			return 17 + (currentLevel - 15) * 3;
		}
		return 17;
	}

	/**
	 * Returns the exp in a total level.
	 * 
	 * @param level
	 * @return
	 */
	public static int levelToExp(int level) {
		if (level <= 15)
			return 17 * level;
		else if (level <= 30)
			return (3 * level * level / 2) - (59 * level / 2) + 360;
		else
			return (7 * level * level / 2) - (303 * level / 2) + 2220;
	}

	public static ItemStack xpBottles() {
		ItemStack xpBottles = new ItemStack(Material.EXP_BOTTLE);
		ItemMeta meta = xpBottles.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "XP Bottle");

		xpBottles.setItemMeta(meta);
		return xpBottles;
	}

	public static void createRecipes() {

		ShapelessRecipe xpBottle = new ShapelessRecipe(xpBottles()).addIngredient(1, Material.GLASS_BOTTLE);

		Bukkit.getServer().addRecipe(xpBottle);
	}

	public void removeRecipes() {
		Bukkit.getServer().clearRecipes();
	}

	@EventHandler
	public void onPlayerCraft(CraftItemEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getCurrentItem().equals(xpBottles())) {
			if (event.isShiftClick()) {
				event.setCancelled(true);
				return;
			}
			// p.sendMessage(ChatColor.RED +
			// Bukkit.getBukkitVersion().split("\\.")[3]);
			ItemStack experiencePotion = event.getCurrentItem();
			ItemMeta experienceMeta = experiencePotion.getItemMeta();
			List<String> stringList = new ArrayList<>();
			stringList.add("");
			int xpLevel = levelToExp(p.getLevel());
			stringList.add(ChatColor.GOLD + "Exp: " + ChatColor.WHITE + xpLevel);

			experienceMeta.setLore(stringList);
			experiencePotion.setItemMeta(experienceMeta);

			p.setExp(0.0F);
			p.setLevel(0);
		}
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void onExpSplash(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) && (p.getItemInHand().getType() == Material.EXP_BOTTLE) && (p.getItemInHand().hasItemMeta())) {
			ItemMeta meta = p.getItemInHand().getItemMeta();
			if (p.getItemInHand().getItemMeta().hasLore()) {
				if (meta.getDisplayName().equals(ChatColor.GOLD + "XP Bottle")) {
					event.setCancelled(true);
					double exp = Double.parseDouble(((String) meta.getLore().get(1)).split("�f")[1]);
					if (exp < 0.0D) {
						ItemStack temp = p.getItemInHand().clone();
						temp.setAmount(p.getItemInHand().getAmount() - 1);
						p.getInventory().remove(p.getItemInHand());

						p.getInventory().addItem(temp);
						p.updateInventory();
						return;
					}
					ItemStack temp = p.getItemInHand().clone();
					temp.setAmount(p.getItemInHand().getAmount() - 1);
					// p.getInventory().remove(p.getItemInHand());

					p.setItemInHand(temp.getAmount() <= 0 ? new ItemStack(Material.AIR) : temp);

					p.updateInventory();

					p.giveExp((int) exp);

					// p.sendMessage(ChatColor.GRAY + "You have " +
					// ChatColor.GREEN + "used" + ChatColor.GRAY +
					// " your experience bottle.");
				}
			}
		}
	}

//	public CraftPlayer getPlayerAsCraftCopy(Player p) {
//		String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
//		try {
//			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
//			return (CraftPlayer) craftPlayerClass.cast(p);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	public static int deltaLevelToExp(int level) {
		if (level <= 15)
			return 17;
		else if (level <= 30)
			return 3 * level - 31;
		else
			return 7 * level - 155;
	}
}
