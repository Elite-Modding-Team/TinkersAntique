package slimeknights.tconstruct.plugin.jei.entitymelting;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.List;

public class EntityMeltingRecipeWrapper implements IRecipeWrapper {

  protected final List<List<ItemStack>> inputs;
  protected final List<FluidStack> outputs;
  protected final int temperature;
  protected final List<FluidStack> fuels;

  public EntityMeltingRecipeWrapper(MeltingRecipe recipe) {
    this.inputs = ImmutableList.of(recipe.input.getInputs());
    this.outputs = ImmutableList.of(recipe.getResult());
    this.temperature = recipe.getTemperature();

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
}
