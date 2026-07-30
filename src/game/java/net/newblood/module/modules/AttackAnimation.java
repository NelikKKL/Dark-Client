package net.newblood.module.modules;

import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Speeds up the arm-swing animation.
 *
 * EntityLivingBase#swingProgress is a DERIVED value, recomputed every
 * tick from swingProgressInt inside updateArmSwingProgress()
 * (swingProgress = swingProgressInt / armSwingAnimationEnd). The old
 * version of this module wrote directly to swingProgress, which got
 * silently overwritten back to the vanilla value on the very same/next
 * tick - so it only ever produced a single-frame snap before reverting,
 * which is exactly what "jerky/torn" animation looks like. Advancing
 * swingProgressInt itself (before vanilla's own ++swingProgressInt runs
 * later in the same tick) actually shortens the swing, smoothly.
 */
public class AttackAnimation extends Module {

	private final NumberSetting extraSteps = new NumberSetting("Extra steps/tick", 1.0, 1.0, 3.0, 1.0);

	public AttackAnimation() {
		super("AttackAnimation", "Snappier arm-swing animation", Category.RENDER);
		addSetting(extraSteps);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		if (mc.thePlayer.isSwingInProgress) {
			mc.thePlayer.swingProgressInt += extraSteps.getValue().intValue();
		}
	}
}
