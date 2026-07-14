package net.newblood.module.modules;

import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Accelerates block-breaking progress by directly nudging
 * PlayerControllerMP#curBlockDamageMP, which was widened from private to
 * public specifically for this (no reflection - TeaVM's reflection support
 * is too limited to rely on for a browser build).
 */
public class SpeedMine extends Module {

	private final NumberSetting boost = new NumberSetting("Boost", 0.15, 0.0, 0.9, 0.05);

	public SpeedMine() {
		super("SpeedMine", "Speeds up block breaking", Category.MISC);
		addSetting(boost);
	}

	@Override
	public void onTick() {
		if (mc.playerController == null) return;
		float current = mc.playerController.curBlockDamageMP;
		if (current > 0.0F && current < 1.0F) {
			mc.playerController.curBlockDamageMP = Math.min(1.0F, current + (float) (double) boost.getValue());
		}
	}
}
