package slimeknights.tconstruct.plugin.jei.material;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.text.WordUtils;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCategory implements IRecipeCategory<MaterialWrapper> {
    protected ResourceLocation icon_location = new ResourceLocation(Util.MODID, "textures/gui/jei/materials.png");

    protected static final int LINE_HEIGHT = 10;
    protected static final float LINE_SPACING = 0.5f;
    protected static final float HEADING_SPACING = 0.1f;
    protected static final int WIDTH = 178;
    protected static final int HEIGHT = 150;

    private static final int PARTS = 0;
    private static final int REPRES = 1;
    private static final int FLUID = 2;

    private static final int BUTTON_WIDTH = 18;
    private static final int BUTTON_HEIGHT = 18;

    protected String title, uuid;
    protected int mouseX, mouseY, btn_id;
    protected IDrawable background, icon, slot;
    protected MaterialWrapper materialWrapper;
    protected final List<String> relatedParts;

    public AbstractCategory(IGuiHelper guiHelper, List<String> parts) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.slot = guiHelper.getSlotDrawable();
        this.relatedParts = parts;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public String getUid() {
        return uuid;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    @Nonnull
    @Override
    public String getModName() {
        return TConstruct.modName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull MaterialWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
        materialWrapper = recipeWrapper;
        if (recipeWrapper.material.hasFluid()) {
            recipeLayout.getFluidStacks().init(FLUID, true, slot.getWidth() + 1, 1);
            recipeLayout.getFluidStacks().set(FLUID, new FluidStack(recipeWrapper.material.getFluid(), 1000));
        }
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        ItemStack item = recipeWrapper.material.getRepresentativeItem();
        if (item != null && !item.isEmpty()) {
            stacks.init(REPRES, true, 0, 0);
            stacks.set(REPRES, item);
        }
        int xOff = Config.jeiGuidebookButton ? BUTTON_WIDTH * 2 : BUTTON_WIDTH;
        stacks.init(PARTS, true, WIDTH - xOff, 0);
        stacks.set(PARTS, recipeWrapper.getParts(relatedParts));
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        ItemStack item = materialWrapper.getMaterial().getRepresentativeItem();
        if (item != null && !item.isEmpty()) {
            int xOff = Config.jeiGuidebookButton ? BUTTON_WIDTH * 2 : BUTTON_WIDTH;
            slot.draw(minecraft, WIDTH - xOff, 0);
        }
        slot.draw(minecraft, 0, 0);
        if (materialWrapper.getMaterial().hasFluid()) {
            slot.draw(minecraft, slot.getWidth(), 0);
        }
        float lineNumber = 0.0f;
        drawComponentShadowCentered(TextFormatting.UNDERLINE + materialWrapper.material.getLocalizedName() + TextFormatting.RESET, lineNumber, materialWrapper.material.materialTextColor);
        lineNumber += 3;
        drawTraits(materialWrapper.getTraits(relatedParts), lineNumber);
        drawStats(materialWrapper.getStatInfos(relatedParts), lineNumber);
        if (Config.jeiGuidebookButton) {
          getGuideButton().drawButton(minecraft, mouseX, mouseY, 0);
        }
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        List<String> tooltip = new ArrayList<>();
        LinkedList<ITrait> traits = materialWrapper.getTraits(relatedParts);
        traits.forEach(iTrait -> {
            int index = traits.indexOf(iTrait);
            int width = ClientProxy.fontRenderer.getStringWidth(iTrait.getLocalizedName());
            if (isHovered(WIDTH - width, WIDTH, 3 + index, mouseX, mouseY)) {
                // Use the same method to split the description as the guide book.
                String[] desc = LocUtils.convertNewlines(iTrait.getLocalizedDesc()).split("\n");
                if (desc.length > 1) {
                    // It seems that some materials have a trait description that is just a single line.
                    String title = TextFormatting.GOLD + desc[0] + TextFormatting.RESET;
                    tooltip.add(title);
                    tooltip.addAll(formatTooltip(desc[1]));
                } else {
                    // If the description is a single line, just add it directly.
                    tooltip.addAll(formatTooltip(desc[0]));
                }
            }
        });
        tooltip.addAll(additionalTooltips(materialWrapper.getStatInfos(relatedParts), materialWrapper.getStatDescriptions(relatedParts), mouseX, mouseY));

        if (Config.jeiGuidebookButton && getGuideButton().mousePressed(mouseX, mouseY)) {
            tooltip.add(getGuideButton().getTooltip());
        }
        return tooltip;
    }

    protected GuideButton getGuideButton() {
        if (materialWrapper.guideButton == null) {
            materialWrapper.guideButton = new GuideButton(btn_id++, WIDTH - BUTTON_WIDTH, 0, BUTTON_WIDTH, BUTTON_HEIGHT, uuid, materialWrapper.material.identifier, materialWrapper.getUseableParts(relatedParts), new ItemStack(TinkerCommons.book));
        }
        return materialWrapper.guideButton;
    }

    protected abstract List<String> additionalTooltips(List<String> statInfo, List<String> statDesc, int mouseX, int mouseY);

    protected abstract void drawStats(LinkedList<String> statInfo, float lineNumber);

    protected final void drawComponent(String component, float x, float lineNumber, int color, boolean shadow) {
        ClientProxy.fontRenderer.drawString(component, x, lineNumber * LINE_HEIGHT, color, shadow);
    }

    protected final void drawStatComponent(String component, float lineNumber) {
        int color = 0xfff0f0f0;
        String category = component.split(": ")[0] + ": ";
        String value = component.split(": ")[1];
        int width = ClientProxy.fontRenderer.getStringWidth(category);
        drawComponent(category, 0, lineNumber, color, true);
        drawComponent(value, width, lineNumber, Color.GREEN.getRGB(), true);
    }

    protected final void drawComponentShadowCentered(String component, float lineNumber, int color) {
        drawComponent(component, (WIDTH - ClientProxy.fontRenderer.getStringWidth(component)) / 2f, lineNumber, color, true);
    }

    protected final void drawTraits(List<ITrait> traits, float lineNumber) {
        for (ITrait trait : traits) {
            final String component = trait.getLocalizedName();
            final int width = ClientProxy.fontRenderer.getStringWidth(component);
            final int color = materialWrapper.material.materialTextColor;
            drawComponent(component, WIDTH - width, lineNumber++, color, true);
        }
    }

    protected final String getHeading(String langKey) {
        return TextFormatting.UNDERLINE + I18n.format(langKey) + TextFormatting.RESET;
    }

    protected boolean isHovered(int left, int right, float y, int mouseX, int mouseY) {
        return left <= mouseX && mouseX < right && y * LINE_HEIGHT <= mouseY && mouseY < y * LINE_HEIGHT + LINE_HEIGHT;
    }

    protected List<String> formatTooltip(String tooltip) {
        return Arrays.asList(WordUtils.wrap(tooltip, 40).replace("\\n", " ").split("\\r\\n"));
    }
}
