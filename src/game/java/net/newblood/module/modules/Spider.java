package net.newblood.module.modules;

import net.newblood.module.Module;

public class Spider extends Module {

	public Spider() {
		super("Spider", "Взбираться по стенам при упоре в блок", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		if (mc.thePlayer.isCollidedHorizontally) {
			mc.thePlayer.motionY = 0.2;
		}
	}
}
