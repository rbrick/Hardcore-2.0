package org.hcsoups.hardcore.entities;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;

import java.util.List;
import java.util.Map;

public enum CustomEntityType {

	CREEPER("Creeper", 50, EntityType.CREEPER, EntityCreeper.class, CustomEntityCreeper.class), SKELETON("Skeleton", 51, EntityType.SKELETON, EntitySkeleton.class, CustomEntitySkeleton.class), ZOMBIE("Zombie", 54, EntityType.ZOMBIE, EntityZombie.class, CustomEntityZombie.class), SPIDER("Spider", 52, EntityType.SPIDER, EntitySpider.class, CustomEntitySpider.class), CAVE_SPIDER("CaveSpider", 59, EntityType.CAVE_SPIDER, EntityCaveSpider.class, CustomEntityCaveSpider.class), GHAST("Ghast", 56, EntityType.GHAST, EntityGhast.class, CustomEntityGhast.class), SILVERFISH("Silverfish", 60, EntityType.SILVERFISH, EntitySilverfish.class, CustomEntitySilverfish.class), BLAZE("Blaze", 61, EntityType.BLAZE, EntityBlaze.class, CustomEntityBlaze.class), ZOMBIE_PIGMAN("PigZombie", 57, EntityType.PIG_ZOMBIE, EntityPigZombie.class, CustomEntityPigZombie.class), ENDERMAN("Enderman", 58, EntityType.ENDERMAN, EntityEnderman.class, CustomEntityEnderman.class), SLIME("Slime", 55, EntityType.SLIME, EntitySlime.class, CustomEntitySlime.class), WITCH("Witch", 66, EntityType.WITCH, EntityWitch.class, CustomEntityWitch.class),
	CHICKEN("Chicken",93, EntityType.CHICKEN, EntityChicken.class, CustomChicken.class),
	COW("Cow", 92, EntityType.COW, EntityCow.class, CustomCow.class),
	PIG("Pig", 90, EntityType.PIG, EntityPig.class, CustomPig.class),
	SHEEP("Sheep", 91, EntityType.SHEEP, EntitySheep.class, CustomSheep.class);

	private String name;
	private int id;
	private EntityType entityType;
	private Class<? extends EntityInsentient> nmsClass;
	private Class<? extends EntityInsentient> customClass;

	private CustomEntityType(String name, int id, EntityType entityType, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass) {
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.nmsClass = nmsClass;
		this.customClass = customClass;
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public Class<? extends EntityInsentient> getNMSClass() {
		return nmsClass;
	}

	public Class<? extends EntityInsentient> getCustomClass() {
		return customClass;
	}

	/**
	 * Register our entities.
	 */
	public static void registerEntities() {
		for (CustomEntityType entity : values()) {
			a(entity.getCustomClass(), entity.getName(), entity.getID());
		}

		// BiomeBase#biomes became private.
		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
		} catch (Exception exc) {
			// Unable to fetch.
			return;
		}
		for (BiomeBase biomeBase : biomes) {
			if (biomeBase == null) {
				break;
			}

			// This changed names from J, K, L and M.
			for (String field : new String[] { "as", "at", "au", "av" }) {
				try {
					Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

					// Write in our custom class.
					for (BiomeMeta meta : mobList) {
						for (CustomEntityType entity : values())
							if (entity.getNMSClass().equals(meta.b)) {
								meta.b = entity.getCustomClass();
							}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Unregister our entities to prevent memory leaks. Call on disable.
	 */
	public static void unregisterEntities() {
		for (CustomEntityType entity : values()) {
			// Remove our class references.
			try {
				((Map) getPrivateStatic(EntityTypes.class, "d")).remove(entity.getCustomClass());
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				((Map) getPrivateStatic(EntityTypes.class, "f")).remove(entity.getCustomClass());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (CustomEntityType entity : values()) {
			try {
				// Unregister each entity by writing the NMS back in place of
				// the custom class.
				a(entity.getNMSClass(), entity.getName(), entity.getID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Biomes#biomes was made private so use reflection to get it.
		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
		} catch (Exception exc) {
			// Unable to fetch.
			return;
		}
		for (BiomeBase biomeBase : biomes) {
			if (biomeBase == null) {
				break;
			}

			// The list fields changed names but update the meta regardless.
			for (String field : new String[] { "as", "at", "au", "av" }) {
				try {
					Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

					// Make sure the NMS class is written back over our custom
					// class.
					for (BiomeMeta meta : mobList) {
						for (CustomEntityType entity : values())
							if (entity.getCustomClass().equals(meta.b)) {
								meta.b = entity.getNMSClass();
							}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * A convenience method.
	 * 
	 * @param clazz
	 *            The class.
	 * @param f
	 *            The string representation of the private static field.
	 * @return The object found
	 * @throws Exception
	 *             if unable to get the object.
	 */
	private static Object getPrivateStatic(Class clazz, String f) throws Exception {
		Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		return field.get(null);
	}

	/*
	 * Since 1.7.2 added a check in their entity registration, simply bypass it
	 * and write to the maps ourself.
	 */
	private static void a(Class paramClass, String paramString, int paramInt) {
		try {
			((Map) getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
			((Map) getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
			((Map) getPrivateStatic(EntityTypes.class, "e")).put(Integer.valueOf(paramInt), paramClass);
			((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramClass, Integer.valueOf(paramInt));
			((Map) getPrivateStatic(EntityTypes.class, "g")).put(paramString, Integer.valueOf(paramInt));
		} catch (Exception exc) {
			// Unable to register the new class.
		}
	}
}