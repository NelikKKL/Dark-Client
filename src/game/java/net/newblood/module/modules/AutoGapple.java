package net.newblood.module.modules;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.newblood.module.Module;
import net.newblood.module.settings.NumberSetting;

public class AutoGapple extends Module {

	private final NumberSetting healthThreshold = new NumberSetting("Health", 10.0, 2.0, 18.0, 1.0);
	private int switchBackDelay;
	private int previousSlot = -1;

	public AutoGapple() {
		super("AutoGapple", "Automatically eats a golden apple at low HP", Category.COMBAT);
		addSetting(healthThreshold);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.playerController == null) return;

		if (switchBackDelay > 0) {
			switchBackDelay--;
			if (switchBackDelay == 0 && previousSlot != -1) {
				mc.thePlayer.inventory.currentItem = previousSlot;
				previousSlot = -1;
			}
			return;
		}

		if (mc.thePlayer.getHealth() > healthThreshold.getValue()) return;

		int slot = findGappleSlot();
		if (slot == -1) return;

		previousSlot = mc.thePlayer.inventory.currentItem;
		mc.thePlayer.inventory.currentItem = slot;
		mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
		switchBackDelay = 32; // eat animation length in ticks
	}

	private int findGappleSlot() {
		ItemStack[] hotbar = mc.thePlayer.inventory.mainInventory;
		for (int i = 0; i < 9; i++) {
			ItemStack stack = hotbar[i];
			if (stack != null && stack.getItem() == Items.golden_apple) {
				return i;
			}
		}
		return -1;
	}
}
