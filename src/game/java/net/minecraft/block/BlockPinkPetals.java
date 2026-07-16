package net.minecraft.block;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**+
 * Pink Petals - the ground-cover "flowers" of the Cherry Grove biome.
 * A thin, non-solid, stackable layer (1-4 petals), similar in spirit
 * to a snow layer: it doesn't stop the player, it can be placed on
 * top of itself to add more petals up to 4, and it drops itself when
 * broken (one item per layer present).
 */
public class BlockPinkPetals extends Block {
	public static final PropertyInteger PETALS = PropertyInteger.create("petals", 1, 4);

	public BlockPinkPetals() {
		super(Material.vine);
		this.setDefaultState(this.blockState.getBaseState().withProperty(PETALS, Integer.valueOf(1)));
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.setStepSound(soundTypeGrass);
		this.setTickRandomly(false);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean isFullCube() {
		return false;
	}

	/**+
	 * Without this override, Block#getBlockLayer() defaults to SOLID:
	 * the transparent pixels of the petals texture would be drawn as
	 * opaque black instead of being alpha-tested away (same class of
	 * bug as the standalone cherry leaves block).
	 */
	public net.minecraft.util.EnumWorldBlockLayer getBlockLayer() {
		return net.minecraft.util.EnumWorldBlockLayer.CUTOUT;
	}

	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return null;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		int petals = ((Integer) world.getBlockState(pos).getValue(PETALS)).intValue();
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float) petals / 8.0F, 1.0F);
	}

	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		Block below = world.getBlockState(pos.down()).getBlock();
		return below == this || (below.isOpaqueCube() && below.blockMaterial.blocksMovement());
	}

	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor) {
		if (!this.canPlaceBlockAt(world, pos)) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state,
			TileEntity tileentity) {
		int petals = ((Integer) state.getValue(PETALS)).intValue();
		spawnAsEntity(world, pos, new ItemStack(Item.getItemFromBlock(this), petals, 0));
		world.setBlockToAir(pos);
	}

	public int quantityDropped(EaglercraftRandom rand) {
		return 0;
	}

	public IBlockState getStateFromMeta(int i) {
		return this.getDefaultState().withProperty(PETALS, Integer.valueOf((i & 3) + 1));
	}

	public int getMetaFromState(IBlockState state) {
		return ((Integer) state.getValue(PETALS)).intValue() - 1;
	}

	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { PETALS });
	}
}
