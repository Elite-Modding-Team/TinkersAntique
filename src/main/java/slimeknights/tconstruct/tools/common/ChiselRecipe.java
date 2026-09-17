package slimeknights.tconstruct.tools.common;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.library.utils.ToolHelper;

public abstract class ChiselRecipe extends Impl<IRecipe> implements IRecipe {

  private final Item chisel;

  protected ChiselRecipe(String name, Item chisel) {
    this.chisel = chisel;
    this.setRegistryName(Util.getResource(name));
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    return !getChiseledTool(inv).isEmpty();
  }

  @Nonnull
  @Override
  public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    return getChiseledTool(inv);
  }

  @Nonnull
  private ItemStack getChiseledTool(@Nonnull InventoryCrafting inv) {
    if(!Config.modifierChisels) {
      return ItemStack.EMPTY;
    }

    ItemStack tool = ItemStack.EMPTY;
    boolean hasChisel = false;

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if(stack.isEmpty()) {
        continue;
      }

      if(stack.getItem() == chisel && !hasChisel) {
        hasChisel = true;
        continue;
      }

      if(stack.getItem() instanceof TinkersItem && tool.isEmpty()) {
        tool = stack;
        continue;
      }

      return ItemStack.EMPTY;
    }

    if(!hasChisel || tool.isEmpty()) {
      return ItemStack.EMPTY;
    }

    if(tool.hasTagCompound() && tool.getTagCompound().getBoolean(Tags.NO_CHISEL)) {
      return ItemStack.EMPTY;
    }

    ItemStack output = chiselTool(tool);
    if (output.isEmpty() || ItemStack.areItemStacksEqual(tool, output)) {
      return ItemStack.EMPTY;
    }

    return output;
  }

  @Nonnull
  protected abstract ItemStack chiselTool(ItemStack tool);

  @Nonnull
  protected ItemStack rebuildTool(ItemStack output, NBTTagCompound root) {
    try {
      ToolBuilder.rebuildTool(root, (TinkersItem) output.getItem());
    }
    catch(TinkerGuiException e) {
      return ItemStack.EMPTY;
    }

    // May be slightly redundant with the checks in rebuildTool, but never hurts to be safe
    output.setTagCompound(root);
    if(output.getItemDamage() >= output.getMaxDamage() || ToolHelper.getFreeModifiers(output) < 0) {
      return ItemStack.EMPTY;
    }

    return output;
  }

  @Nonnull
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Nonnull
  @Override
  public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    if(Config.modifierChiselsSingleUse) {
      return remaining;
    }

    for(int i = 0; i < inv.getSizeInventory(); i++) {
      if(inv.getStackInSlot(i).getItem() == chisel) {
        ItemStack chiselStack = inv.getStackInSlot(i).copy();
        chiselStack.setCount(1);
        remaining.set(i, chiselStack);
        return remaining;
      }
    }

    return remaining;
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public boolean isHidden() {
    return true;
  }
}
