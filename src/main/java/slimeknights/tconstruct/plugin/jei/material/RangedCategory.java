package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RangedCategory extends AbstractCategory {
    public RangedCategory(IGuiHelper guiHelper) {
        super(guiHelper, Reference.RANGED_TYPES);
        icon = guiHelper.drawableBuilder(icon_location, 16, 0, 16, 16).setTextureSize(32, 32).build();
        title = new TextComponentTranslation("gui.jei.material.ranged").getFormattedText();
        uuid = Util.MODID + ":ranged_stats";
    }

    @Override
    protected List<String> additionalTooltips(List<String> statInfo, List<String> statDesc, int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        float height = 4 + HEADING_SPACING;
        for (int i = 0; i < Math.min(statInfo.size(), statDesc.size()); ++i) {
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(i)), height++, mouseX, mouseY)) {
                tooltip.addAll(formatTooltip(statDesc.get(i)));
            }
        }
        return tooltip;
    }

    @Override
    protected void drawStats(LinkedList<String> statInfo, float lineNumber) {
        String heading = getHeading(statInfo.size() >= 2 ? "stat.bow.name" : "stat.bowstring.name");
        drawComponent(heading, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
        lineNumber += HEADING_SPACING;
        for (String s : statInfo) {
            drawStatComponent(s, lineNumber++);
        }
    }
}
