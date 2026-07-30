package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**+
 * ===== NewBlood: Netherite content =====
 *
 * This 1.8 base has no smithing table, so netherite ingots and the
 * diamond -> netherite upgrade are both done on the crafting table:
 *
 *  - Netherite ingot: 4 netherite scrap in the 4 edge slots, gold
 *    ingots in the remaining 5 slots (corners + center).
 *  - Upgrade: any diamond tool/armor piece + 1 netherite ingot
 *    (shapeless) turns into the matching netherite piece. This is a
 *    plain crafting recipe rather than a real smithing operation, so
 *    enchantments on the diamond item are NOT carried over - that's a
 *    limitation of bolting this onto 1.8's recipe system rather than
 *    adding an actual smithing table block.
 */
public class RecipesNetherite {

	public void addRecipes(CraftingManager parCraftingManager) {
		parCraftingManager.addRecipe(new ItemStack(Items.netherite_ingot, 1),
				new Object[] { "GSG", "SGS", "GSG", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('S'),
						Items.netherite_scrap });

		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_sword, 1),
				new Object[] { Items.diamond_sword, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_pickaxe, 1),
				new Object[] { Items.diamond_pickaxe, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_axe, 1),
				new Object[] { Items.diamond_axe, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_shovel, 1),
				new Object[] { Items.diamond_shovel, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_hoe, 1),
				new Object[] { Items.diamond_hoe, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_helmet, 1),
				new Object[] { Items.diamond_helmet, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_chestplate, 1),
				new Object[] { Items.diamond_chestplate, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_leggings, 1),
				new Object[] { Items.diamond_leggings, Items.netherite_ingot });
		parCraftingManager.addShapelessRecipe(new ItemStack(Items.netherite_boots, 1),
				new Object[] { Items.diamond_boots, Items.netherite_ingot });
	}
}
