package slimeknights.tconstruct.plugin.jei.casting;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;

import java.util.Set;

public class CastingBasinRecipeChecker {

  private static CastingRecipeWrapper recipeWrapper;

  public static Set<CastingRecipeWrapper> getCastingRecipes() {
    Set<CastingRecipeWrapper> recipes = new ObjectLinkedOpenHashSet<>();

    for(ICastingRecipe irecipe : TinkerRegistry.getAllBasinCastingRecipes()) {
      if(irecipe instanceof CastingRecipe) {
        CastingRecipe recipe = (CastingRecipe) irecipe;

        recipeWrapper = new CastingRecipeWrapper(recipe, JEIPlugin.castingBasinCategory.castingBasin);

        if(recipeWrapper.isValid(true)) {
          recipes.add(recipeWrapper);
        }
      }
    }

    return recipes;
  }
}
