package net.newblood.content;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**+
 * A bare-bones Entity that exists only to be handed to
 * Minecraft#setRenderViewEntity as a free-flying camera. It is never
 * spawned into the world (not added to World#loadedEntityList), so it
 * has no collision, takes no damage/fall damage, and mobs never react
 * to it - it's purely a viewpoint the renderer reads position/rotation
 * from.
 */
public class FreeCamEntity extends Entity {

	public FreeCamEntity(World world) {
		super(world);
		this.noClip = true;
		this.width = 0.01F;
		this.height = 0.01F;
		this.setSize(0.01F, 0.01F);
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean isEntityInvulnerable(net.minecraft.util.DamageSource source) {
		return true;
	}
}
