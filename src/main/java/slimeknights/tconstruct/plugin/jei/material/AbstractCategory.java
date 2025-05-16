package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nonnull;

public class AbstractCategory implements IRecipeCategory<MaterialWrapper> {
    ResourceLocation icon_location = new ResourceLocation(Util.MODID, "textures/gui/jei/materials.png");

    protected static final int LINE_HEIGHT = 10;
    protected static final int WIDTH = 178;
    protected static final int HEIGHT = 200;

    private static final int PARTS = 0;
    private static final int REPRES = 1;
    private static final int FLUID = 2;

    String title, uuid;
    IDrawable background, icon;

    public AbstractCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
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
        if (recipeWrapper.hasFluid()) {
            recipeLayout.getFluidStacks().init(FLUID, true, recipeWrapper.slot.getWidth() + 1, 1);
            recipeLayout.getFluidStacks().set(FLUID, recipeWrapper.getFluid());
        }
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        ItemStack item = recipeWrapper.material.getRepresentativeItem();
        if (item != null && !item.isEmpty()) {
            stacks.init(REPRES, true, 0, 0);
            stacks.set(REPRES, item);
        }
        stacks.init(PARTS, true, WIDTH - 16, 0);
        stacks.set(PARTS, recipeWrapper.getParts());
    }
}
