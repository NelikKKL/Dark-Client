package net.minecraft.block;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**+
 * Cherry Grove leaves. Unlike every other leaf block, real cherry
 * leaves are not tinted by the biome foliage color - the texture
 * itself is already pink - so colorMultiplier/getBlockColor are
 * overridden to return plain white (no tint). Standalone block (own
 * ID), not part of the shared BlockOldLeaf/BlockNewLeaf variant
 * families, since those only have room for the six vanilla wood types.
 */
public class BlockCherryLeaves extends BlockLeaves {
	public BlockCherryLeaves() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, Boolean.valueOf(true))
				.withProperty(DECAYABLE, Boolean.valueOf(true)));
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public int getBlockColor() {
		return 0xFFFFFF;
	}

	public int getRenderColor(IBlockState state) {
		return 0xFFFFFF;
	}

	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
		return 0xFFFFFF;
	}

	protected void dropApple(World world, BlockPos pos, IBlockState state, int chance) {
		// cherry leaves never drop apples
	}

	public int damageDropped(IBlockState state) {
		return 0;
	}

	public int getDamageValue(World world, BlockPos pos) {
		return 0;
	}

	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item, 1, 0));
	}

	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(Item.getItemFromBlock(this), 1, 0);
	}

	public IBlockState getStateFromMeta(int i) {
		return this.getDefaultState().withProperty(DECAYABLE, Boolean.valueOf((i & 4) == 0))
				.withProperty(CHECK_DECAY, Boolean.valueOf((i & 8) > 0));
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		if (!((Boolean) state.getValue(DECAYABLE)).booleanValue()) {
			i |= 4;
		}
		if (((Boolean) state.getValue(CHECK_DECAY)).booleanValue()) {
			i |= 8;
		}
		return i;
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { CHECK_DECAY, DECAYABLE });
	}

	/**
	 * BlockLeaves declares this abstract for the shared oak/spruce/etc.
	 * variant families. Cherry leaves are a standalone block with no
	 * variants, so this is never actually consulted (we don't use
	 * ItemLeaves for the item form) - it just satisfies the contract.
	 */
	public BlockPlanks.EnumType getWoodType(int i) {
		return BlockPlanks.EnumType.OAK;
	}
}
