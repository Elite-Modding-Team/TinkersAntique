package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;

public class ModIncognito extends ToolModifier {

  public ModIncognito() {
    super("incognito", 0x575757);

    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this));
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // nothing to do :(
  }
}
