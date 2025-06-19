package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.library.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HarvestCategory extends AbstractCategory{
    public HarvestCategory(IGuiHelper guiHelper) {
        super(guiHelper, Reference.HARVEST_TYPES);
        icon = guiHelper.drawableBuilder(icon_location, 0, 0, 16, 16).setTextureSize(32,32).build();
        title = new TextComponentTranslation("gui.jei.material.harvest").getFormattedText();
        uuid = Util.MODID + ":harvest_stats";
    }

    @Override
    protected List<String> additionalTooltips(int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        LinkedList<String> statInfo = materialWrapper.getStatInfos(relatedParts);
        LinkedList<String> statDescriptions = materialWrapper.getStatDescriptions(relatedParts);
        float height = 4 + HEADING_SPACING;
        if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(0)), height++, mouseX, mouseY)) tooltip.addAll(formatTooltip(statDescriptions.get(0)));
        if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(1)), height++, mouseX, mouseY)) tooltip.addAll(formatTooltip(statDescriptions.get(1)));
        if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(2)), height++, mouseX, mouseY)) tooltip.addAll(formatTooltip(statDescriptions.get(2)));
        if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(3)), height++, mouseX, mouseY)) tooltip.addAll(formatTooltip(statDescriptions.get(3)));
        height += LINE_SPACING + HEADING_SPACING;
        height++;
        if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(4)), height++, mouseX, mouseY)) tooltip.addAll(formatTooltip(statDescriptions.get(4)));
        height += LINE_SPACING + HEADING_SPACING;
        height++;
        if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(5)), height++, mouseX, mouseY)) tooltip.addAll(formatTooltip(statDescriptions.get(5)));
        if (isHovered(0, ClientProxy.fontRenderer.getStringWidth(statInfo.get(6)), height, mouseX, mouseY)) tooltip.addAll(formatTooltip(statDescriptions.get(6)));
        return tooltip;
    }

    @Override
    protected void drawStats(LinkedList<String> statInfo, float lineNumber) {
        String head = getHeading("stat.head.name");
        drawComponent(head, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
        lineNumber += HEADING_SPACING;
        drawStatComponent(statInfo.get(0), lineNumber++);
        drawStatComponent(statInfo.get(1), lineNumber++);
        drawStatComponent(statInfo.get(2), lineNumber++);
        drawStatComponent(statInfo.get(3), lineNumber++);
        lineNumber += LINE_SPACING;

        String extra = getHeading("stat.extra.name");
        drawComponent(extra, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
        lineNumber += HEADING_SPACING;
        drawStatComponent(statInfo.get(4), lineNumber++);
        lineNumber += LINE_SPACING;

        String handle = getHeading("stat.handle.name");
        drawComponent(handle, 0, lineNumber++, materialWrapper.material.materialTextColor, true);
        lineNumber += HEADING_SPACING;
        drawStatComponent(statInfo.get(5), lineNumber++);
        drawStatComponent(statInfo.get(6), lineNumber);
    }
}
