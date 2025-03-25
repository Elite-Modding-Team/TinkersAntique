package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotPart extends Slot {

  public Container parent;

  public SlotPart(IInventory inventory, int index, int xPosition, int yPosition, Container parent) {
    super(inventory, index, xPosition, yPosition);
    this.parent = parent;
  }

  @Override
  public void onSlotChanged() {
    // notify container to update craft result
    parent.onCraftMatrixChanged(inventory);
  }
}
