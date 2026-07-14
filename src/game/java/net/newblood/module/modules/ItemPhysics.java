package net.newblood.module.modules;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.newblood.module.Module;

/** Cosmetic: gives dropped item entities a little extra bounce/spin, client-side only. */
public class ItemPhysics extends Module {

	public ItemPhysics() {
		super("ItemPhysics", "Предметы подпрыгивают и вращаются заметнее", Category.RENDER);
	}

	@Override
	public void onTick() {
		if (mc.theWorld == null || mc.thePlayer == null) return;
		List<Entity> nearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer,
				mc.thePlayer.getEntityBoundingBox().expand(8.0, 4.0, 8.0));
		for (Entity e : nearby) {
			if (e instanceof EntityItem && e.onGround && Math.abs(e.motionY) < 0.01) {
				e.motionY = 0.12;
			}
		}
	}
}
