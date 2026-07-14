package net.newblood.module.modules;

import net.newblood.module.Module;

public class BunnyHop extends Module {

	public BunnyHop() {
		super("BunnyHop", "Автоматически прыгать при спринте", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		if (mc.thePlayer.onGround && mc.thePlayer.isSprinting()
				&& mc.gameSettings.keyBindForward.isKeyDown()) {
			mc.thePlayer.motionY = 0.42;
		}
	}
}
