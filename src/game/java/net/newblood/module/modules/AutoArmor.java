package net.newblood.module.modules;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.newblood.module.Module;

/**
 * Scans the main inventory for armor pieces better than what's worn and
 * shift-clicks them into place using a normal windowClick packet - same
 * as a player manually shift-clicking armor in their inventory, just automated.
 */
public class AutoArmor extends Module {

	private int tickCounter;

	public AutoArmor() {
		super("AutoArmor", "Автоматически надевает лучшую броню из инвентаря", Category.COMBAT);
	}

	@Override
	public void onTick() {
		if (mc.thePlayer == null || mc.playerController == null) return;
		if (tickCounter++ % 10 != 0) return; // don't hammer the network every tick

		ItemStack[] main = mc.thePlayer.inventory.mainInventory;
		for (int i = 0; i < main.length; i++) {
			ItemStack stack = main[i];
			if (stack == null || !(stack.getItem() instanceof ItemArmor)) continue;

			ItemArmor armor = (ItemArmor) stack.getItem();
			int slot = armor.armorType; // 0=helmet 1=chest 2=legs 3=boots
			ItemStack worn = mc.thePlayer.inventory.armorInventory[3 - slot];

			if (worn == null || isBetter(armor, worn)) {
				int containerSlot = i < 9 ? (36 + i) : i;
				mc.playerController.windowClick(0, containerSlot, 0, 1, mc.thePlayer); // shift-click
				return; // one swap per attempt keeps it predictable
			}
		}
	}

	private boolean isBetter(ItemArmor candidate, ItemStack wornStack) {
		if (!(wornStack.getItem() instanceof ItemArmor)) return true;
		ItemArmor worn = (ItemArmor) wornStack.getItem();
		return candidate.getArmorMaterial().getDamageReductionAmount(candidate.armorType)
				> worn.getArmorMaterial().getDamageReductionAmount(worn.armorType);
	}
}
