package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**+
 * ===== NewBlood: Bees & Honey content =====
 *
 * A simplified stand-in for vanilla's bee: no hive occupancy tracking,
 * no honey-level state, no pollination-triggered crop growth - but real
 * working behaviour:
 *  - wanders loosely around its home nest/hive
 *  - seeks out nearby flowers (any BlockFlower) and "pollinates" them
 *    (picks one, flies to it, then heads home carrying nectar)
 *  - flies home when it has nectar, or periodically anyway, or at night
 *  - stings back (and remembers who) if attacked, then goes back to
 *    normal behaviour after a while
 *
 * Movement is direct point-seeking (blend motion toward a target each
 * tick), the same technique EntityBat uses in this codebase - there's no
 * dedicated flying pathfinder (PathNavigateFlying) here to hook into.
 */
public class EntityBee extends EntityFlying {

	private static final int FLAG_ANGRY = 1;
	private static final int FLAG_NECTAR = 2;

	private BlockPos homePos;
	private BlockPos wanderTarget;
	private BlockPos flowerTarget;
	private EntityLivingBase angerTarget;
	private int angerTicks;
	private int stingCooldown;
	private int homeSearchCooldown;

	public EntityBee(World worldIn) {
		super(worldIn);
		this.setSize(0.7F, 0.6F);
		this.isImmuneToFire = false;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
	}

	public void setHomePosition(BlockPos pos) {
		this.homePos = pos;
	}

	public boolean isAngry() {
		return (this.dataWatcher.getWatchableObjectByte(16) & FLAG_ANGRY) != 0;
	}

