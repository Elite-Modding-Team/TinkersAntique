package slimeknights.tconstruct.library;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SeveringRecipe {

  public final ResourceLocation input;
  public final ItemStack output;
  public final ItemStack spawnEgg;

  public SeveringRecipe(ResourceLocation input, ItemStack output, ItemStack spawnEgg) {
    this.input = input;
    this.output = output;
    this.spawnEgg = spawnEgg;
  }

  public ItemStack getResult() {
    return output.copy();
  }
}
