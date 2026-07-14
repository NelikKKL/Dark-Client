package net.newblood.module.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.util.BlockPos;
import net.newblood.module.Module;

/**
 * Classic "IceWalk": while enabled, movement over solid ground gets an
 * extra slide, as if every block beneath the player were ice. Purely a
 * client-side motion tweak - does not touch actual world block data.
 */
public class IceWalk extends Module {

	public IceWalk() {
		super("IceWalk", "Земля под ногами скользит как лёд", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.theWorld == null) return;
		BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
		Block block = mc.theWorld.getBlockState(pos).getBlock();
		if (block != net.minecraft.init.Blocks.air && !(block instanceof BlockIce) && mc.thePlayer.onGround) {
			mc.thePlayer.motionX *= 1.02;
			mc.thePlayer.motionZ *= 1.02;
		}
	}
}
