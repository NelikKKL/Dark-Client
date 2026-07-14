package net.newblood.module.modules;

import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Simplified FreeCam: saves the player's real position, then lets them fly
 * around freely (noclip + no gravity) to scout the area. On disable, the
 * player is teleported back to where they actually were.
 *
 * Note: this is NOT a fully decoupled camera entity (that would need a
 * separate fake view-entity swapped in via Minecraft#setRenderViewEntity,
 * a bigger follow-up piece) - the player's real entity temporarily moves,
 * then snaps back. Good enough for scouting a singleplayer world, but
 * nearby mobs will briefly react to the body actually being there.
 */
public class FreeCam extends Module {

	private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.2, 4.0, 0.2);

	private double savedX, savedY, savedZ;
	private float savedYaw, savedPitch;
	private boolean savedNoClip;
	private boolean savedFlying, savedAllowFlying;

	public FreeCam() {
		super("FreeCam", "Free camera for scouting the area", Category.RENDER);
		addSetting(speed);
	}

	@Override
	public void onEnable() {
		if (mc.thePlayer == null) return;
		savedX = mc.thePlayer.posX;
		savedY = mc.thePlayer.posY;
		savedZ = mc.thePlayer.posZ;
		savedYaw = mc.thePlayer.rotationYaw;
		savedPitch = mc.thePlayer.rotationPitch;
		savedNoClip = mc.thePlayer.noClip;
		savedFlying = mc.thePlayer.capabilities.isFlying;
		savedAllowFlying = mc.thePlayer.capabilities.allowFlying;

		mc.thePlayer.noClip = true;
		mc.thePlayer.capabilities.allowFlying = true;
		mc.thePlayer.capabilities.isFlying = true;
	}

	@Override
	public void onDisable() {
		if (mc.thePlayer == null) return;
		mc.thePlayer.setPositionAndRotation(savedX, savedY, savedZ, savedYaw, savedPitch);
		mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
		mc.thePlayer.noClip = savedNoClip;
		mc.thePlayer.capabilities.isFlying = savedFlying;
		mc.thePlayer.capabilities.allowFlying = savedAllowFlying;
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;
		mc.thePlayer.capabilities.isFlying = true;
		mc.thePlayer.capabilities.setFlySpeed((float) (speed.getValue() * 0.05));
		mc.thePlayer.fallDistance = 0.0F;
	}
}
