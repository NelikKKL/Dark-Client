package net.newblood.module.modules;

import net.minecraft.util.EnumParticleTypes;
import net.newblood.module.Module;

/** Purely cosmetic: spawns a ring of particles under the player each time they land. */
public class JumpCircle extends Module {

	private boolean wasOnGround = true;

	public JumpCircle() {
		super("JumpCircle", "Particle ring on landing", Category.RENDER);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.theWorld == null) return;
		boolean onGround = mc.thePlayer.onGround;
		if (onGround && !wasOnGround) {
			double cx = mc.thePlayer.posX;
			double cy = mc.thePlayer.posY;
			double cz = mc.thePlayer.posZ;
			int points = 16;
			double radius = 0.6;
			for (int i = 0; i < points; i++) {
				double angle = (2 * Math.PI / points) * i;
				double px = cx + Math.cos(angle) * radius;
				double pz = cz + Math.sin(angle) * radius;
				mc.theWorld.spawnParticle(EnumParticleTypes.CLOUD, px, cy, pz, 0.0, 0.05, 0.0);
			}
		}
		wasOnGround = onGround;
	}
}
