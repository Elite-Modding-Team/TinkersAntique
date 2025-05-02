package slimeknights.tconstruct.plugin.jei.entitymelting;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class EntityMeltingRecipeWrapper implements IRecipeWrapper {

  protected final List<List<ItemStack>> inputs;
  protected final List<FluidStack> outputs;
  protected final List<FluidStack> fuels;

  public EntityMeltingRecipeWrapper(MeltingRecipe recipe) {
    this.inputs = ImmutableList.of(recipe.input.getInputs());
    this.outputs = ImmutableList.of(recipe.getResult());

    ImmutableList.Builder<FluidStack> builder = ImmutableList.builder();
    for(FluidStack fs : TinkerRegistry.getSmelteryFuels()) {
      fs = fs.copy();
      fs.amount = 1000;
      builder.add(fs);
    }
    fuels = builder.build();
  }

  @Override
  public void getIngredients(IIngredients ingredients) {
    ingredients.setInputLists(ItemStack.class, inputs);
    ingredients.setOutputs(FluidStack.class, outputs);
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    String info = inputs.get(0).get(0).getDisplayName();
    int x = 80 - minecraft.fontRenderer.getStringWidth(info) / 2;
    minecraft.fontRenderer.drawString(info, x, -2, Color.gray.getRGB());
  }
}
