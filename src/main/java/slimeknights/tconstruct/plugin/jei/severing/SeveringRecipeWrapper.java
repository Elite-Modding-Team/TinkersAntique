package slimeknights.tconstruct.plugin.jei.severing;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.SeveringRecipe;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class SeveringRecipeWrapper implements IRecipeWrapper {

  protected final List<List<ItemStack>> input;
  protected final List<ItemStack> output;

  public SeveringRecipeWrapper(SeveringRecipe recipe) {
    this.input = ImmutableList.of(recipe.input.getInputs());
    this.output = ImmutableList.of(recipe.getResult());
  }

  @Override
  public void getIngredients(IIngredients ingredients) {
    ingredients.setInputLists(ItemStack.class, input);
    ingredients.setOutputs(ItemStack.class, output);
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    String info = input.get(0).get(0).getDisplayName();
    int x = 80 - minecraft.fontRenderer.getStringWidth(info) / 2;
    minecraft.fontRenderer.drawString(info, x, 5, Color.gray.getRGB());
  }
}
