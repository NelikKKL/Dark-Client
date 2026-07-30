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
 *  - Upgrade: a diamond tool/armor piece + 1 netherite ingot turns into
 *    the matching netherite piece, keeping enchantments/custom name/
 *    unbreakable etc - see RecipeNetheriteUpgrade, which copies the
 *    diamond item's NBT across instead of using a blank template output
 *    the way a plain shapeless recipe would.
 */
public class RecipesNetherite {

	public void addRecipes(CraftingManager parCraftingManager) {
		parCraftingManager.addRecipe(new ItemStack(Items.netherite_ingot, 1),
				new Object[] { "GSG", "SGS", "GSG", Character.valueOf('G'), Items.gold_ingot, Character.valueOf('S'),
						Items.netherite_scrap });

		// Diamond -> netherite upgrades preserve enchantments/custom name/
		// unbreakable etc from the diamond item - see RecipeNetheriteUpgrade.
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_sword, Items.netherite_sword));
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_pickaxe, Items.netherite_pickaxe));
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_axe, Items.netherite_axe));
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_shovel, Items.netherite_shovel));
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_hoe, Items.netherite_hoe));
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_helmet, Items.netherite_helmet));
		parCraftingManager
				.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_chestplate, Items.netherite_chestplate));
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_leggings, Items.netherite_leggings));
		parCraftingManager.addRecipe(new RecipeNetheriteUpgrade(Items.diamond_boots, Items.netherite_boots));
	}
}
