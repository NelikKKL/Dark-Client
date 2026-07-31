package net.newblood.module.modules;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.newblood.module.Module;

/**
 * Aim anywhere within render distance (not just normal interaction reach):
 * a red "glass" cube is drawn over the farthest point your camera can
 * actually see along that line - either the first solid block hit, or,
 * if you're looking at open sky/across a gap with nothing solid in the
 * way, the farthest point still within render distance. Purely local/
 * visual - no real block is placed, nothing is sent to the server.
 *
 * Left-click while it's showing teleports you to that spot instead of
 * mining/hitting whatever's under the crosshair - the normal attack/mine
 * action that a left click would otherwise trigger is suppressed for a
 * few ticks via Minecraft#leftClickCounter, same mechanism vanilla itself
 * uses to stop an accidental double-hit after closing a GUI.
 */
public class ClickTP extends Module {

	private boolean wasMouseDown;

	public ClickTP() {
		super("ClickTP", "Aim anywhere in render distance, left-click the red preview to teleport there",
				Category.MOVEMENT);
	}

	/** Farthest visible point along the crosshair, out to render distance. */
	private Vec3 findTarget(float partialTicks) {
		if (mc.thePlayer == null || mc.theWorld == null) return null;

		Vec3 eye = mc.thePlayer.getPositionEyes(partialTicks);
		Vec3 look = mc.thePlayer.getLook(partialTicks);
		double maxDist = Math.max(16, mc.gameSettings.renderDistanceChunks * 16);
		Vec3 far = eye.addVector(look.xCoord * maxDist, look.yCoord * maxDist, look.zCoord * maxDist);

		MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(eye, far, false, true, false);
		if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && hit.hitVec != null) {
			return hit.hitVec;
		}
		// Nothing solid in the way - target the farthest point still
		// within render distance instead of disappearing.
		return far;
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;

		boolean mouseDown = Mouse.isButtonDown(0);
		boolean trigger = mouseDown && !wasMouseDown;
		wasMouseDown = mouseDown;

		if (trigger) {
			Vec3 target = findTarget(1.0F);
			if (target != null) {
				mc.thePlayer.setPositionAndUpdate(target.xCoord, target.yCoord + 0.1, target.zCoord);
				mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
				mc.thePlayer.fallDistance = 0.0F;
				// Swallow the mining/attack action this same click would
				// otherwise trigger.
				mc.leftClickCounter = 10;
			}
		}
	}

	@Override
	public void onRender(float partialTicks) {
		if (mc.thePlayer == null || mc.theWorld == null) return;
		Vec3 target = findTarget(partialTicks);
		if (target == null) return;

		RenderManager rm = mc.getRenderManager();
		double x = Math.floor(target.xCoord) - rm.viewerPosX;
		double y = Math.floor(target.yCoord) - rm.viewerPosY;
		double z = Math.floor(target.zCoord) - rm.viewerPosZ;

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.color(0.85F, 0.1F, 0.1F, 0.45F); // red "glass"

		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();
		wr.begin(7, DefaultVertexFormats.POSITION); // GL_QUADS, a plain solid cube
		// -X / +X
		wr.pos(x, y, z).endVertex();
		wr.pos(x, y, z + 1).endVertex();
		wr.pos(x, y + 1, z + 1).endVertex();
		wr.pos(x, y + 1, z).endVertex();
		wr.pos(x + 1, y, z).endVertex();
		wr.pos(x + 1, y + 1, z).endVertex();
		wr.pos(x + 1, y + 1, z + 1).endVertex();
		wr.pos(x + 1, y, z + 1).endVertex();
		// -Y / +Y
		wr.pos(x, y, z).endVertex();
		wr.pos(x + 1, y, z).endVertex();
		wr.pos(x + 1, y, z + 1).endVertex();
		wr.pos(x, y, z + 1).endVertex();
		wr.pos(x, y + 1, z).endVertex();
		wr.pos(x, y + 1, z + 1).endVertex();
		wr.pos(x + 1, y + 1, z + 1).endVertex();
		wr.pos(x + 1, y + 1, z).endVertex();
		// -Z / +Z
		wr.pos(x, y, z).endVertex();
		wr.pos(x, y + 1, z).endVertex();
		wr.pos(x + 1, y + 1, z).endVertex();
		wr.pos(x + 1, y, z).endVertex();
		wr.pos(x, y, z + 1).endVertex();
		wr.pos(x + 1, y, z + 1).endVertex();
		wr.pos(x + 1, y + 1, z + 1).endVertex();
		wr.pos(x, y + 1, z + 1).endVertex();
		tess.draw();

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}
}
