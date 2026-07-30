package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**+
 * ===== NewBlood: Netherite content =====
 *
 * Diamond -> netherite upgrade recipe that preserves the diamond item's
 * NBT (enchantments, custom name/lore, "Unbreakable", etc.) on the
 * resulting netherite item.
 *
 * A plain addShapelessRecipe() always returns the exact same template
 * ItemStack no matter what NBT the ingredients had, which is why the
 * original version of this recipe stripped enchantments - this class
 * copies the diamond ingredient's tag compound across instead.
 */
public class RecipeNetheriteUpgrade implements IRecipe {

	private final Item diamondItem;
	private final Item netheriteItem;

	public RecipeNetheriteUpgrade(Item diamondItem, Item netheriteItem) {
		this.diamondItem = diamondItem;
		this.netheriteItem = netheriteItem;
	}

	private ItemStack findDiamondItem(InventoryCrafting inv) {
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.getItem() == this.diamondItem) {
				return stack;
			}
		}
		return null;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean foundDiamondItem = false;
		boolean foundIngot = false;

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack == null) continue;

			if (stack.getItem() == this.diamondItem && !foundDiamondItem) {
				foundDiamondItem = true;
			} else if (stack.getItem() == net.minecraft.init.Items.netherite_ingot && !foundIngot) {
				foundIngot = true;
			} else {
				return false; // anything else in the grid = not a match
			}
		}

		return foundDiamondItem && foundIngot;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack diamondStack = findDiamondItem(inv);
		ItemStack result = new ItemStack(this.netheriteItem, 1);
		if (diamondStack != null && diamondStack.hasTagCompound()) {
			result.setTagCompound(diamondStack.getTagCompound().copy());
		}
		return result;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(this.netheriteItem);
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}
}
