package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.traits.ITrait;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialWrapper implements IRecipeWrapper {
    final Material material;

    protected GuideButton guideButton;

    public MaterialWrapper(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, getInputs());
        ingredients.setOutputs(VanillaTypes.ITEM, getOutputs());
        if (material.hasFluid()) {
            ingredients.setInputs(VanillaTypes.FLUID, getFluid());
        }
    }

    private List<ItemStack> getInputs() {
        List<ItemStack> inputs = new ArrayList<>();
        if (material.hasFluid()) {
            TinkerRegistry.getAllMeltingRecipies().stream()
                    .filter(meltingRecipe -> material.getFluid().equals(meltingRecipe.output.getFluid()))
                    .map(meltingRecipe -> meltingRecipe.input.getInputs())
                    .forEach(inputs::addAll);
        }
        if (material.getRepresentativeItem() != null && !material.getRepresentativeItem().isEmpty()) {
            inputs.add(material.getRepresentativeItem());
        }
        return inputs;
    }

    private List<ItemStack> getOutputs() {
        return TinkerRegistry.getToolParts().stream()
                .filter(iToolPart -> iToolPart.canUseMaterial(material))
                .map(iToolPart -> iToolPart.getItemstackWithMaterial(material))
                .collect(Collectors.toList());
    }

    private List<FluidStack> getFluid() {
        return Collections.singletonList(new FluidStack(material.getFluid(), 1));
    }

    public LinkedList<ItemStack> getParts(List<String> parts) {
        LinkedList<ItemStack> partList = new LinkedList<>();
        for (IToolPart part : TinkerRegistry.getToolParts()) {
            if (parts.stream().anyMatch(part::hasUseForStat) && part.canUseMaterial(material)) {
                ItemStack stack = part.getItemstackWithMaterial(material);
                if (!stack.equals(material.getShard())) {
                    partList.add(part.getItemstackWithMaterial(material));
                }
            }
        }
        return partList;
    }

    public LinkedList<ITrait> getTraits(List<String> parts) {
        LinkedList<ITrait> traitList = new LinkedList<>();
        for (String part : parts) {
            for (ITrait trait : material.getAllTraitsForStats(part)) {
                if (!traitList.contains(trait)) {
                    traitList.add(trait);
                }
            }
        }
        return traitList;
    }

    public LinkedList<String> getStatInfos(List<String> parts) {
        LinkedList<String> statList = new LinkedList<>();
        getStats(parts).forEach(s -> statList.addAll(s.getLocalizedInfo()));
        return statList;

    }

    public LinkedList<String> getStatDescriptions(List<String> parts) {
        LinkedList<String> descList = new LinkedList<>();
        getStats(parts).forEach(s -> descList.addAll(s.getLocalizedDesc()));
        return descList;
    }

    private List<? extends IMaterialStats> getStats(List<String> parts) {
        ArrayList<? extends IMaterialStats> stats = new ArrayList<>();
        // Projectiles use the same stats as the head part
        LinkedList<String> internal_parts = parts.stream().map(entry -> {
            if (entry.equals(MaterialTypes.PROJECTILE)) {
                return MaterialTypes.HEAD;
            }
            return entry;
        }).collect(Collectors.toCollection(LinkedList::new));
        internal_parts.forEach(part -> stats.add(material.getStats(part)));
        return stats.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        // I have no idea why JEI want's to do all the user interaction in the wrapper instead of the category, but here we are.
        if (guideButton != null && guideButton.mousePressed(minecraft, mouseX, mouseY)) {
            guideButton.openBook();
            return true;
        }
		return false;
	}
}
