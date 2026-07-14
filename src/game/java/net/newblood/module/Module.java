package net.newblood.module;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.newblood.NewBloodClient;
import net.newblood.module.settings.Setting;

/**
 * Base class for every module.
 *
 * IMPORTANT: modules must NEVER be toggled on directly by calling setEnabled(true)
 * or flipping the internal flag. Always go through toggle()/tryEnable(), which
 * enforces NewBloodClient.isSafeEnvironment() (singleplayer / local world only).
 * See ModuleManager#onTick() for the runtime watchdog that force-disables any
 * module the instant the player is no longer in a safe local world (e.g. they
 * connect to a remote server, or someone opens the world to LAN).
 */
public abstract class Module {

	private final String name;
	private final String description;
	private final Category category;
	private boolean enabled;
	private int key;
	private final List<Setting<?>> settings = new ArrayList<>();
	protected final Minecraft mc = Minecraft.getMinecraft();

	public Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.enabled = false;
		this.key = 0;
	}

	public void addSetting(Setting<?> setting) {
		settings.add(setting);
	}

	public List<Setting<?>> getSettings() {
		return settings;
	}

	/**
	 * The only public entry point for turning a module on/off. Enforces the
	 * local-world-only lock. Returns true if the toggle actually happened.
	 */
	public final boolean toggle() {
		if (!enabled) {
			if (!NewBloodClient.isSafeEnvironment()) {
				NewBloodClient.notifyBlocked(this);
				return false;
			}
			enabled = true;
			onEnable();
		} else {
			enabled = false;
			onDisable();
		}
		NewBloodClient.INSTANCE.getConfigManager().save();
		return true;
	}

	/**
	 * Called by the watchdog only, to force a module off (server join, LAN host
	 * started, world unloaded, etc). Never routes back through onEnable/lock logic.
	 */
	final void forceDisable() {
		if (enabled) {
			enabled = false;
			onDisable();
		}
	}

	public void onEnable() {}
	public void onDisable() {}
	public void onTick() {}
	public void onRender(float partialTicks) {}

	public String getName() { return name; }
	public String getDescription() { return description; }
	public Category getCategory() { return category; }
	public boolean isEnabled() { return enabled; }
	public int getKey() { return key; }
	public void setKey(int key) { this.key = key; }

	/** Config loading only - does NOT bypass the safety lock at runtime. */
	public void setEnabledFromConfig(boolean value) {
		this.enabled = value && NewBloodClient.isSafeEnvironment();
	}

	public enum Category {
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		RENDER("Render"),
		MISC("Misc");

		private final String name;
		Category(String name) { this.name = name; }
		public String getName() { return name; }
	}
}