	private void setAngry(boolean angry) {
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);
		this.dataWatcher.updateObject(16, Byte.valueOf(angry ? (byte) (b0 | FLAG_ANGRY) : (byte) (b0 & ~FLAG_ANGRY)));
	}

	public boolean hasNectar() {
		return (this.dataWatcher.getWatchableObjectByte(16) & FLAG_NECTAR) != 0;
	}

	private void setHasNectar(boolean nectar) {
		byte b0 = this.dataWatcher.getWatchableObjectByte(16);
		this.dataWatcher
				.updateObject(16, Byte.valueOf(nectar ? (byte) (b0 | FLAG_NECTAR) : (byte) (b0 & ~FLAG_NECTAR)));
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		boolean result = super.attackEntityFrom(source, amount);
		if (result && !this.worldObj.isRemote) {
			Entity attacker = source.getEntity();
			if (attacker instanceof EntityLivingBase) {
				this.angerTarget = (EntityLivingBase) attacker;
				this.angerTicks = 400;
				this.setAngry(true);
			}
		}
		return result;
	}

	private boolean stingTarget(EntityLivingBase target) {
		if (this.stingCooldown > 0) {
			return false;
		}
		this.stingCooldown = 20;
		return target.attackEntityFrom(DamageSource.causeMobDamage(this), 2.0F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.motionY *= 0.6D;
		if (this.stingCooldown > 0) {
			--this.stingCooldown;
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		if (this.worldObj.isRemote) {
			return;
		}

		if (this.homePos == null && --this.homeSearchCooldown <= 0) {
			this.homeSearchCooldown = 200;
			this.homePos = findNearestHive(this.worldObj, new BlockPos(this), 24);
		}

		if (this.angerTicks > 0) {
			--this.angerTicks;
			if (this.angerTicks <= 0 || this.angerTarget == null || !this.angerTarget.isEntityAlive()) {
				this.setAngry(false);
				this.angerTarget = null;
			}
		}

		BlockPos target;
		float speed;

		if (this.isAngry() && this.angerTarget != null && this.angerTarget.isEntityAlive()
				&& this.getDistanceSqToEntity(this.angerTarget) < 400.0D) {
			target = new BlockPos(this.angerTarget.posX, this.angerTarget.posY + this.angerTarget.height * 0.5,
					this.angerTarget.posZ);
			speed = 0.9F;
			if (this.getDistanceSqToEntity(this.angerTarget) < 3.0D) {
				this.stingTarget(this.angerTarget);
			}
		} else if (this.hasNectar()) {
			if (this.homePos != null) {
				target = this.homePos;
				speed = 0.6F;
				if (target.distanceSq(this.posX, this.posY, this.posZ) < 2.5D) {
					this.setHasNectar(false);
				}
			} else {
				target = pickWanderTarget();
				speed = 0.4F;
			}
		} else if (this.flowerTarget != null) {
			if (!isFlower(this.worldObj, this.flowerTarget) || this.ticksExisted % 1200 == 0) {
				this.flowerTarget = null;
				target = pickWanderTarget();
			} else {
				target = this.flowerTarget.up();
				if (target.distanceSq(this.posX, this.posY, this.posZ) < 1.0D) {
					this.setHasNectar(true);
					this.flowerTarget = null;
				}
			}
			speed = 0.4F;
		} else if (!this.worldObj.isDaytime() && this.homePos != null) {
			target = this.homePos;
			speed = 0.5F;
			if (target.distanceSq(this.posX, this.posY, this.posZ) < 2.5D) {
				// "enter" the nest for the night - simplification: just
				// despawn, matching not having real hive occupancy state.
				this.setDead();
				return;
			}
		} else {
			if (this.rand.nextInt(30) == 0) {
				this.flowerTarget = findNearestFlower(this.worldObj, new BlockPos(this), 8);
			}
			target = pickWanderTarget();
			speed = 0.35F;
		}

		flyToward(target, speed);
	}

	private BlockPos pickWanderTarget() {
		BlockPos center = this.homePos != null ? this.homePos : new BlockPos(this);
		if (this.wanderTarget == null || this.rand.nextInt(30) == 0
				|| this.wanderTarget.distanceSq(this.posX, this.posY, this.posZ) < 4.0D) {
			this.wanderTarget = center.add(this.rand.nextInt(11) - 5, this.rand.nextInt(7) - 3,
					this.rand.nextInt(11) - 5);
		}
		return this.wanderTarget;
	}

	private void flyToward(BlockPos target, float speedFactor) {
		double dx = (double) target.getX() + 0.5D - this.posX;
		double dy = (double) target.getY() + 0.5D - this.posY;
		double dz = (double) target.getZ() + 0.5D - this.posZ;
		this.motionX += (Math.signum(dx) * 0.5D * (double) speedFactor - this.motionX) * 0.1D;
		this.motionY += (Math.signum(dy) * 0.7D * (double) speedFactor - this.motionY) * 0.1D;
		this.motionZ += (Math.signum(dz) * 0.5D * (double) speedFactor - this.motionZ) * 0.1D;

		float f = (float) (MathHelper.func_181159_b(this.motionZ, this.motionX) * 180.0D / Math.PI) - 90.0F;
		this.rotationYaw += MathHelper.wrapAngleTo180_float(f - this.rotationYaw) * 0.2F;
		this.moveForward = 0.5F;
		this.moveEntityWithHeading(0.0F, this.moveForward);
	}

	private static boolean isFlower(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block instanceof BlockFlower;
	}

	private static BlockPos findNearestFlower(World world, BlockPos center, int radius) {
		BlockPos best = null;
		double bestDist = Double.MAX_VALUE;
		for (int x = -radius; x <= radius; ++x) {
			for (int y = -3; y <= 3; ++y) {
				for (int z = -radius; z <= radius; ++z) {
					BlockPos p = center.add(x, y, z);
					if (isFlower(world, p)) {
						double d = p.distanceSq(center.getX(), center.getY(), center.getZ());
						if (d < bestDist) {
							bestDist = d;
							best = p;
						}
					}
				}
			}
		}
		return best;
	}

	private static BlockPos findNearestHive(World world, BlockPos center, int radius) {
		BlockPos best = null;
		double bestDist = Double.MAX_VALUE;
		for (int x = -radius; x <= radius; ++x) {
			for (int y = -radius; y <= radius; ++y) {
				for (int z = -radius; z <= radius; ++z) {
					BlockPos p = center.add(x, y, z);
					Block block = world.getBlockState(p).getBlock();
					if (block == Blocks.bee_nest || block == Blocks.beehive) {
						double d = p.distanceSq(center.getX(), center.getY(), center.getZ());
						if (d < bestDist) {
							bestDist = d;
							best = p;
						}
					}
				}
			}
		}
		return best;
	}

	@Override
	public boolean getCanSpawnHere() {
		return true;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		if (this.homePos != null) {
			tag.setInteger("HiveX", this.homePos.getX());
			tag.setInteger("HiveY", this.homePos.getY());
			tag.setInteger("HiveZ", this.homePos.getZ());
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		if (tag.hasKey("HiveX")) {
			this.homePos = new BlockPos(tag.getInteger("HiveX"), tag.getInteger("HiveY"), tag.getInteger("HiveZ"));
		}
	}

	@Override
	protected String getHurtSound() {
		return "damage.hit";
	}

	@Override
	protected String getDeathSound() {
		return null;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
}
