package slimeknights.tconstruct.plugin.jei.severing;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import slimeknights.tconstruct.library.SeveringRecipe;

import javax.annotation.Nonnull;

public class SeveringRecipeHandler implements IRecipeWrapperFactory<SeveringRecipe> {
  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull SeveringRecipe recipe) {
    return new SeveringRecipeWrapper(recipe);
  }
}
