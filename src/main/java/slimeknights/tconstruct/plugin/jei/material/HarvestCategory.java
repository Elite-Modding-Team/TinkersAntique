package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.library.Util;

public class HarvestCategory extends AbstractCategory{
    public HarvestCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        icon = guiHelper.drawableBuilder(icon_location, 0, 0, 16, 16).setTextureSize(32,32).build();
        title = new TextComponentTranslation("gui.jei.material.harvest").getFormattedText();
        uuid = Util.MODID + ":harvest_stats";
    }
}
