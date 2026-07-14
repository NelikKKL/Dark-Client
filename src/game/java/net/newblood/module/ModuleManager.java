package net.newblood.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.minecraft.client.Minecraft;
import net.newblood.NewBloodClient;
import net.newblood.module.modules.AntiAim;
import net.newblood.module.modules.AntiArrow;
import net.newblood.module.modules.AttackAnimation;
import net.newblood.module.modules.AutoArmor;
import net.newblood.module.modules.AutoClicker;
import net.newblood.module.modules.AutoGapple;
import net.newblood.module.modules.AutoRespawn;
import net.newblood.module.modules.AutoEat;
import net.newblood.module.modules.BunnyHop;
import net.newblood.module.modules.ClickTP;
import net.newblood.module.modules.ESP;
import net.newblood.module.modules.Fly;
import net.newblood.module.modules.FreeCam;
import net.newblood.module.modules.FullLight;
import net.newblood.module.modules.IceWalk;
import net.newblood.module.modules.ItemPhysics;
import net.newblood.module.modules.Jesus;
import net.newblood.module.modules.JumpCircle;
import net.newblood.module.modules.Killaura;
import net.newblood.module.modules.NoClip;
import net.newblood.module.modules.NoFall;
import net.newblood.module.modules.Optimizer;
import net.newblood.module.modules.Parkour;
import net.newblood.module.modules.SpeedMine;
import net.newblood.module.modules.Spider;
import net.newblood.module.modules.Tracers;
import net.newblood.module.modules.Velocity;
import net.newblood.module.modules.XRay;

public class ModuleManager {

	private final List<Module> modules = new ArrayList<>();
	private final Map<Module, Boolean> keyStates = new HashMap<>();

	public ModuleManager() {
		// Combat
		modules.add(new Killaura());
		modules.add(new AutoClicker());
		modules.add(new Velocity());
		modules.add(new AutoEat());
		modules.add(new AutoArmor());
		modules.add(new AutoGapple());
		modules.add(new AntiAim());
		modules.add(new AntiArrow());

		// Movement
		modules.add(new Fly());
		modules.add(new NoFall());
		modules.add(new Spider());
		modules.add(new ClickTP());
		modules.add(new NoClip());
		modules.add(new Jesus());
		modules.add(new BunnyHop());
		modules.add(new IceWalk());
		modules.add(new Parkour());

		// Render
		modules.add(new FullLight());
		modules.add(new ESP());
		modules.add(new XRay());
		modules.add(new Tracers());
		modules.add(new FreeCam());
		modules.add(new JumpCircle());
		modules.add(new ItemPhysics());

		// Misc
		modules.add(new Optimizer());
		modules.add(new AttackAnimation());
		modules.add(new SpeedMine());
		modules.add(new AutoRespawn());
	}

	/**
	 * Runs once per client tick from NewBloodClient.onClientTick(), which is
	 * hooked into Minecraft#runTick(). Handles:
	 *  1) the safety watchdog - force-disables everything the instant the
	 *     player is not in a private local world;
	 *  2) module keybind polling;
	 *  3) per-module onTick() for whatever is still legitimately enabled.
	 */
	public void onTick() {
		Minecraft mc = Minecraft.getMinecraft();
		boolean safe = NewBloodClient.isSafeEnvironment();

		if (NewBloodClient.INSTANCE != null) {
			NewBloodClient.INSTANCE.onSafeStateChanged(safe);
		}

		if (!safe) {
			for (Module m : modules) {
				m.forceDisable();
			}
			return;
		}

		if (mc.thePlayer == null || mc.theWorld == null) {
			return;
		}

		// Keybinds only get polled when there's no open GUI eating input
		if (mc.currentScreen == null) {
			for (Module m : modules) {
				if (m.getKey() == 0) continue;
				boolean pressed = Keyboard.isKeyDown(m.getKey());
				boolean was = keyStates.getOrDefault(m, false);
				if (pressed && !was) {
					m.toggle();
				}
				keyStates.put(m, pressed);
			}
		}

		for (Module m : modules) {
			if (m.isEnabled()) {
				m.onTick();
			}
		}
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<Module> getModulesByCategory(Module.Category category) {
		return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
	}

	public Module getModuleByName(String name) {
		return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
