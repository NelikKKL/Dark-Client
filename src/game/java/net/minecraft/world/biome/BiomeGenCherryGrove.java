package net.minecraft.world.biome;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPinkPetals;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;

/**+
 * Cherry Grove: a moderate/temperate biome that generates cherry trees
 * (using the existing cherry_log/cherry_leaves blocks, vines disabled -
 * see BlockCherrySapling's fix) and scatters pink petals across the
 * grass underneath them.
 *
 * Registered into BiomeGenBase.doBootstrap() under id 40, and added to
 * GenLayerBiome's temperate biome pool alongside forest/plains/birch
 * forest/etc so it actually gets picked during world generation.
 */
public class BiomeGenCherryGrove extends BiomeGenBase {

	public BiomeGenCherryGrove(int id) {
		super(id);
		this.spawnableCreatureList.clear();
		this.theBiomeDecorator.treesPerChunk = 5;
		this.theBiomeDecorator.grassPerChunk = 6;
		this.theBiomeDecorator.flowersPerChunk = 2;
		// Cherry blossoms grow in a cool, fairly wet, temperate climate -
		// distinct enough from plains/forest to read as its own biome.
		this.setTemperatureRainfall(0.5F, 0.8F);
		this.worldGeneratorTrees = new WorldGenTrees(false, 5, Blocks.cherry_log.getDefaultState(),
				Blocks.cherry_leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)),
				false);
	}

	@Override
	public WorldGenAbstractTree genBigTreeChance(EaglercraftRandom rand) {
		// Height varies per tree (5-7 blocks) instead of the base class's
		// single fixed-height instance, same range BlockCherrySapling uses
		// when a planted sapling grows on its own.
		IBlockState log = Blocks.cherry_log.getDefaultState();
		IBlockState leaves = Blocks.cherry_leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY,
				Boolean.valueOf(false));
		return new WorldGenTrees(false, 5 + rand.nextInt(3), log, leaves, false);
	}

	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		// Ground color should read as Plains regardless of Cherry Grove's
		// own (cooler/wetter) temperature+rainfall values used elsewhere
		// (tree growth chance etc) - Plains uses the BiomeGenBase default
		// climate (0.5, 0.5), so that's hardcoded here directly.
		return net.minecraft.world.ColorizerGrass.getGrassColor(0.5D, 0.5D);
	}

	@Override
	public void decorate(World world, EaglercraftRandom rand, BlockPos chunkPos) {
		super.decorate(world, rand, chunkPos);

		int petalPatches = 14 + rand.nextInt(10);
		for (int i = 0; i < petalPatches; ++i) {
			int px = rand.nextInt(16) + 8;
			int pz = rand.nextInt(16) + 8;
			BlockPos ground = world.getHeight(chunkPos.add(px, 0, pz)).down();
			BlockPos above = ground.up();
			if (world.getBlockState(ground).getBlock() == Blocks.grass
					&& world.isAirBlock(above)) {
				int petals = 1 + rand.nextInt(4);
				world.setBlockState(above, Blocks.pink_petals.getDefaultState()
						.withProperty(BlockPinkPetals.PETALS, Integer.valueOf(petals)), 2);
			}
		}
	}
}
