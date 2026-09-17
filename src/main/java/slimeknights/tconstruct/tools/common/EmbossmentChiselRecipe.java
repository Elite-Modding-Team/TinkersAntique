package slimeknights.tconstruct.tools.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.modifiers.ModExtraTrait;

public class EmbossmentChiselRecipe extends ChiselRecipe {

  public EmbossmentChiselRecipe(String name, Item chisel) {
    super(name, chisel);
  }

  @Nonnull
  @Override
  protected ItemStack chiselTool(ItemStack tool) {
    ItemStack output = tool.copy();
    NBTTagCompound root = TagUtil.getTagSafe(output);
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(root);
    NBTTagList retainedModifiers = new NBTTagList();
    boolean removedEmbossment = false;

    for(int i = 0; i < modifiers.tagCount(); i++) {
      String identifier = modifiers.getStringTagAt(i);
      IModifier modifier = TinkerRegistry.getModifier(identifier);
      if(shouldRemove(modifier)) {
        removedEmbossment = true;
      }
      else {
        retainedModifiers.appendTag(new NBTTagString(identifier));
      }
    }

    if(!removedEmbossment) {
      return ItemStack.EMPTY;
    }

    // embossments are not supposed to take up modifier slots, so we don't need to refund any
    TagUtil.setBaseModifiersTagList(root, retainedModifiers);
    return rebuildTool(output, root);
  }

  private boolean shouldRemove(IModifier modifier) {
    if(!(modifier instanceof ModExtraTrait)) {
      return false;
    }

    String material = ((ModExtraTrait) modifier).getMaterialIdentifier();
    return !Config.embossmentChiselMaterialBlacklist.contains(material);
  }
}
