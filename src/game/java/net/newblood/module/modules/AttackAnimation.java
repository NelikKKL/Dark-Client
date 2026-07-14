package net.newblood.module.modules;

import net.newblood.module.Module;

/** Purely cosmetic: speeds up the arm-swing animation progress each tick. */
public class AttackAnimation extends Module {

	public AttackAnimation() {
		super("AttackAnimation", "Snappier arm-swing animation", Category.RENDER);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		if (mc.thePlayer.swingProgress > 0.0F && mc.thePlayer.swingProgress < 1.0F) {
			mc.thePlayer.swingProgress = Math.min(1.0F, mc.thePlayer.swingProgress + 0.3F);
		}
	}
}
