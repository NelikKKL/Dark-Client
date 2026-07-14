package net.newblood.module.modules;

import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Reduces horizontal/vertical knockback the player receives, by scaling
 * down motion whenever it spikes right after taking a hit.
 */
public class Velocity extends Module {

	private final NumberSetting reduction = new NumberSetting("Reduction", 0.5, 0.0, 1.0, 0.05);
	private double lastHealth = -1;
	private int spikeTicks;

	public Velocity() {
		super("Velocity", "Reduces knockback taken from hits", Category.COMBAT);
		addSetting(reduction);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;

		double health = mc.thePlayer.getHealth();
		if (lastHealth != -1 && health < lastHealth) {
			spikeTicks = 6; // dampen knockback for a few ticks after getting hit
		}
		lastHealth = health;

		if (spikeTicks > 0) {
			spikeTicks--;
			double factor = 1.0 - reduction.getValue();
			mc.thePlayer.motionX *= factor;
			mc.thePlayer.motionZ *= factor;
			if (mc.thePlayer.motionY > 0) {
				mc.thePlayer.motionY *= factor;
			}
		}
	}
}
