package net.newblood.module.modules;

import net.newblood.module.Module;

/**
 * Classic "fullbright". Implemented by temporarily overriding the gamma
 * setting that EntityRenderer#updateLightmap() already reads every frame,
 * so no renderer patch is needed - just restore the saved value on disable.
 */
public class FullLight extends Module {

	private float savedGamma;

	public FullLight() {
		super("FullLight", "Full brightness (fullbright)", Category.RENDER);
	}

	@Override
	public void onEnable() {
		savedGamma = mc.gameSettings.gammaSetting;
		mc.gameSettings.gammaSetting = 1000.0F;
	}

	@Override
	public void onDisable() {
		mc.gameSettings.gammaSetting = savedGamma;
	}
}
