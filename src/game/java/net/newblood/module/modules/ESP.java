package net.newblood.module.modules;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/** Draws a wireframe box around nearby living entities, visible through walls. */
public class ESP extends Module {

	private final NumberSetting range = new NumberSetting("Дальность", 32.0, 8.0, 64.0, 4.0);

	public ESP() {
		super("ESP", "Подсветка мобов сквозь стены", Category.RENDER);
		addSetting(range);
	}

	@Override
	public void onRender(float partialTicks) {
		if (mc.thePlayer == null || mc.theWorld == null) return;

		double r = range.getValue();
		List<Entity> nearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer,
				mc.thePlayer.getEntityBoundingBox().expand(r, r, r));

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		for (Entity e : nearby) {
			if (!(e instanceof EntityLivingBase) || e == mc.thePlayer) continue;
			if (e instanceof EntityPlayer) continue; // only mobs, never other player entities

			double ix = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks;
			double iy = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks;
			double iz = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks;
			double halfW = e.width / 2.0;

			AxisAlignedBB box = new AxisAlignedBB(ix - halfW, iy, iz - halfW, ix + halfW, iy + e.height, iz + halfW);

			GlStateManager.color(1.0F, 0.2F, 0.2F, 0.8F);
			RenderGlobal.func_181561_a(box);
		}

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}
}
