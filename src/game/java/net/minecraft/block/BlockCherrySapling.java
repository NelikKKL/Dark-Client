package net.minecraft.block;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockCherrySapling extends BlockBush implements IGrowable {
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

	public BlockCherrySapling() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, Integer.valueOf(0)));
		float f = 0.4F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
		if (!world.isRemote) {
			super.updateTick(world, pos, state, rand);
			if (world.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
				this.growSapling(world, pos, state, rand);
			}
		}
	}

	private void growSapling(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
		if (((Integer) state.getValue(STAGE)).intValue() == 0) {
			world.setBlockState(pos, state.cycleProperty(STAGE), 4);
		} else {
			this.generateTree(world, pos, state, rand);
		}
	}

	/**
	 * TEMPORARY generator - places a tree using the cherry log/leaves
	 * blockstates via the generic WorldGenTrees shape. The dedicated
	 * cherry generator (curved trunk, wide drooping canopy, matching
	 * the real game) is a follow-up piece of work and will be swapped
	 * in here without touching anything else in this class.
	 */
	public void generateTree(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
		IBlockState log = Blocks.cherry_log.getDefaultState();
		IBlockState leaves = Blocks.cherry_leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY,
				Boolean.valueOf(false));
		WorldGenerator gen = new WorldGenTrees(false, 5 + rand.nextInt(3), log, leaves, true);
		world.setBlockToAir(pos);
		if (!gen.generate(world, rand, pos)) {
			world.setBlockState(pos, state, 4);
		}
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { STAGE });
	}

	public IBlockState getStateFromMeta(int i) {
		return this.getDefaultState().withProperty(STAGE, Integer.valueOf(i & 1));
	}

	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(STAGE)).intValue();
	}

	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item, 1, 0));
	}

	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isRemote) {
		return true;
	}

	public boolean canUseBonemeal(World world, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
		return (double) world.rand.nextFloat() < 0.45D;
	}

	public void grow(World world, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
		this.growSapling(world, pos, state, rand);
	}
}
