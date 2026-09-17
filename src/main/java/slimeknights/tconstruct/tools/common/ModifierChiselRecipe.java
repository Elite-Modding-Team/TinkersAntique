package slimeknights.tconstruct.tools.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.tools.modifiers.ModExtraTrait;
import slimeknights.tconstruct.tools.modifiers.ModReinforced;

public class ModifierChiselRecipe extends ChiselRecipe {

  /** Maximum number of iterations to simulate retained modifier slots, for safety against malformed NBT */
  static final int MAXIMUM_SIMULATION_ITERATIONS = 10000;

  public ModifierChiselRecipe(String name, Item chisel) {
    super(name, chisel);
  }

  @Nonnull
  @Override
  protected ItemStack chiselTool(ItemStack tool) {
    ItemStack output = tool.copy();
    NBTTagCompound root = TagUtil.getTagSafe(output);
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(root);
    NBTTagList retainedModifiers = new NBTTagList();
    NBTTagList protectedModifiers = new NBTTagList();
    boolean removedModifier = false;

    for(int i = 0; i < modifiers.tagCount(); i++) {
      String identifier = modifiers.getStringTagAt(i);
      IModifier modifier = TinkerRegistry.getModifier(identifier);
      if(isEmbossment(identifier, modifier)) {  // always preserve embossments
        retainedModifiers.appendTag(new NBTTagString(identifier));
      }
      else if(Config.modifierChiselModifierBlacklist.contains(identifier)) {
        retainedModifiers.appendTag(new NBTTagString(identifier));
        protectedModifiers.appendTag(new NBTTagString(identifier));
      }
      else {
        if(modifier instanceof ModReinforced && ModReinforced.isUnbreakable(TinkerUtil.getModifierTag(root, identifier))) {
          // The Unbreakable tag is not cleaned in RebuildTool, so we need to clean it here
          // We only remove it if it was introduced by the Reinforced modifier,
          // to not remove any Unbreakable tag that was set manually
          root.removeTag(ModReinforced.TAG_UNBREAKABLE);
        }
        removedModifier = true;
      }
    }

    if(!removedModifier) {
      return ItemStack.EMPTY;
    }

    int retainedSlots = simulateRetainedModifierSlots(root, protectedModifiers,
        (TinkersItem) output.getItem());
    if(retainedSlots < 0) {
      return ItemStack.EMPTY;
    }

    TagUtil.setBaseModifiersTagList(root, retainedModifiers);
    TagUtil.setBaseModifiersUsed(root, retainedSlots);
    return rebuildTool(output, root);
  }

  private boolean isEmbossment(String identifier, IModifier modifier) {
    return modifier instanceof ModExtraTrait || identifier.startsWith(ModExtraTrait.EXTRA_TRAIT_IDENTIFIER);
  }

  /**
   * Estimates how many modifier slots are required to preserve the given modifiers,
   * to know how many slots we can refund.
   * @return The number of slots used by the given modifiers, or -1 if the modifiers cannot be applied to the tool
   */
  private int simulateRetainedModifierSlots(NBTTagCompound root, NBTTagList modifiers, TinkersItem item) {
    if(modifiers.tagCount() == 0) {
      return 0;
    }

    int usedModifiers = TagUtil.getBaseModifiersUsed(root);
    int freeModifiers = TagUtil.getToolTag(root).getInteger(Tags.FREE_MODIFIERS);
    if(usedModifiers < 0 || freeModifiers < 0 || freeModifiers > Integer.MAX_VALUE - usedModifiers) {
      return -1;
    }

    int totalModifiers = freeModifiers + usedModifiers;
    NBTTagCompound simulation = root.copy();
    TagUtil.setBaseModifiersTagList(simulation, new NBTTagList());
    TagUtil.setModifiersTagList(simulation, new NBTTagList());
    TagUtil.setBaseModifiersUsed(simulation, 0);

    try {
      ToolBuilder.rebuildTool(simulation, item);
    }
    catch(TinkerGuiException e) {  // shouldn't happen
      return -1;
    }

    NBTTagCompound simulationToolTag = TagUtil.getToolTag(simulation);
    simulationToolTag.setInteger(Tags.FREE_MODIFIERS, totalModifiers);
    TagUtil.setToolTag(simulation, simulationToolTag);

    for(int i = 0; i < modifiers.tagCount(); i++) {
      String identifier = modifiers.getStringTagAt(i);
      IModifier modifier = TinkerRegistry.getModifier(identifier);
      if(modifier == null || !applyUntilMatching(root, simulation, identifier, modifier, usedModifiers)) {
        TConstruct.log.debug(String.format("ModifierChiselRecipe: failed to simulate modifier %s for tool %s. " +
          "Had %d total modifier slots (%d used, %d free), but could not apply modifier to match original.",
          identifier, item.getRegistryName(), totalModifiers, usedModifiers, freeModifiers));
        return -1;
      }
    }

    return TagUtil.getBaseModifiersUsed(simulation);
  }

  /**
   * Applies the given modifier to the simulation until the modifier tag matches the original tool,
   * the simulation stops changing, or the maximum number of slots is exceeded. Modifiers may be
   * applied multiple times, and consume a variable number of slots for each level.
   * <p>
   * Notable examples :
   * <ul>
   *   <li>Tool leveling: no slot used whatsoever</li>
   *   <li>Reinforced : 1 slot per level</li>
   *   <li>Luck : 1 slot total for all levels</li>
   * </ul>
   */
  private boolean applyUntilMatching(NBTTagCompound root, NBTTagCompound tag, String identifier,
                                     IModifier modifier, int usedModifiers) {
    NBTTagCompound modifierTag = TinkerUtil.getModifierTag(root, identifier);
    if(modifierTag.hasNoTags()) {
      modifier.apply(tag);
      if(TagUtil.getBaseModifiersUsed(tag) > usedModifiers) {
        TConstruct.log.debug("ModifierChiselRecipe: somehow exceeded modifier slots count.");
        return false;
      }
      return true;
    }

    NBTTagCompound lastTag = null;
    int initialUsedModifiers = TagUtil.getBaseModifiersUsed(tag);
    int lastUsedModifiers = -1;
    for(int i = 0; i < MAXIMUM_SIMULATION_ITERATIONS; i++) {
      NBTTagCompound simulatedTag = TinkerUtil.getModifierTag(tag, identifier);
      int simulatedUsedModifiers = TagUtil.getBaseModifiersUsed(tag);
      if(modifier.equalModifier(modifierTag, simulatedTag)) {
        return true;
      }

      if(lastTag != null && lastTag.equals(simulatedTag) && lastUsedModifiers == simulatedUsedModifiers) {
        TConstruct.log.debug(String.format(
          "ModifierChiselRecipe: modifier %s stopped changing before matching original. " +
            "Original: %s, simulated: %s", identifier, modifierTag, simulatedTag));
        return simulatedUsedModifiers == initialUsedModifiers;
      }
      lastTag = simulatedTag.copy();
      lastUsedModifiers = simulatedUsedModifiers;

      modifier.apply(tag);
      if(TagUtil.getBaseModifiersUsed(tag) > usedModifiers) {
        TConstruct.log.debug("ModifierChiselRecipe: somehow exceeded modifier slots count.");
        return false;
      }
    }

    TConstruct.log.debug(String.format(
      "ModifierChiselRecipe: exceeded maximum simulation iterations."));

    return false;
  }
}
