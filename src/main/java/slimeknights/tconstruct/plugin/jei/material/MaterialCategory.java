package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nonnull;

public class MaterialCategory implements IRecipeCategory<MaterialWrapper> {
    public static final String UUID = Util.MODID + ":tool_stats";

    public static final int WIDTH = 182;
    public static final int HEIGHT = 128;
    public static final int OFFSET_X = 6;

    private static final int PARTS = 0;
    private static final int REPRES = 1;
    private static final int FLUID = 2;

    IDrawable background, icon;

    public MaterialCategory(IGuiHelper helper) {
        ResourceLocation icon_location = new ResourceLocation(Util.MODID, "textures/gui/jei/materials.png");

        background = helper.createBlankDrawable(WIDTH, HEIGHT);
        icon = helper.createDrawable(icon_location, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UUID;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return new TextComponentTranslation("gui.jei.material.title").getFormattedText();
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
    public void setRecipe(IRecipeLayout layout, MaterialWrapper wrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        if (wrapper.hasFluid()) {
            layout.getFluidStacks().init(FLUID, true, OFFSET_X + 22, 1);
            layout.getFluidStacks().set(FLUID, wrapper.getFluid());
        }
        ItemStack item = wrapper.material.getRepresentativeItem();
        if (item != null && !item.isEmpty()) {
            stacks.init(REPRES, true, OFFSET_X, 0);
            stacks.set(REPRES, item);
        }
        stacks.init(PARTS, true, background.getWidth() - 18 - OFFSET_X, 0);
        stacks.set(PARTS, wrapper.getParts());
    }
}
