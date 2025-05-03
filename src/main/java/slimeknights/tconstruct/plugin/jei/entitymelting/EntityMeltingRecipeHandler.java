package slimeknights.tconstruct.plugin.jei.entitymelting;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class EntityMeltingRecipeHandler implements IRecipeWrapperFactory<EntityMeltingRecipe> {
  @Nonnull
  @Override
  public IRecipeWrapper getRecipeWrapper(@Nonnull EntityMeltingRecipe recipe) {
    return new EntityMeltingRecipeWrapper(recipe);
  }
}
