package net.newblood.module.modules;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.newblood.module.Module;

public class Jesus extends Module {

	public Jesus() {
		super("Jesus", "Walk on water as if it were solid ground", Category.MOVEMENT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.theWorld == null) return;
		if (mc.thePlayer.isSneaking()) return;

		BlockPos below = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);
		Material mat = mc.theWorld.getBlockState(below).getBlock().getMaterial();

		if (mat == Material.water && mc.thePlayer.motionY < 0.0) {
			mc.thePlayer.motionY = 0.0;
			mc.thePlayer.fallDistance = 0.0F;
		}
	}
}
