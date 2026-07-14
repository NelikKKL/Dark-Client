package net.newblood.ui;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.newblood.NewBloodClient;
import net.newblood.module.Module;

/**
 * Minimal click-GUI: one column per category, one row per module.
 * Doesn't pause the game (doesGuiPauseGame = false) so it behaves like the
 * classic "ClickGUI" overlay rather than a menu screen.
 *
 * The GUI itself always opens fine (it's just a list) - but every row is
 * drawn greyed-out and unclickable whenever NewBloodClient.isSafeEnvironment()
 * is false, with a banner explaining why. This mirrors the same lock used
 * by Module#toggle(), so there's no path to enabling anything on a server.
 */
public class ClickGuiScreen extends GuiScreen {

	private static final int ROW_HEIGHT = 12;
	private static final int COL_WIDTH = 100;

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		boolean safe = NewBloodClient.isSafeEnvironment();

		int x = 10;
		for (Module.Category cat : Module.Category.values()) {
			List<Module> mods = NewBloodClient.INSTANCE.getModuleManager().getModulesByCategory(cat);
			if (mods.isEmpty()) {
				continue;
			}
			int y = 10;
			drawRect(x, y, x + COL_WIDTH, y + ROW_HEIGHT, 0xAA202020);
			drawCenteredString(fontRendererObj, cat.getName(), x + COL_WIDTH / 2, y + 2, 0xFFFFAA00);
			y += ROW_HEIGHT;

			for (Module m : mods) {
				int bg = m.isEnabled() ? 0xAA2E7D32 : 0x88303030;
				if (!safe) {
					bg = 0x66303030;
				}
				drawRect(x, y, x + COL_WIDTH, y + ROW_HEIGHT, bg);
				int color = !safe ? 0xFF777777 : (m.isEnabled() ? 0xFF7CFC7C : 0xFFDDDDDD);
				drawString(fontRendererObj, m.getName(), x + 3, y + 2, color);
				y += ROW_HEIGHT;
			}
			x += COL_WIDTH + 6;
		}

		if (!safe) {
			String msg = EnumChatFormatting.RED + "Locked: you are not in your own local singleplayer world";
			drawRect(8, height - 20, 8 + fontRendererObj.getStringWidth(msg) + 8, height - 6, 0xAA000000);
			drawString(fontRendererObj, msg, 12, height - 17, 0xFFFFFFFF);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton != 0) return;
		if (!NewBloodClient.isSafeEnvironment()) return; // rows aren't clickable while locked

		int x = 10;
		for (Module.Category cat : Module.Category.values()) {
			List<Module> mods = NewBloodClient.INSTANCE.getModuleManager().getModulesByCategory(cat);
			if (mods.isEmpty()) continue;
			int y = 10 + ROW_HEIGHT;
			for (Module m : mods) {
				if (mouseX >= x && mouseX <= x + COL_WIDTH && mouseY >= y && mouseY <= y + ROW_HEIGHT) {
					m.toggle();
					return;
				}
				y += ROW_HEIGHT;
			}
			x += COL_WIDTH + 6;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		// 1 = ESC - always allow closing the GUI regardless of lock state
		if (keyCode == 1) {
			mc.displayGuiScreen(null);
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
}
