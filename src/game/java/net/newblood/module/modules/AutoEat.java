package net.newblood.module.modules;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.newblood.module.Module;

/**
 * Minecraft 1.8 (what Eaglercraft is built on) doesn't have totems of
 * undying - those were added in 1.11. This replaces NewBlood's AutoTotem
 * with the closest sensible 1.8 equivalent: auto-eating food from the
 * hotbar when hunger drops low.
 */
public class AutoEat extends Module {

	private int switchBackDelay;
	private int previousSlot = -1;

	public AutoEat() {
		super("AutoEat", "Автоеда при низком голоде (замена AutoTotem, которого нет в 1.8)", Category.COMBAT);
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

		if (mc.thePlayer.getFoodStats().getFoodLevel() >= 18) return;

		int slot = findFoodSlot();
		if (slot == -1) return;

		previousSlot = mc.thePlayer.inventory.currentItem;
		mc.thePlayer.inventory.currentItem = slot;
		mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
		switchBackDelay = 32;
	}

	private int findFoodSlot() {
		ItemStack[] hotbar = mc.thePlayer.inventory.mainInventory;
		for (int i = 0; i < 9; i++) {
			ItemStack stack = hotbar[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			if (item instanceof ItemFood) {
				return i;
			}
		}
		return -1;
	}
}
