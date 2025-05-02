package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.utils.ListUtil;

public class CastingRecipe implements ICastingRecipe {

  public final RecipeMatch cast;
  protected final FluidStack fluid;
  protected final ItemStack output;
  protected final int time; // ticks to cool down
  protected final boolean consumesCast;
  protected final boolean switchOutputs; // switches cast and output. Mostly used for cast creation

  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount) {
    this(output, cast, fluid, amount, calcCooldownTime(fluid, amount));
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount, int time) {
    this(output, cast, new FluidStack(fluid, amount), time, false, false);
  }

  public CastingRecipe(ItemStack output, Fluid fluid, int amount, int time) {
    this(output, null, new FluidStack(fluid, amount), time, false, false);
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, Fluid fluid, int amount, boolean consumesCast, boolean switchOutputs) {
    this(output, cast, new FluidStack(fluid, amount), calcCooldownTime(fluid, amount), consumesCast, switchOutputs);
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, FluidStack fluid, boolean consumesCast, boolean switchOutputs) {
    this(output, cast, fluid, calcCooldownTime(fluid.getFluid(), fluid.amount), consumesCast, switchOutputs);
  }

  public CastingRecipe(ItemStack output, RecipeMatch cast, FluidStack fluid, int time, boolean consumesCast, boolean switchOutputs) {
    if(output == null || output.isEmpty()) {
      throw new TinkerAPIException("Casting Recipe is missing an output!");
    }
    else if(fluid == null) {
      throw new TinkerAPIException(String.format("Casting Recipe for %s has no fluid!", output.getDisplayName()));
    }

    this.output = output;
    this.cast = cast;
    this.fluid = fluid;
    this.time = time;
    this.consumesCast = consumesCast;
    this.switchOutputs = switchOutputs;
  }

  @Override
  public boolean matches(ItemStack cast, Fluid fluid) {
    if((cast.isEmpty() && this.cast == null) || (this.cast != null && this.cast.matches(ListUtil.getListFrom(cast)).isPresent())) {
      return this.fluid.getFluid() == fluid;
    }
    return false;
  }

  @Override
  public ItemStack getResult(ItemStack cast, Fluid fluid) {
    return getResult().copy();
  }

  @Override
  public int getTime() {
    return time;
  }

  @Override
  public boolean consumesCast() {
    return consumesCast;
  }

  @Override
  public int getFluidAmount() {
    return fluid.amount;
  }

  @Override
  public boolean switchOutputs() {
    return switchOutputs;
  }

  @Override
  public FluidStack getFluid(ItemStack cast, Fluid fluid) {
    return this.fluid;
  }

  // JEI stuff
  public ItemStack getResult() {
    return output;
  }

  public FluidStack getFluid() {
    return fluid;
  }


  public static int calcCooldownTime(Fluid fluid, int amount) {
    // minimum time = faucet animation time :I
    int time = 24;
    int temperature = fluid.getTemperature() - 300;

    return time + (temperature * amount) / 1600;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || getClass() != o.getClass()) {
      return false;
    }
    CastingRecipe that = (CastingRecipe) o;
    if(time != that.time) {
      return false;
    }
    if(consumesCast != that.consumesCast) {
      return false;
    }
    if(switchOutputs != that.switchOutputs) {
      return false;
    }
    if(cast != null ? !cast.equals(that.cast) : that.cast != null) {
      return false;
    }
    if(fluid != null ? !fluid.isFluidStackIdentical(that.fluid) : that.fluid != null) {
      return false;
    }
    return output != null ? output.isItemEqual(that.output) && ItemStack.areItemStackTagsEqual(output, that.output) : that.output == null;
  }

  @Override
  public int hashCode() {
    int result = cast != null ? cast.hashCode() : 0;
    int fluidHash = 0;
    if(fluid != null) {
      fluidHash = fluid.getFluid().hashCode() + fluid.amount;
      if(fluid.tag != null) {
        fluidHash = 31 * fluidHash + fluid.tag.hashCode();
      }
    }
    result = 31 * result + fluidHash;
    result = 31 * result + (output != null ? output.getItem().hashCode() + output.getMetadata() + (output.getTagCompound() != null ? output.getTagCompound().hashCode() : 0) : 0);
    result = 31 * result + time;
    result = 31 * result + (consumesCast ? 1 : 0);
    result = 31 * result + (switchOutputs ? 1 : 0);
    return result;
  }
}
