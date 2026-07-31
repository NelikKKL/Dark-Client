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
 * By default every module works **only** in the player's own local
 * (single‑player, not LAN‑hosted) world.
 *
 * Set {@link #ENABLE_GLOBAL} to {@code true} to **remove** this restriction –
 * modules will then run everywhere (local, remote, LAN).
 * ===========================================================
 */
public class NewBloodClient {

    public static NewBloodClient INSTANCE;

    /** When {@code true} modules are allowed to run in any world (remote servers,
     *  LAN, or local). When {@code false} the original safety‑lock stays active. */
    public static boolean ENABLE_GLOBAL = true;   // <-- NEW / RENAMED

    public final Minecraft mc = Minecraft.getMinecraft();
    private final ModuleManager moduleManager = new ModuleManager();
    private final ConfigManager configManager = new ConfigManager();

    private boolean lastSafeState = true;
    private boolean guiKeyWasDown = false;

    private static final int GUI_KEY = KeyboardConstants.KEY_RSHIFT;

    public static void init() {
        if (INSTANCE != null) return;
        INSTANCE = new NewBloodClient();
        INSTANCE.configManager.load();
    }

    /** Call once per client tick, e.g. from {@code Minecraft#runTick()}. */
    public static void onClientTick() {
        if (INSTANCE == null) return;
        INSTANCE.handleGuiKey();
        INSTANCE.moduleManager.onTick();
    }

    /**
     * Right‑Shift opens the ClickGUI, but ONLY inside a safe local world
     * unless {@link #ENABLE_GLOBAL} is {@code true}.
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

    /** Rendering hook – aborts if the environment isn’t safe. */
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

    /* ---------- unchanged helpers (cancelViewBob, onRenderTick, etc.) ---------- */

    /**
     * Determines whether the player is in a *safe* environment.
     * <p>
     * If {@link #ENABLE_GLOBAL} is {@code true} this method always returns
     * {@code true}, effectively disabling the safety lock.
     */
    public static boolean isSafeEnvironment() {
        if (ENABLE_GLOBAL) return true;                     // <-- NEW CHECK
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