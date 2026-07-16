package net.newblood.content;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.newblood.NewBloodClient;

/**+
 * NewBlood content gating.
 *
 * Cherry Grove and Netherite content is meant to be a purely local
 * addition to the player's own singleplayer world - the same rule
 * that already applies to every cheat module (see
 * NewBloodClient#isSafeEnvironment()). This wrapper is used in place
 * of the plain ItemBlock for every new block added by that content
 * pack: on a remote server (or with LAN open) placement is silently
 * refused and the player gets the same one-line explanation a
 * blocked module would print.
 */
public class GatedItemBlock extends ItemBlock {

	public GatedItemBlock(Block block) {
		super(block);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		// Only evaluate the gate on the client-side prediction call. The
		// integrated (singleplayer) server processes this same placement
		// independently, on its own World, running on its own thread/worker -
		// isSafeEnvironment() reads the CLIENT's Minecraft singleton, which
		// isn't meaningfully reachable from that context. Gating there too
		// made the server silently reject every placement a tick after the
		// client had already shown it, so the block popped back to air a
		// moment later. This codebase's integrated server is always the
		// player's own local world (a real remote server never runs this
		// code), so it's safe to trust whatever the client already decided.
		if (world.isRemote && !NewBloodClient.isSafeEnvironment()) {
			if (player != null) {
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED
						+ "[NewBlood] This content is only available in your own local singleplayer world (not on a server, not with LAN open)."));
			}
			return false;
		}
		return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
	}
}
