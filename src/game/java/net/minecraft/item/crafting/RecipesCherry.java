package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**+
 * ===== NewBlood: Cherry content =====
 *
 * cherry_planks/cherry_log/cherry_door/cherry_trapdoor all existed as
 * registered blocks/items already, but had NO recipes anywhere - cherry_log
 * couldn't even be turned into cherry_planks, so none of it was actually
 * obtainable in survival despite the textures/models existing.
 *
 * cherry_planks is its own standalone Block (not a metadata variant of the
 * shared Blocks.planks, the way oak/spruce/birch/jungle/acacia/dark_oak
 * are), so every vanilla recipe that only accepts generic Blocks.planks
 * needs its own explicit cherry_planks entry added here too.
 */
public class RecipesCherry {

	public void addRecipes(CraftingManager parCraftingManager) {
		parCraftingManager.addRecipe(new ItemStack(Blocks.cherry_planks, 4),
				new Object[] { "#", Character.valueOf('#'), Blocks.cherry_log });

		parCraftingManager.addRecipe(new ItemStack(Items.stick, 4),
				new Object[] { "#", "#", Character.valueOf('#'), Blocks.cherry_planks });

		parCraftingManager.addRecipe(new ItemStack(Blocks.crafting_table),
				new Object[] { "##", "##", Character.valueOf('#'), Blocks.cherry_planks });

		parCraftingManager.addRecipe(new ItemStack(Items.cherry_door, 3), new Object[] { "##", "##", "##",
				Character.valueOf('#'), Blocks.cherry_planks });

		parCraftingManager.addRecipe(new ItemStack(Blocks.cherry_trapdoor, 2),
				new Object[] { "##", "##", Character.valueOf('#'), Blocks.cherry_planks });
	}
}
