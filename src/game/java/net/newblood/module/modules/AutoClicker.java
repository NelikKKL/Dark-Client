package net.newblood.module.modules;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.minecraft.util.MovingObjectPosition;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * While the left mouse button is held down, automatically keeps clicking
 * until the module is turned off (or the button is released) - not just
 * a single click.
 *
 * Two independent settings, both in seconds:
 *  - Delay between clicks: time from the start of one click to the start
 *    of the next.
 *  - Click hold duration: how long each simulated click stays "pressed"
 *    for - matters for mining a block (damage is applied every tick for
 *    the whole hold window, like actually holding the button down),
 *    doesn't change anything for attacking an entity (that's an instant
 *    hit either way).
 */
public class AutoClicker extends Module {

	private static final float TICKS_PER_SECOND = 20.0F;

	private final NumberSetting delaySeconds = new NumberSetting("Delay between clicks (s)", 0.15, 0.02, 2.0, 0.01);
	private final NumberSetting holdSeconds = new NumberSetting("Click hold duration (s)", 0.05, 0.02, 1.0, 0.01);

	private boolean pressing;
	private int ticksUntilNextPhase;
	private net.minecraft.util.BlockPos miningBlockPos;
	private net.minecraft.util.EnumFacing miningFace;

	public AutoClicker() {
		super("AutoClicker", "Repeatedly clicks for you while the mouse button is held", Category.COMBAT);
		addSetting(delaySeconds);
		addSetting(holdSeconds);
	}

	@Override
	public void onDisable() {
		stopMining();
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.playerController == null) return;

		if (!Mouse.isButtonDown(0)) {
			// Button released - stop wherever we are in the cycle.
			pressing = false;
			ticksUntilNextPhase = 0;
			stopMining();
			return;
		}

		if (ticksUntilNextPhase > 0) {
			ticksUntilNextPhase--;
			if (pressing) {
				continueHold();
			}
			return;
		}

		if (!pressing) {
			// Start a new click.
			pressing = true;
			startClick();
			ticksUntilNextPhase = ticksToDuration(holdSeconds.getValue());
		} else {
			// End of hold window - release, then wait out the delay
			// before the next click starts.
			pressing = false;
			stopMining();
			ticksUntilNextPhase = ticksToDuration(delaySeconds.getValue());
		}
	}

	private void startClick() {
		MovingObjectPosition mop = mc.objectMouseOver;
		if (mop == null) return;

		if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mop.entityHit != null) {
			mc.playerController.attackEntity(mc.thePlayer, mop.entityHit);
			mc.thePlayer.swingItem();
		} else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			miningBlockPos = mop.getBlockPos();
			miningFace = mop.sideHit;
			mc.playerController.clickBlock(miningBlockPos, miningFace);
			mc.thePlayer.swingItem();
		}
	}

	private void continueHold() {
		if (miningBlockPos != null) {
			mc.playerController.onPlayerDamageBlock(miningBlockPos, miningFace);
		}
	}

	private void stopMining() {
		if (miningBlockPos != null) {
			mc.playerController.resetBlockRemoving();
			miningBlockPos = null;
		}
	}

	private static int ticksToDuration(double seconds) {
		return Math.max(1, Math.round((float) seconds * TICKS_PER_SECOND));
	}
}
