package slimeknights.tconstruct.plugin.jei.alloy;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;

public class AlloyRecipeChecker {
  public static Set<AlloyRecipe> getAlloyRecipes() {
    Set<AlloyRecipe> recipes = new ObjectOpenHashSet<>();

    for(AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
      if(recipe.getFluids() != null && !recipe.getFluids().isEmpty() && recipe.getResult() != null && recipe.getResult().amount > 0) {
        recipes.add(recipe);
      }
    }

    return recipes;
  }
}
