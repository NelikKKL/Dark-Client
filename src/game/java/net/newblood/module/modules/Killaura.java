package net.newblood.module.modules;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

public class Killaura extends Module {

	private final NumberSetting range = new NumberSetting("Дальность", 4.0, 2.0, 6.0, 0.5);
	private final NumberSetting cps = new NumberSetting("Ударов/сек", 8.0, 1.0, 20.0, 1.0);
	private int cooldown;

	public Killaura() {
		super("Killaura", "Автоатака ближайшей цели в радиусе", Category.COMBAT);
		addSetting(range);
		addSetting(cps);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.theWorld == null || mc.playerController == null) return;

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		EntityLivingBase target = findTarget();
		if (target != null) {
			mc.thePlayer.rotationYaw = rotationTo(target);
			mc.playerController.attackEntity(mc.thePlayer, target);
			mc.thePlayer.swingItem();
			cooldown = Math.max(1, (int) (20.0 / cps.getValue()));
		}
	}

	private float rotationTo(Entity target) {
		double dx = target.posX - mc.thePlayer.posX;
		double dz = target.posZ - mc.thePlayer.posZ;
		return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
	}

	private EntityLivingBase findTarget() {
		double r = range.getValue();
		List<Entity> nearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer,
				mc.thePlayer.getEntityBoundingBox().expand(r, r, r));

		EntityLivingBase best = null;
		double bestDist = Double.MAX_VALUE;
		for (Entity e : nearby) {
			if (!(e instanceof EntityLivingBase) || e == mc.thePlayer) continue;
			if (e instanceof EntityPlayer) continue; // never auto-target other real players, even locally
			EntityLivingBase living = (EntityLivingBase) e;
			if (living.isDead || living.getHealth() <= 0) continue;
			double dist = mc.thePlayer.getDistanceSqToEntity(living);
			if (dist < bestDist && dist <= r * r) {
				bestDist = dist;
				best = living;
			}
		}
		return best;
	}
}
