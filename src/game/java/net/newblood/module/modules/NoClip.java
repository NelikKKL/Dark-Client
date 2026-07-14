package net.newblood.module.modules;

import net.newblood.module.Module;

public class NoClip extends Module {

	public NoClip() {
		super("NoClip", "Pass through blocks", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		if (mc.thePlayer != null) {
			mc.thePlayer.noClip = true;
			mc.thePlayer.capabilities.allowFlying = true;
			mc.thePlayer.capabilities.isFlying = true;
		}
	}

	@Override
	public void onDisable() {
		if (mc.thePlayer != null) {
			mc.thePlayer.noClip = false;
			mc.thePlayer.capabilities.isFlying = false;
		}
	}

	@Override
	public void onTick() {
		if (mc.thePlayer != null) {
			mc.thePlayer.noClip = true;
			mc.thePlayer.fallDistance = 0.0F;
		}
	}
}
