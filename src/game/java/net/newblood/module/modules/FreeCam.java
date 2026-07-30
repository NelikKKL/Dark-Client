package net.newblood.module.modules;

import net.minecraft.util.MathHelper;
import net.newblood.content.FreeCamEntity;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * A real decoupled camera: swaps Minecraft's render-view entity for a
 * lightweight, non-physical {@link FreeCamEntity} that flies wherever you
 * look, passing through blocks and never taking damage, while your actual
 * player body stays exactly where it was and just stands there.
 *
 * The previous version just gave the real player entity flight (noclip +
 * capabilities.isFlying), which is why it looked like nothing more than a
 * fly hack: the "camera" and the physical body were the same object, so
 * the body moved right along with the view instead of staying behind.
 */
public class FreeCam extends Module {

	private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.2, 4.0, 0.2);

	private FreeCamEntity cam;
	private net.minecraft.entity.Entity savedViewEntity;

	public FreeCam() {
		super("FreeCam", "Detached free-flying camera; your body stays put", Category.RENDER);
		addSetting(speed);
	}

	@Override
	public void onEnable() {
		if (mc.thePlayer == null || mc.theWorld == null) return;

		cam = new FreeCamEntity(mc.theWorld);
		cam.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
		cam.rotationYaw = cam.prevRotationYaw = mc.thePlayer.rotationYaw;
		cam.rotationPitch = cam.prevRotationPitch = mc.thePlayer.rotationPitch;
		cam.lastTickPosX = cam.prevPosX = cam.posX;
		cam.lastTickPosY = cam.prevPosY = cam.posY;
		cam.lastTickPosZ = cam.prevPosZ = cam.posZ;

		savedViewEntity = mc.getRenderViewEntity();
		mc.setRenderViewEntity(cam);

		mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
	}

	@Override
	public void onDisable() {
		mc.setRenderViewEntity(savedViewEntity != null ? savedViewEntity : mc.thePlayer);
		cam = null;
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || cam == null) return;

		// Freeze the real body in place - mouse-look still turns it (that's
		// applied to thePlayer directly by the input system regardless of
		// which entity the camera follows), but it must not translate.
		mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
		mc.thePlayer.fallDistance = 0.0F;

		// The camera looks wherever the mouse points (mouse-look always
		// updates thePlayer's rotation, so mirror it onto the camera).
		cam.prevRotationYaw = cam.rotationYaw;
		cam.prevRotationPitch = cam.rotationPitch;
		cam.rotationYaw = mc.thePlayer.rotationYaw;
		cam.rotationPitch = mc.thePlayer.rotationPitch;

		float spd = (float) (speed.getValue() * 0.5);
		float yawRad = (float) Math.toRadians(cam.rotationYaw);
		float pitchRad = (float) Math.toRadians(cam.rotationPitch);

		float forward = 0.0F;
		float strafe = 0.0F;
		float up = 0.0F;
		if (mc.gameSettings.keyBindForward.isKeyDown()) forward += 1.0F;
		if (mc.gameSettings.keyBindBack.isKeyDown()) forward -= 1.0F;
		if (mc.gameSettings.keyBindLeft.isKeyDown()) strafe += 1.0F;
		if (mc.gameSettings.keyBindRight.isKeyDown()) strafe -= 1.0F;
		if (mc.gameSettings.keyBindJump.isKeyDown()) up += 1.0F;
		if (mc.gameSettings.keyBindSneak.isKeyDown()) up -= 1.0F;

		double dx = 0.0, dy = 0.0, dz = 0.0;
		if (forward != 0.0F || strafe != 0.0F) {
			float len = MathHelper.sqrt_float(forward * forward + strafe * strafe);
			forward /= len;
			strafe /= len;
			// Move in the direction the camera is actually looking (includes
			// pitch), like a spectator/creative flycam, not a flat walk.
			dx = (-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad) * forward
					+ MathHelper.cos(yawRad) * strafe) * spd;
			dy = (-MathHelper.sin(pitchRad) * forward) * spd;
			dz = (MathHelper.cos(yawRad) * MathHelper.cos(pitchRad) * forward
					+ MathHelper.sin(yawRad) * strafe) * spd;
		}
		dy += up * spd;

		cam.prevPosX = cam.posX;
		cam.prevPosY = cam.posY;
		cam.prevPosZ = cam.posZ;
		cam.lastTickPosX = cam.posX;
		cam.lastTickPosY = cam.posY;
		cam.lastTickPosZ = cam.posZ;
		cam.setPosition(cam.posX + dx, cam.posY + dy, cam.posZ + dz);
	}
}
