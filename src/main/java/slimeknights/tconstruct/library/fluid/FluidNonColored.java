package slimeknights.tconstruct.library.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.Util;

// Mainly for fluids that already have a colored texture
public class FluidNonColored extends Fluid {

  public static ResourceLocation ICON_BlazeStill = Util.getResource("blocks/fluids/colored/molten_blaze");
  public static ResourceLocation ICON_BlazeFlowing = Util.getResource("blocks/fluids/colored/molten_blaze_flow");

  public FluidNonColored(String fluidName, ResourceLocation still, ResourceLocation flowing) {
    super(fluidName, still, flowing);
  }

  @Override
  public String getLocalizedName(FluidStack stack) {
    String s = this.getUnlocalizedName();
    return s == null ? "" : I18n.translateToLocal(s + ".name");
  }
}
