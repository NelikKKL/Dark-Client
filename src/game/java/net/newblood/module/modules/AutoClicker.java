package net.newblood.module.modules;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

public class AutoClicker extends Module {

	private final NumberSetting cps = new NumberSetting("Clicks/sec", 10.0, 1.0, 20.0, 1.0);
	private int cooldown;

	public AutoClicker() {
		super("AutoClicker", "Automatically clicks while the mouse button is held", Category.COMBAT);
		addSetting(cps);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.playerController == null) return;
		if (cooldown > 0) {
			cooldown--;
			return;
		}

		if (Mouse.isButtonDown(0)) { // left click held -> attack
			if (mc.objectMouseOver != null
					&& mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.ENTITY) {
				mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
				mc.thePlayer.swingItem();
			}
			cooldown = Math.max(1, (int) (20.0 / cps.getValue()));
		}
	}
}
