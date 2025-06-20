package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HarvestCategory extends AbstractCategory {
    public HarvestCategory(IGuiHelper guiHelper) {
        super(guiHelper, Reference.HARVEST_TYPES);
        icon = guiHelper.drawableBuilder(icon_location, 0, 0, 16, 16).setTextureSize(32, 32).build();
        title = new TextComponentTranslation("gui.jei.material.harvest").getFormattedText();
        uuid = Util.MODID + ":harvest_stats";
    }

    @Override
    protected List<String> additionalTooltips(List<String> statInfo, List<String> statDesc, int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        float height = 4 + HEADING_SPACING;
        for (int i = 0; i < Math.min(statInfo.size(), statDesc.size()); ++i) {
            if (i == 4 || i == 5) {
                height += LINE_SPACING + HEADING_SPACING;
                height++;
            }
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(i)), height++, mouseX, mouseY)) {
                tooltip.addAll(formatTooltip(statDesc.get(i)));
            }
        }
        return tooltip;
    }

    @Override
    protected void drawStats(LinkedList<String> statInfo, float lineNumber) {
        String[] header = new String[]{
                getHeading("stat.head.name"),
                getHeading("stat.extra.name"),
                getHeading("stat.handle.name")
        };
        int index = 0;

        for (int i = 0; i < statInfo.size(); ++i) {
            if (i == 4 || i == 5) {
                lineNumber += LINE_SPACING;
            }
            if (i == 0 || i == 4 || i == 5) {
                drawComponent(header[index], 0, lineNumber++, materialWrapper.material.materialTextColor, true);
                lineNumber += HEADING_SPACING;
                index++;
            }
            drawStatComponent(statInfo.get(i), lineNumber++);
        }
    }
}
