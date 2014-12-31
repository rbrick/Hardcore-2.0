package org.hcsoups.hardcore.listeners;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.hcsoups.hardcore.Hardcore;
import org.hcsoups.hardcore.stats.Stat;
import org.hcsoups.hardcore.stats.StatManager;
import org.hcsoups.hardcore.utils.annotations.Todo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Ryan on 12/30/2014
 * <p/>
 * Project: HCSoups
 */
public class DeathListener implements Listener {

    DBCollection deaths = Hardcore.getPlugin(Hardcore.class).getMongo().getCollection("deaths");


    List<Material> mats = Arrays.asList(
            // Diamond
            Material.DIAMOND_SWORD,
            Material.DIAMOND_SPADE,
            Material.DIAMOND_AXE,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_HOE,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            // Gold
            Material.GOLD_SWORD,
            Material.GOLD_SPADE,
            Material.GOLD_AXE,
            Material.GOLD_PICKAXE,
            Material.GOLD_HOE,
            Material.GOLD_HELMET,
            Material.GOLD_CHESTPLATE,
            Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS,
            // Iron
            Material.IRON_SWORD,
            Material.IRON_SPADE,
            Material.IRON_AXE,
            Material.IRON_PICKAXE,
            Material.IRON_HOE,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            // Leather
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            // ChainMail
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            // Wood
            Material.WOOD_SWORD,
            Material.WOOD_SPADE,
            Material.WOOD_AXE,
            Material.WOOD_PICKAXE,
            Material.WOOD_HOE,
            // Stone
            Material.STONE_SWORD,
            Material.STONE_SPADE,
            Material.STONE_AXE,
            Material.STONE_PICKAXE,
            Material.STONE_HOE,
            Material.BOW,
            Material.POTION
    );




    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Stat stat = StatManager.getInstance().createStats(event.getEntity());
        stat.setDeaths(stat.getDeaths() + 1);
        stat.setKdr();
        StatManager.getInstance().updateStats(event.getEntity().getName(), stat);
        logBattle(event.getEntity());


        if(event.getEntity().getKiller() != null) {
         //   Bukkit.broadcastMessage("Killer: " + event.getEntity().getKiller().getName());
            Stat stat1 = StatManager.getInstance().createStats(event.getEntity().getKiller());
            stat1.setKills(stat1.getKills() + 1);
            stat1.setKdr();
            StatManager.getInstance().updateStats(event.getEntity().getKiller().getName(), stat1);
            logBattle(event.getEntity().getKiller());
        }

    }



    @Todo("Change this method to logPlayer and make it return a DBObject. then create another method logBattle which serializes the inventory" +
          "and inserts it into the database.")
    public void logBattle(Player player) {
        BasicDBObject object = new BasicDBObject()
                .append("name", player.getName())
                .append("uuid", player.getUniqueId().toString())
                .append("time", new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date()))
                .append("health", player.getHealth())
                .append("hunger", player.getFoodLevel());

        PlayerInventory inv = player.getInventory();

        BasicDBList armor = new BasicDBList();

        // Messy and unpratical way of making sure the Armor displays in order. but y0l0
        // Helmet
        if(inv.getHelmet() != null) {
            if(mats.contains(inv.getHelmet().getType())) {
                armor.add(inv.getHelmet().getData().getItemType().getId() + "-0");
            } else {
                armor.add(inv.getHelmet().getData().getItemType().getId() + "-" + inv.getHelmet().getDurability());
            }
        } else {
            armor.add("0-0");
        }

        // Chestplate
        if(inv.getChestplate() != null) {
            if(mats.contains(inv.getChestplate().getType())) {
                armor.add(inv.getChestplate().getData().getItemType().getId() + "-0");
            } else {
                armor.add(inv.getChestplate().getData().getItemType().getId() + "-" + inv.getChestplate().getDurability());
            }
        } else {
            armor.add("0-0");
        }

        // Leggings
        if(inv.getLeggings() != null) {
            if(mats.contains(inv.getLeggings().getType())) {
                armor.add(inv.getLeggings().getData().getItemType().getId() + "-0");
            } else {
                armor.add(inv.getLeggings().getData().getItemType().getId() + "-" + inv.getLeggings().getDurability());
            }
        } else {
            armor.add("0-0");
        }

        // Boots
        if(inv.getBoots() != null) {
            if(mats.contains(inv.getBoots().getType())) {
                armor.add(inv.getBoots().getData().getItemType().getId() + "-0");
            } else {
                armor.add(inv.getBoots().getData().getItemType().getId() + "-" + inv.getBoots().getDurability());
            }
        } else {
            armor.add("0-0");
        }


        BasicDBList items = new BasicDBList();

        List<ItemStack> hotbar = Arrays.asList(player.getInventory().getContents());

        for(int i = 0; i < 9; i++) {
            if (hotbar.get(i) != null) {
                ItemStack itemStack = hotbar.get(i);
                if(mats.contains(itemStack.getType())) {
                    items.add(itemStack.getData().getItemType().getId() + "-0");
                } else {
                    items.add(itemStack.getData().getItemType().getId() + "-" + itemStack.getDurability());
                }
            } else {
                items.add("0-0");
            }
        }
        object.append("armor", armor);
        object.append("hotbar", items);
        deaths.insert(object, WriteConcern.NORMAL);
    }



}
