package net.newblood.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.newblood.NewBloodClient;
import net.newblood.module.Module;

/**
 * Very small key:value persistence layer, stored via EagRuntime's browser
 * storage (same mechanism GameSettings uses for options.txt).
 *
 * Only stores which modules were toggled on - never bypasses the
 * safety lock on load, see Module#setEnabledFromConfig.
 */
public class ConfigManager {

	private static final String STORAGE_KEY = "newblood_config";

	public void save() {
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bao, false, "UTF-8");
			for (Module m : NewBloodClient.INSTANCE.getModuleManager().getModules()) {
				out.println(m.getName() + ":" + m.isEnabled() + ":" + m.getKey());
			}
			out.flush();
			EagRuntime.setStorage(STORAGE_KEY, bao.toByteArray());
		} catch (Exception e) {
			// non-fatal, config persistence is best-effort
		}
	}

	public void load() {
		try {
			byte[] data = EagRuntime.getStorage(STORAGE_KEY);
			if (data == null) return;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(":");
				if (parts.length < 2) continue;
				Module m = NewBloodClient.INSTANCE.getModuleManager().getModuleByName(parts[0]);
				if (m == null) continue;
				// Deliberately does NOT re-enable modules on load if unsafe -
				// setEnabledFromConfig re-checks isSafeEnvironment() itself.
				m.setEnabledFromConfig(Boolean.parseBoolean(parts[1]));
				if (parts.length >= 3) {
					try {
						m.setKey(Integer.parseInt(parts[2]));
					} catch (NumberFormatException ignored) {}
				}
			}
		} catch (Exception e) {
			// non-fatal
		}
	}
}
