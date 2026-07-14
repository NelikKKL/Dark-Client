package net.newblood.module.modules;

import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

public class Fly extends Module {

	private final NumberSetting speed = new NumberSetting("Speed", 0.6, 0.1, 3.0, 0.05);
	private boolean prevAllowFlying;
	private boolean prevFlying;

	public Fly() {
		super("Fly", "Free flight like in creative mode", Category.MOVEMENT);
		addSetting(speed);
	}

	@Override
	public void onEnable() {
		if (mc.thePlayer == null) return;
		prevAllowFlying = mc.thePlayer.capabilities.allowFlying;
		prevFlying = mc.thePlayer.capabilities.isFlying;
		mc.thePlayer.capabilities.allowFlying = true;
		mc.thePlayer.capabilities.isFlying = true;
	}

	@Override
	public void onDisable() {
		if (mc.thePlayer == null) return;
		mc.thePlayer.capabilities.isFlying = prevFlying;
		mc.thePlayer.capabilities.allowFlying = prevAllowFlying && mc.thePlayer.capabilities.allowFlying;
		mc.thePlayer.capabilities.isFlying = false;
		if (!prevAllowFlying) {
			mc.thePlayer.capabilities.allowFlying = false;
		}
		mc.thePlayer.fallDistance = 0.0F;
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		mc.thePlayer.capabilities.isFlying = true;
		mc.thePlayer.capabilities.setFlySpeed((float) (speed.getValue() * 0.05));
		mc.thePlayer.fallDistance = 0.0F;
	}
}
