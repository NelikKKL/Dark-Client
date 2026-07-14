package net.newblood.module.modules;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/** Draws a line from the player's eyes to nearby mobs. */
public class Tracers extends Module {

	private final NumberSetting range = new NumberSetting("Дальность", 32.0, 8.0, 64.0, 4.0);

	public Tracers() {
		super("Tracers", "Линии от игрока к мобам поблизости", Category.RENDER);
		addSetting(range);
	}

	@Override
	public void onRender(float partialTicks) {
		if (mc.thePlayer == null || mc.theWorld == null) return;

		double r = range.getValue();
		List<Entity> nearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer,
				mc.thePlayer.getEntityBoundingBox().expand(r, r, r));

		double eyeX = mc.thePlayer.lastTickPosX
				+ (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
		double eyeY = mc.thePlayer.lastTickPosY
				+ (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks + mc.thePlayer.getEyeHeight();
		double eyeZ = mc.thePlayer.lastTickPosZ
				+ (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 0.0F, 0.6F);

		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();
		wr.begin(1, DefaultVertexFormats.POSITION); // GL_LINES

		for (Entity e : nearby) {
			if (!(e instanceof EntityLivingBase) || e == mc.thePlayer) continue;
			if (e instanceof EntityPlayer) continue;

			double ix = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks;
			double iy = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks + e.height / 2.0;
			double iz = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks;

			wr.pos(eyeX, eyeY, eyeZ).endVertex();
			wr.pos(ix, iy, iz).endVertex();
		}

		tess.draw();

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}
}
