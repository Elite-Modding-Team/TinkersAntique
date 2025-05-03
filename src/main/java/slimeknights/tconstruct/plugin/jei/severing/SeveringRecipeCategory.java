package slimeknights.tconstruct.plugin.jei.severing;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nonnull;
import java.util.List;

public class SeveringRecipeCategory implements IRecipeCategory<SeveringRecipeWrapper> {

  public static String CATEGORY = Util.prefix("severing");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/severing.png");

  private final IDrawable background;
  private final IDrawable icon;

  public SeveringRecipeCategory(IGuiHelper guiHelper) {
	    background = guiHelper.createDrawable(background_loc, 0, 0, 134, 66, 0, 0, 0, 0);
	    icon = guiHelper.createDrawable(background_loc, 134, 0, 16, 16);
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


  @Override
  public IDrawable getIcon() {
  	return icon;
  }
  
  @Nonnull
  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, SeveringRecipeWrapper recipeWrapper, IIngredients ingredients) {
    IGuiItemStackGroup items = recipeLayout.getItemStacks();

    items.init(1, false, 107, 22);
    items.set(ingredients);
  }

  @Override
  public List<String> getTooltipStrings(int mouseX, int mouseY) {
	if (mouseX > 43 && mouseY > 17 && mouseX < 73 && mouseY < 45) {
	  return ImmutableList.of(I18n.format("gui.jei.severing.item_with_beheading"));
	}
    return ImmutableList.of();
  }

  @Override
  public String getModName() {
    return TConstruct.modName;
  }

}
