package net.newblood.module.modules;

import net.minecraft.client.gui.GuiGameOver;
import net.newblood.module.Module;

public class AutoRespawn extends Module {

	public AutoRespawn() {
		super("AutoRespawn", "Мгновенный респавн после смерти", Category.MISC);
	}

	@Override
	public void onTick() {
		if (mc.currentScreen instanceof GuiGameOver && mc.thePlayer != null) {
			mc.thePlayer.respawnPlayer();
			mc.displayGuiScreen(null);
		}
	}
}
