package net.newblood;

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.newblood.config.ConfigManager;
import net.newblood.module.Module;
import net.newblood.module.ModuleManager;
import net.newblood.ui.ClickGuiScreen;

/**
 * Entry point for the ported NewBlood module suite.
 *
 * ====================== SAFETY LOCK ======================
 * Every module in this suite ONLY works in the player's own local
 * (singleplayer, not opened to LAN) world. The instant the player
 * connects to any remote server, or opens their world to LAN so other
 * people can join, every active module is force-disabled and can not
 * be re-enabled until they are back in a private local world.
 *
 * This check lives in one place - isSafeEnvironment() - and is enforced
 * both when a module is toggled (Module#toggle) and every tick by the
 * watchdog in ModuleManager#onTick(), so a module can't stay on across
 * a world transition.
 * ===========================================================
 */
public class NewBloodClient {

	public static NewBloodClient INSTANCE;

	public final Minecraft mc = Minecraft.getMinecraft();
	private final ModuleManager moduleManager = new ModuleManager();
	private final ConfigManager configManager = new ConfigManager();

	private boolean lastSafeState = true;
	private boolean guiKeyWasDown = false;

	/** Default keybind for opening the ClickGUI: Right Shift. */
	private static final int GUI_KEY = KeyboardConstants.KEY_RSHIFT;

	public static void init() {
		if (INSTANCE != null) return;
		INSTANCE = new NewBloodClient();
		INSTANCE.configManager.load();
	}

	/** Call once per client tick, e.g. from Minecraft#runTick(). */
	public static void onClientTick() {
		if (INSTANCE == null) return;
		INSTANCE.handleGuiKey();
		INSTANCE.moduleManager.onTick();
	}

	/**
	 * Right Shift opens the ClickGUI, but ONLY inside a safe local world.
	 * On a remote server (or LAN-hosted world) the key does nothing except
	 * print a one-line explanation, exactly like every other module.
	 */
	private void handleGuiKey() {
		if (mc.currentScreen != null) {
			guiKeyWasDown = Keyboard.isKeyDown(GUI_KEY);
			return;
		}
		boolean down = Keyboard.isKeyDown(GUI_KEY);
		if (down && !guiKeyWasDown) {
			if (isSafeEnvironment()) {
				mc.displayGuiScreen(new ClickGuiScreen());
			} else if (mc.thePlayer != null) {
				mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED
						+ "[NewBlood] Menu is only available in your own local singleplayer world."));
			}
		}
		guiKeyWasDown = down;
	}

	/**
	 * Called from inside EntityRenderer's camera-transformed matrix, same spot
	 * vanilla draws its block-selection outline. IMPORTANT: coordinates here
	 * must be CAMERA-RELATIVE, not absolute world coordinates - vanilla's own
	 * RenderGlobal#drawSelectionBox explicitly subtracts the interpolated
	 * player/camera position before drawing its box for exactly this reason.
	 * Subtract mc.getRenderManager().viewerPosX/Y/Z from every world position
	 * before handing it to any draw call in onRender().
	 */
	public static void onWorldRender(float partialTicks) {
		if (INSTANCE == null) return;
		if (!isSafeEnvironment()) return;

		boolean undoBob = cancelViewBob(partialTicks);
		try {
			for (Module m : INSTANCE.moduleManager.getModules()) {
				if (m.isEnabled()) {
					m.onRender(partialTicks);
				}
			}
		} finally {
			if (undoBob) {
				net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.popMatrix();
			}
		}
	}

	/**
	 * Vanilla's view-bob (EntityRenderer#setupViewBobbing) applies a GL
	 * translate + two rotates to the camera matrix BEFORE onWorldRender is
	 * ever called, so every module drawing world-space content here (tracer
	 * lines, ESP/ClickTP boxes) inherits that sway - while the 2D crosshair
	 * overlay never moves. That mismatch is what makes tracers look
	 * disconnected from the crosshair while walking. This pushes a fresh
	 * matrix and applies the exact inverse of that same transform so
	 * anything drawn afterwards (until the matching popMatrix above) lines
	 * up with the true, non-bobbing camera center instead.
	 */
	private static boolean cancelViewBob(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null || !mc.gameSettings.viewBobbing) return false;
		if (!(mc.getRenderViewEntity() instanceof net.minecraft.entity.player.EntityPlayer)) return false;

		net.minecraft.entity.player.EntityPlayer p = (net.minecraft.entity.player.EntityPlayer) mc
				.getRenderViewEntity();
		float f = p.distanceWalkedModified - p.prevDistanceWalkedModified;
		float f1 = -(p.distanceWalkedModified + f * partialTicks);
		float f2 = p.prevCameraYaw + (p.cameraYaw - p.prevCameraYaw) * partialTicks;
		float f3 = p.prevCameraPitch + (p.cameraPitch - p.prevCameraPitch) * partialTicks;

		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.pushMatrix();
		// Exact inverse of setupViewBobbing, applied in reverse order.
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate(-f3, 1.0F, 0.0F, 0.0F);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate(
				-Math.abs(net.minecraft.util.MathHelper.cos(f1 * 3.1415927F - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager
				.rotate(-net.minecraft.util.MathHelper.sin(f1 * 3.1415927F) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(
				-(net.minecraft.util.MathHelper.sin(f1 * 3.1415927F) * f2 * 0.5F),
				Math.abs(net.minecraft.util.MathHelper.cos(f1 * 3.1415927F) * f2), 0.0F);
		return true;
	}

	/** Call once per rendered frame with the current partial tick time. */
	public static void onRenderTick(float partialTicks) {
		if (INSTANCE == null) return;
		// intentionally empty for now - reserved for 2D/HUD-space rendering
		// (onWorldRender above handles the 3D world-space ESP/tracer drawing)
	}

	/**
	 * True only when the player is in their own local world:
	 * integrated (singleplayer) server running, AND not opened to LAN.
	 * False for any remote multiplayer connection.
	 */
	public static boolean isSafeEnvironment() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.theWorld == null || mc.thePlayer == null) return false;
		if (!mc.isSingleplayer()) return false;
		if (LANServerController.isHostingLAN()) return false;
		return true;
	}

	public static void notifyBlocked(Module m) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.thePlayer != null) {
			mc.thePlayer.addChatMessage(new ChatComponentText(
					EnumChatFormatting.RED + "[NewBlood] " + m.getName()
							+ " is only available in your own local singleplayer world (not on a server, not with LAN open)."));
		}
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	/** Called by the watchdog whenever the safe/unsafe state flips, for chat feedback. */
	public void onSafeStateChanged(boolean nowSafe) {
		if (nowSafe == lastSafeState) return;
		lastSafeState = nowSafe;
		if (!nowSafe && mc.thePlayer != null) {
			mc.thePlayer.addChatMessage(new ChatComponentText(
					EnumChatFormatting.YELLOW + "[NewBlood] Left the local world - all modules disabled."));
		}
	}
}
