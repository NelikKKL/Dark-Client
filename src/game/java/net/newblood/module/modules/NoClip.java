package net.newblood.module.modules;

import net.newblood.module.Module;

/**
 * True noclip: the player entity's collision box is bypassed entirely
 * (Entity#noClip, checked in Entity#moveEntity), so normal WASD movement
 * slides straight through blocks instead of colliding with them.
 *
 * Vertical movement is stepped rather than a smooth float: each fresh
 * press of jump moves the player up exactly one block, each fresh press
 * of sneak moves them down exactly one block - matching the original
 * behaviour instead of vanilla creative flight's continuous rise/fall.
 */
public class NoClip extends Module {

	private boolean wasJumpDown;
	private boolean wasSneakDown;

	public NoClip() {
		super("NoClip", "Pass through blocks; jump/sneak step you up/down a block", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		if (mc.thePlayer == null) return;
		mc.thePlayer.noClip = true;
		mc.thePlayer.capabilities.allowFlying = true;
		mc.thePlayer.capabilities.isFlying = true;
		mc.thePlayer.fallDistance = 0.0F;
		wasJumpDown = false;
		wasSneakDown = false;
	}

	@Override
	public void onDisable() {
		if (mc.thePlayer == null) return;
		mc.thePlayer.noClip = false;
		mc.thePlayer.capabilities.isFlying = false;
		mc.thePlayer.fallDistance = 0.0F;
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;

		mc.thePlayer.noClip = true;
		mc.thePlayer.capabilities.allowFlying = true;
		mc.thePlayer.capabilities.isFlying = true;
		mc.thePlayer.fallDistance = 0.0F;
		// Cancel gravity/residual vertical drift every tick - height only
		// changes through the explicit 1-block steps below.
		mc.thePlayer.motionY = 0.0;

		boolean jumpDown = mc.gameSettings.keyBindJump.isKeyDown();
		boolean sneakDown = mc.gameSettings.keyBindSneak.isKeyDown();

		if (jumpDown && !wasJumpDown) {
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ);
		}
		if (sneakDown && !wasSneakDown) {
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
		}

		wasJumpDown = jumpDown;
		wasSneakDown = sneakDown;
	}
}
