package slimeknights.tconstruct.plugin.jei.severing;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.SeveringRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;

public class SeveringRecipeChecker {
	public static Set<SeveringRecipe> getSeveringRecipes() {
		Set<SeveringRecipe> recipes = new HashSet<>();
		for (Class<? extends EntityLivingBase> entityClass : TinkerRegistry.getAllSeveringRecipes().keySet()) {
			ResourceLocation rl = EntityList.getKey(entityClass);
			if (rl == null) {
				continue;
			}
			ItemStack spawnEgg = new ItemStack(Items.SPAWN_EGG);
			ItemMonsterPlacer.applyEntityIdToItemStack(spawnEgg, rl);
			ItemStack headStack = TinkerRegistry.getAllSeveringRecipes().get(entityClass);

			if (headStack != null && !headStack.isEmpty()) {
				SeveringRecipe severingRecipe = new SeveringRecipe(rl, headStack, spawnEgg);
				recipes.add(severingRecipe);
			}

		}
		return recipes;
	}
}
