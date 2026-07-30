package net.newblood.module.modules;

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.newblood.module.Module;

/**
 * Aim at a block: a red "glass" cube is drawn over it (purely local/visual
 * - no real block is placed, nothing is sent to the server). Left-click
 * while it's showing teleports you to that spot instead of mining/hitting
 * whatever's under the crosshair - the normal attack/mine action that a
 * left click would otherwise trigger is suppressed for a few ticks via
 * Minecraft#leftClickCounter, same mechanism vanilla itself uses to stop
 * an accidental double-hit after closing a GUI.
 */
public class ClickTP extends Module {

	private boolean wasMouseDown;

	public ClickTP() {
		super("ClickTP", "Aim at a block, left-click the red preview to teleport there", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;

		boolean mouseDown = Mouse.isButtonDown(0);
		boolean trigger = mouseDown && !wasMouseDown;
		wasMouseDown = mouseDown;

		if (trigger) {
			MovingObjectPosition mop = mc.objectMouseOver;
			if (mop != null && mop.hitVec != null && mop.getBlockPos() != null) {
				mc.thePlayer.setPositionAndUpdate(mop.hitVec.xCoord, mop.hitVec.yCoord + 0.1, mop.hitVec.zCoord);
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
		MovingObjectPosition mop = mc.objectMouseOver;
		if (mop == null || mop.getBlockPos() == null) return;

		BlockPos pos = mop.getBlockPos();
		RenderManager rm = mc.getRenderManager();
		double x = pos.getX() - rm.viewerPosX;
		double y = pos.getY() - rm.viewerPosY;
		double z = pos.getZ() - rm.viewerPosZ;

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
