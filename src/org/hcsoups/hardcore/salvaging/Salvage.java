package org.hcsoups.hardcore.salvaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Salvage implements Listener {

	int ChestMax = 8;
	int LegsMax = 7;
	int HelmMax = 5;
	int bootsMax = 4;
	int ironMax = 155;
	int pick = 3;
	int swordMax = 2;
	int hoe = 2;
	int spade = 1;
	int axe = 2;
	int barding = 3;

	private List<Material> armorMaterials = new ArrayList<Material>(Arrays.asList(new Material[] { Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_HOE, Material.IRON_SPADE, Material.IRON_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_HOE, Material.DIAMOND_SPADE, Material.DIAMOND_AXE, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.IRON_BOOTS, Material.IRON_CHESTPLATE, Material.IRON_HELMET, Material.IRON_LEGGINGS, Material.IRON_BARDING, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_LEGGINGS, Material.GOLD_BOOTS, Material.GOLD_CHESTPLATE, Material.GOLD_HELMET, Material.GOLD_LEGGINGS, Material.GOLD_BARDING, Material.DIAMOND_BOOTS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BARDING, Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.GOLD_HOE, Material.GOLD_SPADE, Material.GOLD_SWORD }));

	public boolean BlockNear(Material mat, World w, Block block, int x, int y, int z) {
		Location loc = new Location(w, block.getX() + x, block.getY() + y, block.getZ() + z);
		Block block2 = w.getBlockAt(loc);
		if (block2.getType() == mat) {
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Diamond(PlayerInteractEvent event) {
		if (!event.hasBlock()) {
			return;
		}
		if (event.getClickedBlock() == null) {
			return;
		}
		Block block = event.getClickedBlock();
		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (block.getType() == Material.DIAMOND_BLOCK) && (armorMaterials.contains(event.getPlayer().getItemInHand().getType()))) {
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			Player pl = event.getPlayer();
			if ((BlockNear(Material.FURNACE, pl.getWorld(), block, -1, 0, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 1, 0, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 0, -1)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 0, 1)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, -1, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 1, 0))) {
				ItemStack item = pl.getItemInHand();
				Material mitem = item.getType();
				System.out.println(mitem);
				double mult = 1.0D - item.getDurability() / ironMax;
				double amtIron = 0.0D;
				if (mitem == Material.DIAMOND_SWORD) {
					amtIron = Math.ceil(swordMax * mult);
				}
				if (mitem == Material.DIAMOND_BOOTS) {
					amtIron = Math.ceil(bootsMax * mult);
				}
				if (mitem == Material.DIAMOND_HELMET) {
					amtIron = Math.ceil(HelmMax * mult);
				}
				if (mitem == Material.DIAMOND_LEGGINGS) {
					amtIron = Math.ceil(LegsMax * mult);
				}
				if (mitem == Material.DIAMOND_CHESTPLATE) {
					amtIron = Math.ceil(ChestMax * mult);
				}
				if (mitem == Material.DIAMOND_HOE) {
					amtIron = Math.ceil(hoe * mult);
				}
				if (mitem == Material.DIAMOND_AXE) {
					amtIron = Math.ceil(axe * mult);
				}
				if (mitem == Material.DIAMOND_PICKAXE) {
					amtIron = Math.ceil(pick);
				}
				if (mitem == Material.DIAMOND_SPADE) {
					amtIron = Math.ceil(spade);
				}
				if (mitem == Material.DIAMOND_BARDING) {
					amtIron = Math.ceil(barding);
				}
				if (amtIron > 0.0D) {
					event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
					Inventory inv = pl.getInventory();
					inv.removeItem(new ItemStack[] { item });
					pl.getWorld().dropItem(block.getLocation(), new ItemStack(Material.DIAMOND, (int) amtIron));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Iron(PlayerInteractEvent event) {
		if (!event.hasBlock()) {
			return;
		}
		if (event.getClickedBlock() == null) {
			return;
		}
		Block block = event.getClickedBlock();
		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (block.getType() == Material.IRON_BLOCK) && (armorMaterials.contains(event.getPlayer().getItemInHand().getType()))) {
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			Player pl = event.getPlayer();
			if ((BlockNear(Material.FURNACE, pl.getWorld(), block, -1, 0, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 1, 0, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 0, -1)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 0, 1)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, -1, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 1, 0))) {
				ItemStack item = pl.getItemInHand();
				Material mitem = item.getType();
				System.out.println(mitem);
				double mult = 1.0D - item.getDurability() / ironMax;
				double amtIron = 0.0D;
				if (mitem == Material.IRON_BOOTS) {
					amtIron = Math.ceil(bootsMax * mult);
				}
				if (mitem == Material.IRON_HELMET) {
					amtIron = Math.ceil(HelmMax * mult);
				}
				if (mitem == Material.IRON_LEGGINGS) {
					amtIron = Math.ceil(LegsMax * mult);
				}
				if (mitem == Material.IRON_CHESTPLATE) {
					amtIron = Math.ceil(ChestMax * mult);
				}
				if (mitem == Material.IRON_SWORD) {
					amtIron = Math.ceil(swordMax * mult);
				}
				if (mitem == Material.IRON_HOE) {
					amtIron = Math.ceil(hoe * mult);
				}
				if (mitem == Material.IRON_AXE) {
					amtIron = Math.ceil(axe * mult);
				}
				if (mitem == Material.IRON_PICKAXE) {
					amtIron = Math.ceil(pick);
				}
				if (mitem == Material.IRON_SPADE) {
					amtIron = Math.ceil(spade);
				}
				if (mitem == Material.IRON_BARDING) {
					amtIron = Math.ceil(barding);
				}
				if (amtIron > 0.0D) {
					event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
					Inventory inv = pl.getInventory();
					inv.removeItem(new ItemStack[] { item });
					pl.getWorld().dropItem(block.getLocation(), new ItemStack(Material.IRON_INGOT, (int) amtIron));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void Gold(PlayerInteractEvent event) {
		if (!event.hasBlock()) {
			return;
		}
		if (event.getClickedBlock() == null) {
			return;
		}
		Block block = event.getClickedBlock();
		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (block.getType() == Material.GOLD_BLOCK) && (armorMaterials.contains(event.getPlayer().getItemInHand().getType()))) {
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			Player pl = event.getPlayer();
			if ((BlockNear(Material.FURNACE, pl.getWorld(), block, -1, 0, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 1, 0, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 0, -1)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 0, 1)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, -1, 0)) || (BlockNear(Material.FURNACE, pl.getWorld(), block, 0, 1, 0))) {
				ItemStack item = pl.getItemInHand();
				Material mitem = item.getType();
				System.out.println(mitem);
				double mult = 1.0D - item.getDurability() / ironMax;
				double amtIron = 0.0D;
				if (mitem == Material.GOLD_BOOTS) {
					amtIron = Math.ceil(bootsMax * mult);
				}
				if (mitem == Material.GOLD_HELMET) {
					amtIron = Math.ceil(HelmMax * mult);
				}
				if (mitem == Material.GOLD_LEGGINGS) {
					amtIron = Math.ceil(LegsMax * mult);
				}
				if (mitem == Material.GOLD_CHESTPLATE) {
					amtIron = Math.ceil(ChestMax * mult);
				}
				if (mitem == Material.GOLD_SWORD) {
					amtIron = Math.ceil(swordMax * mult);
				}
				if (mitem == Material.GOLD_HOE) {
					amtIron = Math.ceil(hoe * mult);
				}
				if (mitem == Material.GOLD_AXE) {
					amtIron = Math.ceil(axe * mult);
				}
				if (mitem == Material.GOLD_PICKAXE) {
					amtIron = Math.ceil(pick);
				}
				if (mitem == Material.GOLD_SPADE) {
					amtIron = Math.ceil(spade);
				}
				if (mitem == Material.GOLD_BARDING) {
					amtIron = Math.ceil(barding);
				}
				if (amtIron > 0.0D) {
					event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
					Inventory inv = pl.getInventory();
					inv.removeItem(new ItemStack[] { item });
					pl.getWorld().dropItem(block.getLocation(), new ItemStack(Material.GOLD_INGOT, (int) amtIron));
				}
			}
		}
	}
}
