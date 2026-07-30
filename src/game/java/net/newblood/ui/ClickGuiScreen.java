package net.newblood.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.newblood.NewBloodClient;
import net.newblood.module.Module;
import net.newblood.module.settings.BooleanSetting;
import net.newblood.module.settings.NumberSetting;
import net.newblood.module.settings.Setting;

/**
 * ClickGUI, restyled to match the original NewBlood client: dark panels
 * with a red accent line, a red "NEW BLOOD" title with a small blood-drop
 * mark, and a light falling-snow background effect. Category panels can
 * be dragged by their header, and modules can be expanded to show their
 * settings inline.
 *
 * Every row still goes dark/unclickable with a warning banner whenever
 * NewBloodClient.isSafeEnvironment() is false - same lock as before,
 * just restyled.
 */
public class ClickGuiScreen extends GuiScreen {

	private static final int PANEL_WIDTH = 132;
	private static final int HEADER_HEIGHT = 18;
	private static final int ROW_HEIGHT = 16;
	private static final int SETTING_ROW_HEIGHT = 14;

	private static final int COLOR_ACCENT = 0xFFC62828; // NewBlood red
	private static final int COLOR_PANEL_BG = 0xE0141414;
	private static final int COLOR_HEADER_BG = 0xF01B1B1B;
	private static final int COLOR_ROW_BG = 0xB0202020;
	private static final int COLOR_ROW_BG_ON = 0xC02E7D32;

	private final Map<Module.Category, int[]> panelPos = new HashMap<>(); // {x, y}
	private final Set<Module> expanded = new HashSet<>();

	private Module.Category dragging;
	private int dragOffsetX, dragOffsetY;

	private final List<Snowflake> snow = new ArrayList<>();
	private long lastFrameNanos;

	private static class Snowflake {
		float x, y, speed, drift, size;
	}

