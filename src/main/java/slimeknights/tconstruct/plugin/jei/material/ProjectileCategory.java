package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProjectileCategory extends AbstractCategory {
    public ProjectileCategory(IGuiHelper guiHelper) {
        super(guiHelper, Reference.PROJECTILE_TYPES);
        icon = guiHelper.drawableBuilder(icon_location, 16, 16, 16, 16).setTextureSize(32, 32).build();
        title = new TextComponentTranslation("gui.jei.material.projectile").getFormattedText();
        uuid = Util.MODID + ":projectile_stats";
    }

    @Override
    protected List<String> additionalTooltips(List<String> statInfo, List<String> statDesc, int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        float height = 4 + HEADING_SPACING;
        for (int i = 0; i < Math.min(statInfo.size(), statDesc.size()); ++i) {
            if (i == 4) {
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
        String mainHeading = getHeading("stat." + (statInfo.size() != 2 ? "projectile" : statInfo.get(1).contains("%") ? "fletching" : "shaft") + ".name");
        drawComponent(mainHeading, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
        lineNumber += HEADING_SPACING;
        for (int i = 0; i < statInfo.size(); ++i) {
            if (i == 4) {
                lineNumber += LINE_SPACING;
                String shaft = getHeading("stat.shaft.name");
                drawComponent(shaft, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
                lineNumber += HEADING_SPACING;
            }
            drawStatComponent(statInfo.get(i), lineNumber++);
        }
    }
}
