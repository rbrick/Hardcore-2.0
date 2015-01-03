package org.hcsoups.hardcore.mobcapture;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum SpawnEggType {
	CREEPER(50, EntityType.CREEPER, 15),  
	SKELETON(51, EntityType.SKELETON,25),  
	SPIDER(52, EntityType.SPIDER,5),  
	ZOMBIE(54, EntityType.ZOMBIE,5),  
	SLIME(55, EntityType.SLIME,5),  
	GHAST(56, EntityType.GHAST,10),  
	PIG_ZOMBIE(57, EntityType.PIG_ZOMBIE,5),  
	ENDERMAN(58, EntityType.ENDERMAN, 10),  
	CAVE_SPIDER(59, EntityType.CAVE_SPIDER,5),  
	SILVERFISH(60, EntityType.SILVERFISH,5),  
	BLAZE(61, EntityType.BLAZE, 5),  
	MAGMA_CUBE(62, EntityType.MAGMA_CUBE,5),   
	BAT(65, EntityType.BAT, 5),  
	WITCH(66, EntityType.WITCH,15),  
	PIG(90, EntityType.PIG,5),  
	SHEEP(91, EntityType.SHEEP,5),  
	COW(92, EntityType.COW,5),  
	CHICKEN(93, EntityType.CHICKEN,15),  
	SQUID(94, EntityType.SQUID,5),  
	WOLF(95, EntityType.WOLF,5),  
	MUSHROOM_COW(96, EntityType.MUSHROOM_COW, 25),  
	SNOWMAN(97, EntityType.SNOWMAN,5),  
	OCELOT(98, EntityType.OCELOT,5),  
	VILLAGER(120, EntityType.VILLAGER,35),  
	HORSE(100, EntityType.HORSE,10);

	private int id;
	private EntityType entityType;
	private int exp_cost;

	private SpawnEggType(int id, EntityType entityType, int exp_cost) {
		this.id = id;
		this.entityType = entityType;
		this.exp_cost = exp_cost;
	}

	public static SpawnEggType getByName(String name) {
		if (name == null) {
			return null;
		}
		for (SpawnEggType spawnEggType : values()) {
			if (spawnEggType.getName().equalsIgnoreCase(name)) {
				return spawnEggType;
			}
		}
		return null;
	}

	public static SpawnEggType getByEntityType(EntityType entityType) {
		if (entityType == null) {
			return null;
		}
		for (SpawnEggType spawnEggType : values()) {
			if (spawnEggType.getEntityType().equals(entityType)) {
				return spawnEggType;
			}
		}
		return null;
	}

	public int getId() {
		return this.id;
	}

	public int getCost() {
		return exp_cost;
	}

	public boolean isInstance(Entity e) {
		return e.getType().equals(getEntityType());
	}

	public String getName() {
		return this.entityType.getName();
	}

	public EntityType getEntityType() {
		return this.entityType;
	}
}
