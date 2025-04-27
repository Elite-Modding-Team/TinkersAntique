package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

import javax.annotation.Nonnull;

public class SlotToolStationOut extends Slot {

  public ContainerToolStation parent;

  public SlotToolStationOut(int index, int xPosition, int yPosition, ContainerToolStation container) {
    super(new InventoryCraftResult(), index, xPosition, yPosition);

    this.parent = container;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return Config.deconstructTools // config enabled
            && !stack.isEmpty() && stack.getItem() instanceof TinkersItem // is tool
            && !stack.isItemDamaged() && !ToolHelper.isBroken(stack) // undamaged
            && parent.getBuildableTools().contains(stack.getItem()) // can be built in the current table
            && parent.getInputSlotContents().isEmpty() // input slots are empty
            && ModifierNBT.readTag(TinkerUtil.getModifierTag(stack, "tconevo.artifact")).level != 1; // is not a sealed artifact
  }

  @Override
  public void putStack(@Nonnull ItemStack stack) {
    super.putStack(stack);
    // trigger craft matrix update and sync when a tool is placed in the output slot
    if(isItemValid(stack)) {
      parent.onCraftMatrixChanged(parent.getTile());
      parent.detectAndSendChanges();
    }
  }

  @Nonnull
  @Override
  public ItemStack onTake(EntityPlayer playerIn, @Nonnull ItemStack stack) {
    FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, parent.getTile());
    parent.onResultTaken(playerIn, stack);
    stack.onCrafting(playerIn.getEntityWorld(), playerIn, 1);

    return super.onTake(playerIn, stack);
  }
}
