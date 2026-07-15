package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**+
 * Cherry Wood / Cherry Grove content added for NewBlood.
 * Standalone rotated-pillar log block, separate from the shared
 * Blocks.log / Blocks.log2 metadata families (those are full - 4
 * variants each - so cherry gets its own block IDs instead, one
 * instance of this class for "cherry_log" and a second instance for
 * "stripped_cherry_log", distinguished purely by their block-state/
 * model JSON, exactly like BlockHay is a standalone pillar block).
 */
public class BlockCherryLog extends BlockRotatedPillar {
	public BlockCherryLog() {
		super(Material.wood, MapColor.pinkColor);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
		this.setStepSound(soundTypeWood);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public IBlockState getStateFromMeta(int i) {
		EnumFacing.Axis axis = EnumFacing.Axis.Y;
		int j = i & 12;
		if (j == 4) {
			axis = EnumFacing.Axis.X;
		} else if (j == 8) {
			axis = EnumFacing.Axis.Z;
		}
		return this.getDefaultState().withProperty(AXIS, axis);
	}

	public int getMetaFromState(IBlockState iblockstate) {
		int i = 0;
		EnumFacing.Axis axis = (EnumFacing.Axis) iblockstate.getValue(AXIS);
		if (axis == EnumFacing.Axis.X) {
			i |= 4;
		} else if (axis == EnumFacing.Axis.Z) {
			i |= 8;
		}
		return i;
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { AXIS });
	}

	protected ItemStack createStackedBlock(IBlockState var1) {
		return new ItemStack(Item.getItemFromBlock(this), 1, 0);
	}

	public IBlockState onBlockPlaced(World world, BlockPos blockpos, EnumFacing enumfacing, float f, float f1,
			float f2, int i, EntityLivingBase entitylivingbase) {
		return super.onBlockPlaced(world, blockpos, enumfacing, f, f1, f2, i, entitylivingbase).withProperty(AXIS,
				enumfacing.getAxis());
	}
}
