package net.newblood.module.modules;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.newblood.module.Module;

/** Auto-jumps when the block ahead is empty but the one below it isn't too far - classic parkour-assist. */
public class Parkour extends Module {

	public Parkour() {
		super("Parkour", "Автопрыжок на краях блоков при движении вперёд", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.theWorld == null) return;
		if (!mc.thePlayer.onGround) return;
		if (!mc.gameSettings.keyBindForward.isKeyDown()) return;

		double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
		double dx = -Math.sin(yaw) * 0.8;
		double dz = Math.cos(yaw) * 0.8;

		BlockPos feet = new BlockPos(mc.thePlayer.posX + dx, mc.thePlayer.posY, mc.thePlayer.posZ + dz);
		BlockPos ground = feet.down();

		Block feetBlock = mc.theWorld.getBlockState(feet).getBlock();
		Block groundBlock = mc.theWorld.getBlockState(ground).getBlock();

		boolean feetClear = feetBlock == net.minecraft.init.Blocks.air;
		boolean groundClear = groundBlock == net.minecraft.init.Blocks.air;

		if (feetClear && groundClear) {
			mc.thePlayer.motionY = 0.42;
		}
	}
}
