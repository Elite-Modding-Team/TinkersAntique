package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.text.WordUtils;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.traits.ITrait;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MaterialWrapper implements IRecipeWrapper {
    final Material material;
    final IDrawable slot;

    final HashMap<Integer, ITrait> traitList = new HashMap<>();

    public MaterialWrapper(Material material, IGuiHelper helper) {
        this.material = material;
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, getRepresantives());
        ingredients.setOutputs(VanillaTypes.ITEM, getRepresantives());
        if (material.hasFluid()) {
            ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(new FluidStack(material.getFluid(), 1)));
            ingredients.setOutputs(VanillaTypes.FLUID, Collections.singletonList(new FluidStack(material.getFluid(), 1)));
        }
    }

    public List<ItemStack> getRepresantives() {
        ArrayList<ItemStack> list = new ArrayList<>();
        if (material.hasFluid()) {
            for (MeltingRecipe recipe : TinkerRegistry.getAllMeltingRecipies()) {
                if (material.getFluid().equals(recipe.output.getFluid())) {
                    list.addAll(recipe.input.getInputs());
                }
            }
        }
        if (material.getRepresentativeItem() != null && !material.getRepresentativeItem().isEmpty()) {
            list.add(material.getRepresentativeItem());
        }
        list.addAll(getParts());
        return list;
    }

    public List<ItemStack> getParts() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (IToolPart part : TinkerRegistry.getToolParts()) {
            if (part.canUseMaterial(material)) {
                ItemStack stack = part.getItemstackWithMaterial(material);
                if (!stack.equals(material.getShard())) {
                    list.add(part.getItemstackWithMaterial(material));
                }
            }
        }
        return list;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        ItemStack item = material.getRepresentativeItem();
        if (item != null && !item.isEmpty()) {
            slot.draw(minecraft, recipeWidth - 16, 0);
        }
        slot.draw(minecraft, 0, 0);
        if (material.hasFluid()) {
            slot.draw(minecraft, slot.getWidth(), 0);
        }

        minecraft.fontRenderer.drawString(material.getLocalizedName(), (recipeWidth - minecraft.fontRenderer.getStringWidth(material.getLocalizedName())) / 2.0F, 4, material.materialTextColor, true);

        int h = minecraft.fontRenderer.FONT_HEIGHT;
        int offset = 22;

        if (!material.getAllTraits().isEmpty()) {
            ArrayList<ITrait> traits = new ArrayList<>();
            for (ITrait trait : material.getAllTraits())
                if (trait != null && trait.getLocalizedName() != null && !traits.contains(trait))
                    traits.add(trait);

            traitList.clear();
            int y = 0;

            for (ITrait trait : traits) {
                if (trait != null && !traitList.containsValue(trait)) {
                    traitList.put(y, trait);
                    y++;
                }
            }

            traitList.forEach((row, trait) -> {
                int w = minecraft.fontRenderer.getStringWidth(trait.getLocalizedName());
                minecraft.fontRenderer.drawString(trait.getLocalizedName(), (recipeWidth - w) / 2.0F, 22 + h * row, material.materialTextColor, true);
            });

            offset += (h + 1) * traitList.size();
        }

        int maxLines = (recipeHeight - offset - 4) / h - 1;
        ArrayList<IMaterialStats> stats = new ArrayList<>(material.getAllStats());
        stats.removeIf(stat -> stat.getLocalizedInfo().isEmpty());
        IMaterialStats stat = stats.get((int) (System.currentTimeMillis() / 2000 % stats.size()));

        ArrayList<String> lines = new ArrayList<>();
        lines.add(TextFormatting.BOLD + stat.getLocalizedName() + ": ");
        lines.addAll(stat.getLocalizedInfo());

        for (int y = 0; y < Math.min(maxLines, lines.size()); y++) {
            String line = lines.get(y);

            int textWidth = minecraft.fontRenderer.getStringWidth(line);
            minecraft.fontRenderer.drawString(line, (recipeWidth - textWidth) / 2, (y % maxLines) * h + offset + 4, Color.GRAY.getRGB());
        }
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();
        traitList.forEach((index, trait) -> {
            if (mouseY >= 22 + index * 9 && mouseY < 31 + index * 9 && mouseX >= 0 && mouseX <= AbstractCategory.WIDTH) {
                String[] desc = trait.getLocalizedDesc().split("\\\\n");
                String title = TextFormatting.GOLD + desc[0] + TextFormatting.RESET;
                tooltip.add(title);
                tooltip.addAll(Arrays.asList(WordUtils.wrap(desc[1], 40).split("\\r\\n")));
            }
        });
        return tooltip;
    }

    public boolean hasFluid() {
        return material.hasFluid();
    }

    public FluidStack getFluid() {
        return new FluidStack(material.getFluid(), 1000);
    }
}
