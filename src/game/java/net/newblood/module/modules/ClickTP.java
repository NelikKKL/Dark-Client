package net.newblood.module.modules;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.minecraft.util.MovingObjectPosition;
import net.newblood.module.Module;

/** While enabled, middle-click teleports the player to whatever their crosshair is on. */
public class ClickTP extends Module {

	private static final int MIDDLE_BUTTON = 2;
	private boolean wasDown;

	public ClickTP() {
		super("ClickTP", "Телепорт по средней кнопке мыши на точку прицела", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		boolean down = Mouse.isButtonDown(MIDDLE_BUTTON);
		if (down && !wasDown) {
			MovingObjectPosition mop = mc.objectMouseOver;
			if (mop != null && mop.hitVec != null) {
				mc.thePlayer.setPositionAndUpdate(mop.hitVec.xCoord, mop.hitVec.yCoord + 0.1, mop.hitVec.zCoord);
				mc.thePlayer.motionY = 0.0;
				mc.thePlayer.fallDistance = 0.0F;
			}
		}
		wasDown = down;
	}
}
