package net.newblood.module.modules;

import net.newblood.module.Module;

/** Trims particles/clouds/render distance while enabled for a smoother experience. */
public class Optimizer extends Module {

	private int savedParticles;
	private int savedClouds;
	private int savedRenderDistance;

	public Optimizer() {
		super("Optimizer", "Reduces particles and render distance for better FPS", Category.MISC);
	}

	@Override
	public void onEnable() {
		savedParticles = mc.gameSettings.particleSetting;
		savedClouds = mc.gameSettings.clouds;
		savedRenderDistance = mc.gameSettings.renderDistanceChunks;

		mc.gameSettings.particleSetting = 2; // minimal
		mc.gameSettings.clouds = 0; // off
		mc.gameSettings.renderDistanceChunks = Math.min(savedRenderDistance, 6);
	}

	@Override
	public void onDisable() {
		mc.gameSettings.particleSetting = savedParticles;
		mc.gameSettings.clouds = savedClouds;
		mc.gameSettings.renderDistanceChunks = savedRenderDistance;
	}
}
