package slimeknights.tconstruct.plugin.jei.severing;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nonnull;
import java.util.List;

public class SeveringRecipeCategory implements IRecipeCategory<SeveringRecipeWrapper> {

  public static String CATEGORY = Util.prefix("severing");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/severing.png");

  private final IDrawable background;

  public SeveringRecipeCategory(IGuiHelper guiHelper) {
    background = guiHelper.createDrawable(background_loc, 0, 0, 160, 60, 0, 0, 0, 0);
  }

  @Nonnull
  @Override
  public String getUid() {
    return CATEGORY;
  }

  @Nonnull
  @Override
  public String getTitle() {
    return Util.translate("gui.jei.severing.title");
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, SeveringRecipeWrapper recipeWrapper, IIngredients ingredients) {
    IGuiItemStackGroup items = recipeLayout.getItemStacks();

    items.init(0, true, 43, 17);
    items.set(ingredients);

    items.init(1, false, 97, 17);
    items.set(ingredients);
  }

  @Override
  public List<String> getTooltipStrings(int mouseX, int mouseY) {
    return ImmutableList.of();
  }

  @Override
  public IDrawable getIcon() {
    // use the default icon
    return null;
  }

  @Override
  public String getModName() {
    return TConstruct.modName;
  }

}
