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
	 * vanilla draws its block-selection outline - so absolute world coordinates
	 * are valid for ESP boxes / tracer lines drawn here.
	 */
	public static void onWorldRender(float partialTicks) {
		if (INSTANCE == null) return;
		if (!isSafeEnvironment()) return;
		for (Module m : INSTANCE.moduleManager.getModules()) {
			if (m.isEnabled()) {
				m.onRender(partialTicks);
			}
		}
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
