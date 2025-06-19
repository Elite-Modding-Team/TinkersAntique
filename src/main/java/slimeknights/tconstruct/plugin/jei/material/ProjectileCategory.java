package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

import java.util.ArrayList;
import java.util.Collections;
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
    protected List<String> additionalTooltips(int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        LinkedList<String> statInfo = materialWrapper.getStatInfos(relatedParts);
        LinkedList<String> statDescriptions = materialWrapper.getStatDescriptions(relatedParts);
        if (statInfo.size() == 6 && statDescriptions.size() == 6) {
            float height = 4 + HEADING_SPACING;
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(0)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(0)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(1)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(1)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(2)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(2)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(3)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(3)));
            height += LINE_SPACING + HEADING_SPACING;
            height++;
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(4)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(4)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(5)), height, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(5)));
        } else if (statInfo.size() == 4 && statDescriptions.size() == 4) {
            float height = 4 + HEADING_SPACING;
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(0)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(0)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(1)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(1)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(2)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(2)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(3)), height, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(3)));
        } else if (statInfo.size() == 2 && statDescriptions.size() == 2) {
            float height = 4 + HEADING_SPACING;
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(0)), height++, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(0)));
            if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(1)), height, mouseX, mouseY))
                tooltip.addAll(formatTooltip(statDescriptions.get(1)));
        }
        return tooltip;
    }

    @Override
    protected void drawStats(LinkedList<String> statInfo, float lineNumber) {
        if (statInfo.size() == 6) {
            // Head && Arrow Shaft
            String projectile = getHeading("stat.projectile.name");
            drawComponent(projectile, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
            lineNumber += HEADING_SPACING;
            drawStatComponent(statInfo.get(0), lineNumber++);
            drawStatComponent(statInfo.get(1), lineNumber++);
            drawStatComponent(statInfo.get(2), lineNumber++);
            drawStatComponent(statInfo.get(3), lineNumber++);
            lineNumber += LINE_SPACING;

            String shaft = getHeading("stat.shaft.name");
            drawComponent(shaft, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
            lineNumber += HEADING_SPACING;
            drawStatComponent(statInfo.get(4), lineNumber++);
            drawStatComponent(statInfo.get(5), lineNumber);
        } else if (statInfo.size() == 4) {
            // Head
            String projectile = getHeading("stat.projectile.name");
            drawComponent(projectile, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
            lineNumber += HEADING_SPACING;
            drawStatComponent(statInfo.get(0), lineNumber++);
            drawStatComponent(statInfo.get(1), lineNumber++);
            drawStatComponent(statInfo.get(2), lineNumber++);
            drawStatComponent(statInfo.get(3), lineNumber);
        } else if (statInfo.size() == 2) {
            // Arrow Shaft || Fletching
            String shaft = getHeading(statInfo.get(1).contains("%") ? "stat.fletching.name" : "stat.shaft.name");
            drawComponent(shaft, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
            lineNumber += HEADING_SPACING;
            drawStatComponent(statInfo.get(0), lineNumber++);
            drawStatComponent(statInfo.get(1), lineNumber);
        }
    }
}