	public ClickGuiScreen() {
		int x = 12;
		for (Module.Category cat : Module.Category.values()) {
			panelPos.put(cat, new int[] { x, 28 });
			x += PANEL_WIDTH + 10;
		}
		java.util.Random r = new java.util.Random();
		for (int i = 0; i < 80; i++) {
			Snowflake f = new Snowflake();
			f.x = r.nextInt(400);
			f.y = r.nextInt(240);
			f.speed = 6.0F + r.nextFloat() * 10.0F;
			f.drift = (r.nextFloat() - 0.5F) * 6.0F;
			f.size = 1.0F + r.nextFloat() * 1.5F;
			snow.add(f);
		}
		lastFrameNanos = System.nanoTime();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		boolean safe = NewBloodClient.isSafeEnvironment();

		drawRect(0, 0, width, height, 0x70000000);
		updateAndDrawSnow();

		drawTitle();

		for (Module.Category cat : Module.Category.values()) {
			List<Module> mods = NewBloodClient.INSTANCE.getModuleManager().getModulesByCategory(cat);
			if (mods.isEmpty()) continue;
			drawPanel(cat, mods, safe, mouseX, mouseY);
		}

		if (!safe) {
			String msg = EnumChatFormatting.RED
					+ "Locked: you are not in your own local singleplayer world";
			int w = fontRendererObj.getStringWidth(msg);
			drawRect(width / 2 - w / 2 - 6, height - 24, width / 2 + w / 2 + 6, height - 8, 0xB0000000);
			drawCenteredString(fontRendererObj, msg, width / 2, height - 21, 0xFFFFFFFF);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawTitle() {
		GlStateManager.pushMatrix();
		GlStateManager.translate(12.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.4F, 1.4F, 1.0F);
		drawString(fontRendererObj, EnumChatFormatting.BOLD + "" + EnumChatFormatting.RED + "NEW"
				+ EnumChatFormatting.WHITE + "BLOOD", 0, 0, 0xFFFFFFFF);
		GlStateManager.popMatrix();

		// small blood-drop mark next to the title
		int dropX = 12 + fontRendererObj.getStringWidth("NEWBLOOD") * 2 + 10;
		int dropY = 9;
		drawRect(dropX, dropY, dropX + 4, dropY + 5, COLOR_ACCENT);
		drawRect(dropX - 1, dropY + 5, dropX + 5, dropY + 8, COLOR_ACCENT);
		drawRect(dropX, dropY + 8, dropX + 4, dropY + 9, COLOR_ACCENT);
	}

	private void updateAndDrawSnow() {
		long now = System.nanoTime();
		float dt = (float) ((now - lastFrameNanos) / 1.0e9);
		lastFrameNanos = now;
		dt = MathHelper.clamp_float(dt, 0.0F, 0.1F);

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		for (Snowflake f : snow) {
			f.y += f.speed * dt;
			f.x += f.drift * dt;
			if (f.y > height) {
				f.y = -4;
				f.x = (float) (Math.random() * width);
			}
			if (f.x < 0) f.x = width;
			if (f.x > width) f.x = 0;
			drawRect((int) f.x, (int) f.y, (int) (f.x + f.size), (int) (f.y + f.size), 0x66FFFFFF);
		}
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}

	private void drawPanel(Module.Category cat, List<Module> mods, boolean safe, int mouseX, int mouseY) {
		int[] pos = panelPos.get(cat);
		int x = pos[0];
		int y = pos[1];

		int totalHeight = HEADER_HEIGHT;
		for (Module m : mods) {
			totalHeight += ROW_HEIGHT;
			if (expanded.contains(m)) {
				totalHeight += m.getSettings().size() * SETTING_ROW_HEIGHT;
			}
		}

		drawRect(x - 1, y - 1, x + PANEL_WIDTH + 1, y + totalHeight + 1, COLOR_ACCENT);
		drawRect(x, y, x + PANEL_WIDTH, y + HEADER_HEIGHT, COLOR_HEADER_BG);
		drawRect(x, y + HEADER_HEIGHT, x + PANEL_WIDTH, y + totalHeight, COLOR_PANEL_BG);
		drawRect(x, y + HEADER_HEIGHT - 2, x + PANEL_WIDTH, y + HEADER_HEIGHT, COLOR_ACCENT);
		drawCenteredString(fontRendererObj, cat.getName().toUpperCase(), x + PANEL_WIDTH / 2, y + 5, 0xFFEEEEEE);

		int rowY = y + HEADER_HEIGHT;
		for (Module m : mods) {
			boolean hover = mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT;
			int bg = m.isEnabled() ? COLOR_ROW_BG_ON : COLOR_ROW_BG;
			if (!safe) bg = 0x70282828;
			if (hover && safe) bg = blend(bg, 0x30FFFFFF);
			drawRect(x, rowY, x + PANEL_WIDTH, rowY + ROW_HEIGHT, bg);
			if (m.isEnabled()) {
				drawRect(x, rowY, x + 2, rowY + ROW_HEIGHT, COLOR_ACCENT);
			}
			int color = !safe ? 0xFF777777 : (m.isEnabled() ? 0xFFB6FFB6 : 0xFFDDDDDD);
			drawString(fontRendererObj, m.getName(), x + 6, rowY + 4, color);
			if (!m.getSettings().isEmpty()) {
				String arrow = expanded.contains(m) ? "-" : "+";
				drawString(fontRendererObj, arrow, x + PANEL_WIDTH - 12, rowY + 4, 0xFFAAAAAA);
			}
			rowY += ROW_HEIGHT;

			if (expanded.contains(m)) {
				for (Setting<?> s : m.getSettings()) {
					drawRect(x, rowY, x + PANEL_WIDTH, rowY + SETTING_ROW_HEIGHT, 0x90101010);
					drawSettingRow(x, rowY, s);
					rowY += SETTING_ROW_HEIGHT;
				}
			}
		}
	}

	private void drawSettingRow(int x, int y, Setting<?> s) {
		if (s instanceof BooleanSetting) {
			boolean v = ((BooleanSetting) s).getValue();
			drawString(fontRendererObj, s.getName(), x + 6, y + 3, 0xFFCCCCCC);
			int boxX = x + PANEL_WIDTH - 16;
			drawRect(boxX, y + 3, boxX + 8, y + 11, v ? 0xFF4CAF50 : 0xFF555555);
		} else if (s instanceof NumberSetting) {
			NumberSetting ns = (NumberSetting) s;
			String text = s.getName() + ": " + trimNumber(ns.getValue());
			drawString(fontRendererObj, text, x + 6, y + 3, 0xFFCCCCCC);
			drawString(fontRendererObj, "-", x + PANEL_WIDTH - 24, y + 3, 0xFFFF8A80);
			drawString(fontRendererObj, "+", x + PANEL_WIDTH - 12, y + 3, 0xFF8AFF8A);
		}
	}

	private static String trimNumber(double v) {
		if (v == Math.floor(v)) return String.valueOf((long) v);
		return String.format("%.2f", v);
	}

	private static int blend(int base, int add) {
		int aa = (add >>> 24) & 0xFF;
		int ar = (add >>> 16) & 0xFF, ag = (add >>> 8) & 0xFF, ab = add & 0xFF;
		int br = (base >>> 16) & 0xFF, bg = (base >>> 8) & 0xFF, bb = base & 0xFF;
		int r = Math.min(255, br + ar * aa / 255);
		int g = Math.min(255, bg + ag * aa / 255);
		int b = Math.min(255, bb + ab * aa / 255);
		return (base & 0xFF000000) | (r << 16) | (g << 8) | b;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		boolean safe = NewBloodClient.isSafeEnvironment();

		for (Module.Category cat : Module.Category.values()) {
			List<Module> mods = NewBloodClient.INSTANCE.getModuleManager().getModulesByCategory(cat);
			if (mods.isEmpty()) continue;
			int[] pos = panelPos.get(cat);
			int x = pos[0];
			int y = pos[1];

			if (mouseButton == 0 && mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= y
					&& mouseY <= y + HEADER_HEIGHT) {
				dragging = cat;
				dragOffsetX = mouseX - x;
				dragOffsetY = mouseY - y;
				return;
			}

			int rowY = y + HEADER_HEIGHT;
			for (Module m : mods) {
				boolean inRow = mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= rowY
						&& mouseY <= rowY + ROW_HEIGHT;
				if (inRow) {
					if (mouseX >= x + PANEL_WIDTH - 16 && !m.getSettings().isEmpty()) {
						if (expanded.contains(m)) expanded.remove(m); else expanded.add(m);
					} else if (safe) {
						m.toggle();
					}
					return;
				}
				rowY += ROW_HEIGHT;

				if (expanded.contains(m)) {
					for (Setting<?> s : m.getSettings()) {
						boolean inSettingRow = mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= rowY
								&& mouseY <= rowY + SETTING_ROW_HEIGHT;
						if (inSettingRow && safe) {
							handleSettingClick(mouseX, x, s);
							return;
						}
						rowY += SETTING_ROW_HEIGHT;
					}
				}
			}
		}
	}

	private void handleSettingClick(int mouseX, int panelX, Setting<?> s) {
		if (s instanceof BooleanSetting) {
			BooleanSetting bs = (BooleanSetting) s;
			bs.setValue(!bs.getValue());
		} else if (s instanceof NumberSetting) {
			NumberSetting ns = (NumberSetting) s;
			if (mouseX >= panelX + PANEL_WIDTH - 26 && mouseX < panelX + PANEL_WIDTH - 16) {
				ns.setValue(ns.getValue() - ns.getStep());
			} else if (mouseX >= panelX + PANEL_WIDTH - 14) {
				ns.setValue(ns.getValue() + ns.getStep());
			}
		}
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedButton, timeSinceLastClick);
		if (dragging != null) {
			panelPos.put(dragging, new int[] { mouseX - dragOffsetX, mouseY - dragOffsetY });
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		dragging = null;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		// 1 = ESC, 18 = E - both close the GUI regardless of lock state
		if (keyCode == 1 || keyCode == 18) {
			mc.displayGuiScreen(null);
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
}
