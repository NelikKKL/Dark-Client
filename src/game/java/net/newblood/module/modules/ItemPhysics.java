package net.newblood.module.modules;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.newblood.module.Module;

/**
 * Vanilla dropped-item physics (falling under gravity, coming to rest on
 * a surface, and merging same-type stacks that end up close together -
 * see EntityItem#onUpdate / EntityItem#searchForOtherItemsNearby) already
 * work correctly by themselves; nothing in this codebase disables them.
 *
 * The previous version of this module was the actual bug: every tick it
 * gave every item at rest (onGround, near-zero motionY) a fresh upward
 * velocity, which launched items back into the air forever and stopped
 * them from ever truly settling - items looked like they had NO physics
 * because this module kept fighting the physics that already worked.
 *
 * This version only gives freshly-spawned items (dropped a moment ago) a
 * small randomized toss so they don't all land in a single stack point,
 * then leaves gravity/resting/merging entirely to vanilla.
 */
public class ItemPhysics extends Module {

	public ItemPhysics() {
		super("ItemPhysics", "Dropped items scatter slightly instead of landing in one spot", Category.RENDER);
	}

	@Override
	public void onTick() {
		if (mc.theWorld == null || mc.thePlayer == null) return;
		List<Entity> nearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer,
				mc.thePlayer.getEntityBoundingBox().expand(8.0, 4.0, 8.0));
		for (Entity e : nearby) {
			if (e instanceof EntityItem && e.ticksExisted <= 1) {
				EntityItem item = (EntityItem) e;
				item.motionX += (Math.random() - Math.random()) * 0.05;
				item.motionZ += (Math.random() - Math.random()) * 0.05;
			}
		}
	}
}
