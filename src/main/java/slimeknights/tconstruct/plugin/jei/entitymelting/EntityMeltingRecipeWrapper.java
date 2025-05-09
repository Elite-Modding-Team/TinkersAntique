package slimeknights.tconstruct.plugin.jei.entitymelting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.EntityMeltingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;

public class EntityMeltingRecipeWrapper implements IRecipeWrapper {

	protected double renderScale = -1;
	protected final ResourceLocation entity;
	protected final List<FluidStack> outputs;
	protected final List<FluidStack> fuels;
	protected final List<List<ItemStack>> inputs;

	private String configStr;
	private double configScale;

	private World curWorld;
	private EntityLivingBase entityInst;

	private boolean entityErrored = false;
	public int outputAmount;

	public EntityMeltingRecipeWrapper(EntityMeltingRecipe recipe) {
		this.entity = recipe.getEntity();
		FluidStack result = recipe.getResultStack();
		outputAmount = result.amount;
		this.outputs = ImmutableList.of(result);
		this.inputs = ImmutableList.of(ImmutableList.of(recipe.spawnEgg));

		ImmutableList.Builder<FluidStack> builder = ImmutableList.builder();
		for (FluidStack fs : TinkerRegistry.getSmelteryFuels()) {
			fs = fs.copy();
			fs.amount = 1000;
			builder.add(fs);
		}
		fuels = builder.build();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(FluidStack.class, outputs);
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if (minecraft.world != null && minecraft.world != curWorld) {
			curWorld = minecraft.world;
			if (entityInst == null && !entityErrored) {
				try {
					Class<? extends EntityLivingBase> entityClass = EntityList.getClass(entity).asSubclass(EntityLivingBase.class);
					entityInst = entityClass.getConstructor(World.class).newInstance(minecraft.world);
				} catch (Exception e) {
					e.printStackTrace();
					entityErrored = true;
				}
			}
		}
		if (entityInst != null) {
			if ((Minecraft.getSystemTime() / 500) % 4 == 0) {
				entityInst.hurtTime = 100;
			} else {
				entityInst.hurtTime = 0;
			}
			if (this.renderScale < 0) {
				double width = entityInst.width;
				double height = entityInst.height;

				if (width > height) {
					double scaleFact = 13;
					this.renderScale = scaleFact / width;
				} else {
					double scaleFact = 32;
					this.renderScale = scaleFact / height;
				}
			}

			for (String scale : Config.entityJEIRendererTransformation) {
				String[] split = scale.split(";");
				// yes i am testing reference equality
				if (configStr != scale && split.length == 2 && entity.toString().equalsIgnoreCase(split[0])) {
					try {
						this.configScale = Double.parseDouble(split[1]);
					} catch (NumberFormatException e) {
					}
					this.configStr = scale;
					break;
				}
			}

			
			int x = 35;
			int y = 40;

			GlStateManager.enableDepth();

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			if (configStr != null) {
				GlStateManager.scale(configScale, configScale, configScale);
			}
			GuiInventory.drawEntityOnScreen(0, 0, (int) Math.round(renderScale), -100, 0, entityInst);
			GlStateManager.popMatrix();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); // I HATE YOU SPIDER EYES!!!!!
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); // I HATE YOU SPIDER EYES!!!!!
		}
		int x = 80 - minecraft.fontRenderer.getStringWidth("-") / 2;
		minecraft.fontRenderer.drawStringWithShadow("-", x, 7, Color.red.getRGB());
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (mouseX > 21 && mouseY > 5 && mouseX < 49 && mouseY < 41 && entityInst != null) {
			List<String> list = new ArrayList<>();
			list.add(entityInst.getName());
			ModContainer mod = Loader.instance().getIndexedModList().get(entity.getResourceDomain());
			if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
				list.add(TextFormatting.DARK_GRAY + entity.toString());
			}
			list.add(TextFormatting.BLUE + "" + TextFormatting.ITALIC + (mod == null ? "Unknown" : mod.getName()));
			return list;
		}
		return ImmutableList.of();
	}
}
