package net.newblood.module.modules;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Draws a line from the player's crosshair to nearby mobs, with a red box
 * around each one.
 *
 * Like ESP, this was drawing in absolute world coordinates while the
 * render hook expects camera-relative coordinates, so the lines/boxes
 * were drawn far away from where the camera actually is. Fixed by
 * subtracting RenderManager#viewerPosX/Y/Z, same as vanilla.
 */
public class Tracers extends Module {

	private final NumberSetting range = new NumberSetting("Range", 32.0, 8.0, 64.0, 4.0);

	public Tracers() {
		super("Tracers", "Draws lines + a box from you to nearby mobs", Category.RENDER);
		addSetting(range);
	}

	@Override
	public void onRender(float partialTicks) {
		if (mc.thePlayer == null || mc.theWorld == null) return;

		RenderManager rm = mc.getRenderManager();
		double camX = rm.viewerPosX;
		double camY = rm.viewerPosY;
		double camZ = rm.viewerPosZ;

		double r = range.getValue();
		List<Entity> nearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer,
				mc.thePlayer.getEntityBoundingBox().expand(r, r, r));

		double eyeX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks
				- camX;
		double eyeY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks
				+ mc.thePlayer.getEyeHeight() - camY;
		double eyeZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks
				- camZ;

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();

		for (Entity e : nearby) {
			if (!(e instanceof EntityLivingBase) || e == mc.thePlayer) continue;
			if (e instanceof EntityPlayer) continue;

			double ix = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks - camX;
			double iy = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks - camY;
			double iz = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks - camZ;
			double halfW = e.width / 2.0;

			GlStateManager.color(0.75F, 0.75F, 0.75F, 0.8F);
			wr.begin(1, DefaultVertexFormats.POSITION); // GL_LINES
			wr.pos(eyeX, eyeY, eyeZ).endVertex();
			wr.pos(ix, iy + e.height / 2.0, iz).endVertex();
			tess.draw();

			AxisAlignedBB box = new AxisAlignedBB(ix - halfW, iy, iz - halfW, ix + halfW, iy + e.height, iz + halfW);
			GlStateManager.color(1.0F, 0.1F, 0.1F, 0.9F);
			RenderGlobal.func_181561_a(box);
		}

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}
}
