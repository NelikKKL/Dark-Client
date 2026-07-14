package net.newblood.module.modules;

import java.util.Random;

import net.newblood.module.Module;

/**
 * Sends a randomized yaw/pitch to the (local integrated) server while
 * keeping the client's own camera rotation untouched, so the player's
 * own view feels normal but their in-world model looks jittery/random -
 * mostly a cosmetic novelty in singleplayer since only the player sees it.
 */
public class AntiAim extends Module {

	private final Random random = new Random();

	public AntiAim() {
		super("AntiAim", "Randomizes the visible body rotation", Category.COMBAT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		mc.thePlayer.renderYawOffset = random.nextFloat() * 360.0F - 180.0F;
	}

	@Override
	public void onDisable() {
		if (mc.thePlayer != null) {
			mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw;
		}
	}
}
