package slimeknights.tconstruct.plugin.jei.severing;

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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.SeveringRecipe;
import slimeknights.tconstruct.tools.melee.TinkerMeleeWeapons;

public class SeveringRecipeWrapper implements IRecipeWrapper {

	protected final List<List<ItemStack>> input;
	protected final List<ItemStack> output;
	protected final ResourceLocation entity;

	private String configStr;
	private double configScale;

	private World curWorld;
	private EntityLivingBase entityInst;

	private boolean entityErrored = false;
	protected double renderScale = -1;

	public SeveringRecipeWrapper(SeveringRecipe recipe) {
		this.input = ImmutableList.of(ImmutableList.of(recipe.spawnEgg));
		this.output = ImmutableList.of(recipe.getResult());
		this.entity = recipe.input;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		ingredients.setOutputs(ItemStack.class, output);
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
			this.renderScale = -1;
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
			int y = 45;
			
			GlStateManager.enableDepth();

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			if (configStr != null) {
				GlStateManager.scale(configScale, configScale, configScale);
			}
			GuiInventory.drawEntityOnScreen(0, 0, (int) Math.round(renderScale), -100, 0, entityInst);
			GlStateManager.popMatrix();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); // I HATE YOU SPIDER EYES!!!!!
		}

		if (Config.fancyJEIBeheadingAnimation) {
			float theta = (float) (Math.PI / 2 * ((minecraft.world.getTotalWorldTime() + minecraft.getRenderPartialTicks()) / 10f + 5));
			double movementX = Math.sin(theta); // Yes i know sine is supposed to be y usually but its out of phase. shut up math majors grrr ik what im doing
			double movementY = Math.cos(theta);

			double x = 50 + movementX * 16;
			double y = 35 + movementY * 8;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			GlStateManager.rotate((float) (120 * movementX), 0, 0, 1);
			GlStateManager.disableDepth();
			SeveringRecipeCategory.instance.icon.draw(minecraft, 0, -16);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		} else {
			GlStateManager.disableDepth();
			SeveringRecipeCategory.instance.icon.draw(minecraft, 50, 23);
			GlStateManager.enableDepth();
		}
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (mouseX > 9 && mouseY > 8 && mouseX < 42 && mouseY < 57 && entityInst != null) {
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
