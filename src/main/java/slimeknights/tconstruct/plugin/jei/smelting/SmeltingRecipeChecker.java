package slimeknights.tconstruct.plugin.jei.smelting;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class SmeltingRecipeChecker {
  public static Set<MeltingRecipe> getSmeltingRecipes() {
    Set<MeltingRecipe> recipes = new ObjectOpenHashSet<>();

    for(MeltingRecipe recipe : TinkerRegistry.getAllMeltingRecipies()) {
      if(recipe.output != null && recipe.input != null && recipe.input.getInputs() != null && !recipe.input.getInputs().isEmpty()) {
        recipes.add(recipe);
      }
    }

    return recipes;
  }
}
