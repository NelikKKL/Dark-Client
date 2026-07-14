package net.newblood.module.modules;

import net.newblood.module.Module;

public class NoFall extends Module {

	public NoFall() {
		super("NoFall", "Убирает урон от падения", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		if (mc.thePlayer.fallDistance > 2.0F && mc.thePlayer.motionY < 0.0) {
			mc.thePlayer.fallDistance = 0.0F;
		}
	}
}
