package slimeknights.tconstruct.tools.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nullable;
import java.util.List;

public class ModifierChisel extends Item {

  public ModifierChisel() {
    super();
    this.setCreativeTab(TinkerRegistry.tabTools);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.addAll(LocUtils.getTooltips(Util.translate("item.tconstruct.modifier_chisel.tooltip")));
    tooltip.add(Util.translate(Config.modifierChiselsSingleUse ? "item.tconstruct.chisel.single_use"
                                                               : "item.tconstruct.chisel.reusable"));
  }
}
