package net.newblood.module.modules;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

/**
 * Simplified XRay: rather than patching the chunk mesh renderer to make
 * stone transparent (a much bigger change touching RenderChunk/chunk
 * rebuild logic), this periodically scans nearby blocks and draws a
 * through-wall highlight box on valuable ores.
 *
 * The scan runs once per second (in onTick, not every frame) and is capped
 * at a modest radius, since a full nested XYZ scan every single frame would
 * tank performance, especially under TeaVM in a browser.
 */
public class XRay extends Module {

	private final NumberSetting radius = new NumberSetting("Радиус", 12.0, 4.0, 20.0, 2.0);
	private final List<OrePoint> cache = new ArrayList<>();
	private int rescanTimer;

	private static class OrePoint {
		final BlockPos pos;
		final float[] color;
		OrePoint(BlockPos pos, float[] color) { this.pos = pos; this.color = color; }
	}

	public XRay() {
		super("XRay", "Подсветка руды сквозь стены", Category.RENDER);
		addSetting(radius);
	}

	@Override
	public void onEnable() {
		rescanTimer = 0;
		cache.clear();
	}

	@Override
	public void onTick() {
		if (rescanTimer-- > 0) return;
		rescanTimer = 20; // rescan once a second

		cache.clear();
		if (mc.thePlayer == null || mc.theWorld == null) return;

		int r = (int) radius.getValue();
		BlockPos center = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

		for (int x = -r; x <= r; x++) {
			for (int y = -r; y <= r; y++) {
				for (int z = -r; z <= r; z++) {
					BlockPos pos = center.add(x, y, z);
					Block block = mc.theWorld.getBlockState(pos).getBlock();
					float[] color = colorFor(block);
					if (color != null) {
						cache.add(new OrePoint(pos, color));
					}
				}
			}
		}
	}

	@Override
	public void onRender(float partialTicks) {
		if (cache.isEmpty()) return;

		GlStateManager.disableTexture2D();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		for (OrePoint p : cache) {
			AxisAlignedBB box = new AxisAlignedBB(p.pos.getX(), p.pos.getY(), p.pos.getZ(), p.pos.getX() + 1,
					p.pos.getY() + 1, p.pos.getZ() + 1);
			GlStateManager.color(p.color[0], p.color[1], p.color[2], 0.9F);
			RenderGlobal.func_181561_a(box);
		}

		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}

	private float[] colorFor(Block block) {
		if (block == Blocks.diamond_ore) return new float[] { 0.3F, 0.9F, 1.0F };
		if (block == Blocks.emerald_ore) return new float[] { 0.2F, 1.0F, 0.4F };
		if (block == Blocks.gold_ore) return new float[] { 1.0F, 0.9F, 0.2F };
		if (block == Blocks.iron_ore) return new float[] { 0.9F, 0.9F, 0.9F };
		if (block == Blocks.redstone_ore || block == Blocks.lit_redstone_ore) return new float[] { 1.0F, 0.2F, 0.2F };
		if (block == Blocks.lapis_ore) return new float[] { 0.2F, 0.3F, 1.0F };
		return null;
	}
}
