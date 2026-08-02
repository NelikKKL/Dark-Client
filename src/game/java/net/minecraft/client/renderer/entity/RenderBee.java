package net.minecraft.client.renderer.entity;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.model.ModelBee;
import net.minecraft.entity.passive.EntityBee;
import net.minecraft.util.ResourceLocation;

/**+
 * ===== NewBlood: Bees & Honey content =====
 */
public class RenderBee extends RenderLiving<EntityBee> {

	private static final ResourceLocation TEX_NORMAL = new ResourceLocation("textures/entity/bee.png");
	private static final ResourceLocation TEX_ANGRY = new ResourceLocation("textures/entity/bee_angry.png");
	private static final ResourceLocation TEX_NECTAR = new ResourceLocation("textures/entity/bee_nectar.png");
	private static final ResourceLocation TEX_ANGRY_NECTAR = new ResourceLocation(
			"textures/entity/bee_angry_nectar.png");

	public RenderBee(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBee(), 0.3F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBee bee) {
		boolean angry = bee.isAngry();
		boolean nectar = bee.hasNectar();
		if (angry) {
			return nectar ? TEX_ANGRY_NECTAR : TEX_ANGRY;
		}
		return nectar ? TEX_NECTAR : TEX_NORMAL;
	}

	@Override
	protected void preRenderCallback(EntityBee bee, float partialTicks) {
		GlStateManager.scale(0.6F, 0.6F, 0.6F);
	}
}
