package net.newblood.module.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Real XRay: makes stone-type blocks (and other common "filler" rock)
 * fully invisible so you can see straight through them into caves/ores,
 * AND draws a coloured highlight box around every ore block still
 * nearby so they're easy to spot at a distance / through the now-hidden
 * stone.
 *
 * Transparency works by hooking Block#getRenderType(): RenderChunk skips
 * a block entirely during chunk mesh building when that returns -1 (see
 * RenderChunk#tessellateInner). {@link #isHidden(Block)} is checked live
 * from there, so toggling the module and forcing a re-render of nearby
 * chunks makes the blocks genuinely disappear (collision/mining are
 * untouched - only the render mesh skips them).
 */
public class XRay extends Module {

	private static final Set<Block> HIDDEN = Collections.synchronizedSet(new HashSet<Block>());
	private static final Map<Block, Integer> ORE_COLORS = new HashMap<>();
	static {
		ORE_COLORS.put(Blocks.coal_ore, 0xFF2B2B2B);
		ORE_COLORS.put(Blocks.iron_ore, 0xFFD8A76B);
		ORE_COLORS.put(Blocks.gold_ore, 0xFFFFD700);
		ORE_COLORS.put(Blocks.diamond_ore, 0xFF4DE1FF);
		ORE_COLORS.put(Blocks.redstone_ore, 0xFFFF3B3B);
		ORE_COLORS.put(Blocks.lit_redstone_ore, 0xFFFF3B3B);
		ORE_COLORS.put(Blocks.lapis_ore, 0xFF2A5CD8);
		ORE_COLORS.put(Blocks.emerald_ore, 0xFF23D160);
		ORE_COLORS.put(Blocks.quartz_ore, 0xFFF2E9DD);
		ORE_COLORS.put(Blocks.ancient_debris, 0xFFB8654A);
	}

	private final NumberSetting range = new NumberSetting("Ore highlight range", 24.0, 8.0, 48.0, 4.0);

	public XRay() {
		super("XRay", "Makes stone/rock see-through and highlights nearby ores", Category.RENDER);
		addSetting(range);
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

	@Override
	public void onRender(float partialTicks) {
		if (mc.thePlayer == null || mc.theWorld == null) return;

		RenderManager rm = mc.getRenderManager();
		double camX = rm.viewerPosX;
		double camY = rm.viewerPosY;
		double camZ = rm.viewerPosZ;

		int r = (int) range.getValue();
		BlockPos center = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
		BlockPos from = center.add(-r, -r, -r);
		BlockPos to = center.add(r, r, r);

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(from, to)) {
			Block block = mc.theWorld.getBlockState(pos).getBlock();
			Integer color = ORE_COLORS.get(block);
			if (color == null) continue;

			double x = pos.getX() - camX;
			double y = pos.getY() - camY;
			double z = pos.getZ() - camZ;
			AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);

			int c = color.intValue();
			float a = ((c >>> 24) & 0xFF) / 255.0F;
			float rr = ((c >>> 16) & 0xFF) / 255.0F;
			float gg = ((c >>> 8) & 0xFF) / 255.0F;
			float bb = (c & 0xFF) / 255.0F;
			GlStateManager.color(rr, gg, bb, a);
			RenderGlobal.func_181561_a(box);
		}

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
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

