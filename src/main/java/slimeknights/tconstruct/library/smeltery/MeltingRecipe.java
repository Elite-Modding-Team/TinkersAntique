package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.ListUtil;

public class MeltingRecipe {

  private static final double LOG9_2 = 0.31546487678;

  // speed and temperature are inferred automatically through the output
  public final RecipeMatch input;
  public final FluidStack output;
  public final int temperature;

  public MeltingRecipe(RecipeMatch input, Fluid output) {
    this(input, new FluidStack(output, input.amountMatched));
  }

  public MeltingRecipe(RecipeMatch input, FluidStack output) {
    this(input, output, calcTemperature(output.getFluid().getTemperature(output), input.amountMatched));
  }

  public MeltingRecipe(RecipeMatch input, Fluid output, int temperature) {
    this(input, new FluidStack(output, input.amountMatched), temperature);
  }

  public MeltingRecipe(RecipeMatch input, FluidStack output, int temperature) {
    this.input = input;
    this.output = new FluidStack(output, input.amountMatched);
    this.temperature = temperature;
  }

  /** Required time to execute the recpipe, expressed as "temperature", and also the minimum required temp. for this recipe */
  public int getTemperature() {
    return temperature;
  }

  // seriously, who thought kelvin is a good unit for this?
  public int getUsableTemperature() {
    return Math.max(1, temperature - 300);
  }

  public boolean matches(ItemStack stack) {
    return input.matches(ListUtil.getListFrom(stack)).isPresent();
  }

  public FluidStack getResult() {
    return output.copy();
  }

  public MeltingRecipe register() {
    TinkerRegistry.registerMelting(this);
    return this;
  }

  private static int calcTemperature(int temp, int timeAmount) {
    int base = Material.VALUE_Block;
    int max_tmp = Math.max(0, temp - 300); // we use 0 as baseline, not 300
    double f = (double) timeAmount / (double) base;

    // we calculate 2^log9(f), which effectively gives us 2^(1 for each multiple of 9)
    // so 1 = 1, 9 = 2, 81 = 4, 1/9 = 1/2, 1/81 = 1/4 etc
    // we simplify it to f^log9(2) to make calculation simpler
    f = Math.pow(f, LOG9_2);

    return 300 + (int) (f * (double) max_tmp);
  }

  public static MeltingRecipe registerFor(RecipeMatch recipeMatch, Fluid fluid) {
    return new MeltingRecipe(recipeMatch, fluid).register();
  }

  /**
   * Returns a meltingrecipe for the given recipematch, that returns the given fluid-output combination
   * but the temperature required for it is as if timeAmount would be returned.
   */
  public static MeltingRecipe forAmount(RecipeMatch recipeMatch, FluidStack output, int timeAmount) {
    return new MeltingRecipe(recipeMatch, output, calcTemperature(output.getFluid().getTemperature(), timeAmount));
  }

  /**
   * See fluidstack variant
   */
  public static MeltingRecipe forAmount(RecipeMatch recipeMatch, Fluid fluid, int timeAmount) {
    return forAmount(recipeMatch, new FluidStack(fluid, recipeMatch.amountMatched), timeAmount);
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || getClass() != o.getClass()) {
      return false;
    }
    MeltingRecipe that = (MeltingRecipe) o;
    if(temperature != that.temperature) {
      return false;
    }
    if(input != null ? !input.equals(that.input) : that.input != null) {
      return false;
    }
    return output != null ? output.isFluidStackIdentical(that.output) : that.output == null;
  }

  @Override
  public int hashCode() {
    int result = input != null ? input.hashCode() : 0;
    int outputHash = 0;
    if(output != null) {
      outputHash = output.getFluid().hashCode() + output.amount;
      if(output.tag != null) {
        outputHash = 31 * outputHash + output.tag.hashCode();
      }
    }
    result = 31 * result + outputHash;
    result = 31 * result + temperature;
    return result;
  }
}
