package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

/**+
 * ===== NewBlood: Bees & Honey content =====
 * Simple stand-in bee model (body/head/stinger/wings) - not a pixel-exact
 * recreation of vanilla's box layout, but recognizably bee-shaped and
 * uses the real bee.png texture.
 */
public class ModelBee extends ModelBase {

	private ModelRenderer body;
	private ModelRenderer head;
	private ModelRenderer stinger;
	private ModelRenderer rightWing;
	private ModelRenderer leftWing;

	public ModelBee() {
		this.textureWidth = 64;
		this.textureHeight = 64;

		this.body = new ModelRenderer(this, 0, 0);
		this.body.addBox(-4.0F, -3.0F, -6.0F, 8, 6, 12);
		this.body.setRotationPoint(0.0F, 19.0F, 0.0F);

		this.head = new ModelRenderer(this, 0, 20);
		this.head.addBox(-2.5F, -2.5F, -2.0F, 5, 5, 3);
		this.head.setRotationPoint(0.0F, -1.0F, -6.5F);
		this.body.addChild(this.head);

		this.stinger = new ModelRenderer(this, 26, 0);
		this.stinger.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 2);
		this.stinger.setRotationPoint(0.0F, 0.0F, 6.0F);
		this.body.addChild(this.stinger);

		this.rightWing = new ModelRenderer(this, 0, 36);
		this.rightWing.addBox(-6.0F, 0.0F, 0.0F, 6, 1, 5);
		this.rightWing.setRotationPoint(-1.0F, -3.0F, -2.0F);
		this.body.addChild(this.rightWing);

		this.leftWing = new ModelRenderer(this, 0, 36);
		this.leftWing.mirror = true;
		this.leftWing.addBox(0.0F, 0.0F, 0.0F, 6, 1, 5);
		this.leftWing.setRotationPoint(1.0F, -3.0F, -2.0F);
		this.body.addChild(this.leftWing);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		this.body.render(f5);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, Entity entity) {
		this.head.rotateAngleY = netHeadYaw / 57.295776F;
		this.head.rotateAngleX = headPitch / 57.295776F;

		float flap = MathHelper.cos(ageInTicks * 2.7F) * 0.9F;
		this.rightWing.rotateAngleY = flap;
		this.leftWing.rotateAngleY = -flap;
	}
}
