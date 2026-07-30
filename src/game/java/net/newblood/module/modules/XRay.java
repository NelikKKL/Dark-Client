package net.newblood.module.modules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.newblood.module.Module;

/**
 * Real XRay: makes stone-type blocks (and other common "filler" rock)
 * fully invisible so you can see straight through them into caves/ores,
 * instead of drawing highlight boxes on top of ores.
 *
 * This works by hooking Block#getRenderType(): RenderChunk skips a block
 * entirely during chunk mesh building when that returns -1 (see
 * RenderChunk#tessellateInner). {@link #isHidden(Block)} is checked live
 * from there, so toggling the module and forcing a re-render of nearby
 * chunks makes the blocks genuinely disappear (collision/mining are
 * untouched - only the render mesh skips them).
 */
public class XRay extends Module {

	private static final Set<Block> HIDDEN = Collections.synchronizedSet(new HashSet<Block>());

	public XRay() {
		super("XRay", "Makes stone and other common rock fully see-through", Category.RENDER);
	}

	public static boolean isHidden(Block block) {
		return !HIDDEN.isEmpty() && HIDDEN.contains(block);
	}

	@Override
	public void onEnable() {
		HIDDEN.add(Blocks.stone);
		HIDDEN.add(Blocks.dirt);
		HIDDEN.add(Blocks.grass);
		HIDDEN.add(Blocks.gravel);
		HIDDEN.add(Blocks.sand);
		HIDDEN.add(Blocks.sandstone);
		HIDDEN.add(Blocks.cobblestone);
		HIDDEN.add(Blocks.netherrack);
		HIDDEN.add(Blocks.end_stone);
		HIDDEN.add(Blocks.mycelium);
		refreshChunks();
	}

	@Override
	public void onDisable() {
		HIDDEN.clear();
		refreshChunks();
	}

	/** Forces every loaded chunk near the player to rebuild its render mesh. */
	private void refreshChunks() {
		if (mc.thePlayer == null || mc.theWorld == null) return;
		BlockPos p = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		int r = 128;
		mc.theWorld.markBlockRangeForRenderUpdate(p.getX() - r, p.getY() - r, p.getZ() - r, p.getX() + r,
				p.getY() + r, p.getZ() + r);
	}
}
