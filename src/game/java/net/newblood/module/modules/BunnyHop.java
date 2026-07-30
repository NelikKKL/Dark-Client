package net.newblood.module.modules;

import net.newblood.module.Module;

/**
 * Automatically hops while walking forward, forcing sprint on each hop.
 *
 * The old version only ever jumped if the player was ALREADY sprinting
 * (mc.thePlayer.isSprinting()), which vanilla only turns on itself after
 * you've been holding forward for a bit with enough hunger - so most of
 * the time the condition was never true and the module did nothing. This
 * version forces sprinting itself and uses EntityPlayer#jump() (which
 * properly accounts for jump boost etc) instead of poking motionY directly.
 */
public class BunnyHop extends Module {

	public BunnyHop() {
		super("BunnyHop", "Automatically jumps while moving forward", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		boolean movingForward = mc.gameSettings.keyBindForward.isKeyDown();
		if (!movingForward || mc.thePlayer.isSneaking() || mc.thePlayer.isCollidedHorizontally
				&& !mc.thePlayer.onGround) {
			return;
		}

		if (mc.thePlayer.onGround) {
			if (!mc.thePlayer.isSprinting() && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
				mc.thePlayer.setSprinting(true);
			}
			mc.thePlayer.jump();
		}
	}
}
