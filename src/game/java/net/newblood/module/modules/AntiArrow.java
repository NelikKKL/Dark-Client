package net.newblood.module.modules;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.newblood.module.Module;

/** Sidesteps away from incoming arrows that are close and heading toward the player. */
public class AntiArrow extends Module {

	public AntiArrow() {
		super("AntiArrow", "Уклонение от стрел мобов-стрелков", Category.COMBAT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.theWorld == null) return;

		List<Entity> nearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer,
				mc.thePlayer.getEntityBoundingBox().expand(5.0, 3.0, 5.0));

		for (Entity e : nearby) {
			if (!(e instanceof EntityArrow)) continue;
			EntityArrow arrow = (EntityArrow) e;

			double dx = mc.thePlayer.posX - arrow.posX;
			double dz = mc.thePlayer.posZ - arrow.posZ;
			double dist = Math.sqrt(dx * dx + dz * dz);
			if (dist > 4.0) continue;

			// arrow moving roughly toward the player?
			double toPlayerX = dx, toPlayerZ = dz;
			double dot = arrow.motionX * toPlayerX + arrow.motionZ * toPlayerZ;
			if (dot <= 0) continue; // moving away, ignore

			// sidestep perpendicular to the arrow's travel direction
			double perpX = -arrow.motionZ;
			double perpZ = arrow.motionX;
			double len = Math.sqrt(perpX * perpX + perpZ * perpZ);
			if (len < 1.0E-4) continue;
			mc.thePlayer.motionX += (perpX / len) * 0.3;
			mc.thePlayer.motionZ += (perpZ / len) * 0.3;
			return;
		}
	}
}
