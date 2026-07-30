package net.newblood.module.modules;

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MovingObjectPosition;
import net.newblood.module.Module;

/**
 * While enabled, a small target dot is drawn on whatever block face the
 * crosshair is aimed at, and either middle-click or the T key teleports
 * the player there.
 *
 * Middle-click alone used to be the only trigger, but in a browser some
 * OS/browser combinations swallow the middle mouse button for autoscroll
 * before it ever reaches the game canvas, so a keyboard trigger (T) is
 * kept as a reliable fallback.
 */
public class ClickTP extends Module {

	private static final int MIDDLE_BUTTON = 2;
	private static final int KEY_T = 20;
	private boolean wasMouseDown;
	private boolean wasKeyDown;

	public ClickTP() {
		super("ClickTP", "Middle-click (or T) teleports you to your crosshair target", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null) return;

		boolean mouseDown = Mouse.isButtonDown(MIDDLE_BUTTON);
		boolean keyDown = Keyboard.isKeyDown(KEY_T);
		boolean trigger = (mouseDown && !wasMouseDown) || (keyDown && !wasKeyDown);
		wasMouseDown = mouseDown;
		wasKeyDown = keyDown;

		if (trigger) {
			MovingObjectPosition mop = mc.objectMouseOver;
			if (mop != null && mop.hitVec != null) {
				mc.thePlayer.setPositionAndUpdate(mop.hitVec.xCoord, mop.hitVec.yCoord + 0.1, mop.hitVec.zCoord);
				mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0.0;
				mc.thePlayer.fallDistance = 0.0F;
			}
		}
	}

	@Override
	public void onRender(float partialTicks) {
		if (mc.thePlayer == null || mc.theWorld == null) return;
		MovingObjectPosition mop = mc.objectMouseOver;
		if (mop == null || mop.hitVec == null) return;

		double camX = mc.getRenderManager().viewerPosX;
		double camY = mc.getRenderManager().viewerPosY;
		double camZ = mc.getRenderManager().viewerPosZ;

		double x = mop.hitVec.xCoord - camX;
		double y = mop.hitVec.yCoord - camY;
		double z = mop.hitVec.zCoord - camZ;

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.color(0.3F, 1.0F, 0.4F, 0.9F);

		double s = 0.06;
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();
		wr.begin(1, DefaultVertexFormats.POSITION); // GL_LINES, a little 3D crosshair dot
		wr.pos(x - s, y, z).endVertex();
		wr.pos(x + s, y, z).endVertex();
		wr.pos(x, y - s, z).endVertex();
		wr.pos(x, y + s, z).endVertex();
		wr.pos(x, y, z - s).endVertex();
		wr.pos(x, y, z + s).endVertex();
		tess.draw();

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}
}
