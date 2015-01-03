package org.hcsoups.hardcore.entities;


import net.minecraft.server.v1_7_R4.EntityCaveSpider;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;

import java.lang.reflect.Field;

public class CustomEntityCaveSpider extends EntityCaveSpider {

	public CustomEntityCaveSpider(World arg0) {
		super(arg0);

		try {
			Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
			bField.setAccessible(true);
			Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
			cField.setAccessible(true);
			bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
			cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
		} catch (Exception exc) {
			exc.printStackTrace();
			// This means that the name of one of the fields changed names or
			// declaration and will have to be re-examined.
		}
	}

}
