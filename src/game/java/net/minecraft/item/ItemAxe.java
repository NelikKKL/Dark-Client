package net.minecraft.item;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class ItemAxe extends ItemTool {
	private static Set<Block> EFFECTIVE_ON;

	public static void bootstrap() {
		EFFECTIVE_ON = Sets.newHashSet(new Block[] { Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2,
				Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin, Blocks.melon_block, Blocks.ladder });
	}

	protected ItemAxe(Item.ToolMaterial material) {
		super(3.0F, material, EFFECTIVE_ON);
	}

	public float getStrVsBlock(ItemStack itemstack, Block block) {
		return block.getMaterial() != Material.wood && block.getMaterial() != Material.plants
				&& block.getMaterial() != Material.vine ? super.getStrVsBlock(itemstack, block)
						: this.efficiencyOnProperMaterial;
	}

	// ===== NewBlood: Netherite/Cherry content =====
	// Right-clicking a cherry log with an axe strips it, same as vanilla's
	// 1.13+ stripping mechanic - stripped_cherry_log existed as a block
	// already but had no way to actually obtain it in survival.
	@Override
	public boolean onItemUse(ItemStack stack, net.minecraft.entity.player.EntityPlayer player, net.minecraft.world.World world,
			net.minecraft.util.BlockPos pos, net.minecraft.util.EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == Blocks.cherry_log) {
				world.setBlockState(pos, Blocks.stripped_cherry_log.getDefaultState()
						.withProperty(net.minecraft.block.BlockRotatedPillar.AXIS,
								state.getValue(net.minecraft.block.BlockRotatedPillar.AXIS)),
						3);
				if (!player.capabilities.isCreativeMode) {
					stack.damageItem(1, player);
				}
				return true;
			}
		}
		return false;
	}
}