package net.newblood.module.modules;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Draws a wireframe box around nearby mobs (visible through walls), with
 * their name and HP floating above them.
 *
 * The box used to be drawn using each entity's raw absolute world
 * coordinates, but everything under this render hook is drawn relative to
 * the camera (RenderManager#viewerPosX/Y/Z), so the box ended up offset by
 * the camera's own position - effectively invisible except very close to
 * world origin. Every coordinate below is now translated into
 * camera-relative space before drawing, same as vanilla's own entity
 * renderer does internally.
 */
public class ESP extends Module {

	private final NumberSetting range = new NumberSetting("Range", 32.0, 8.0, 64.0, 4.0);

	public ESP() {
		super("ESP", "Highlights mobs through walls, with name + HP", Category.RENDER);
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

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		for (Entity e : nearby) {
			if (!(e instanceof EntityLivingBase) || e == mc.thePlayer) continue;
			if (e instanceof EntityPlayer) continue; // only mobs, never other player entities
			EntityLivingBase living = (EntityLivingBase) e;

			double ix = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks - camX;
			double iy = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks - camY;
			double iz = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks - camZ;
			double halfW = e.width / 2.0;

			AxisAlignedBB box = new AxisAlignedBB(ix - halfW, iy, iz - halfW, ix + halfW, iy + e.height, iz + halfW);

			GlStateManager.color(1.0F, 0.2F, 0.2F, 0.8F);
			RenderGlobal.func_181561_a(box);

			@SuppressWarnings("unchecked")
			Render<EntityLivingBase> render = mc.getRenderManager().getEntityRenderObject(living);
			if (render != null) {
				String label = living.getName() + "  " + (int) Math.ceil(living.getHealth()) + "/"
						+ (int) living.getMaxHealth() + " HP";
				render.renderLivingLabel(living, label, ix, iy, iz, 96);
			}
		}

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}
}
