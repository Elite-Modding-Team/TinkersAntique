package slimeknights.tconstruct.plugin.jei.entitymelting;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;

public class EntityMeltingRecipeCategory implements IRecipeCategory<EntityMeltingRecipeWrapper> {

	public static String CATEGORY = Util.prefix("entitymelting");
	public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/smeltery2.png");

	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable tankOverlay;

	public EntityMeltingRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(background_loc, 0, 0, 150, 62);
		icon = guiHelper.createDrawable(background_loc, 174, 0, 16, 16);
		tankOverlay = guiHelper.createDrawable(background_loc, 150, 33, 16, 16);
	}

	@Nonnull
	@Override
	public String getUid() {
		return CATEGORY;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return Util.translate("gui.jei.entitymelting.title");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {

	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, EntityMeltingRecipeWrapper recipe, IIngredients ingredients) {
		IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
		fluids.addTooltipCallback((slotIndex, input, fluid, text) -> {
			if (slotIndex == 0) {
				// first, store the original name and mod name from JEI
				String ingredientName = text.get(0);
				String modName = text.get(text.size() - 1);
				text.clear();
				text.add(ingredientName);

				// next, determine smeltery amounts to show
				int amount = fluid.amount;
				
				// we always show mbs
				GuiUtil.calcLiquidText(amount, 1, Util.translate("gui.smeltery.liquid.millibucket") + " / " + Util.translate("gui.jei.smeltery_damage.info"), text);

				// add mod name back
				text.add(modName);
			} else
				GuiUtil.onFluidTooltip(slotIndex, input, fluid, text);
		});

		fluids.init(0, false, 115, 11, 16, 32, recipe.outputAmount, false, null);
		fluids.set(ingredients);

		fluids.init(1, false, 75, 43, 16, 16, 1000, false, tankOverlay);
		fluids.set(1, recipe.fuels);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return ImmutableList.of();
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public String getModName() {
		return TConstruct.modName;
	}
}
