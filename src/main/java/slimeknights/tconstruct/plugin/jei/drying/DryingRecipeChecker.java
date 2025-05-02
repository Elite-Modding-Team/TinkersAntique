package slimeknights.tconstruct.plugin.jei.drying;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;

public class DryingRecipeChecker {
  public static Set<DryingRecipe> getDryingRecipes() {
    Set<DryingRecipe> recipes = new ObjectOpenHashSet<>();

    for(DryingRecipe recipe : TinkerRegistry.getAllDryingRecipes()) {
      if(recipe.output != null && recipe.input != null && recipe.input.getInputs() != null && !recipe.input.getInputs().isEmpty()) {
        recipes.add(recipe);
      }
    }

    return recipes;
  }
}
