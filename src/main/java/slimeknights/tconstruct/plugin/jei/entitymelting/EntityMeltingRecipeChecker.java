package slimeknights.tconstruct.plugin.jei.entitymelting;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.HashSet;
import java.util.Set;

public class EntityMeltingRecipeChecker {
    public static Set<MeltingRecipe> getEntityMeltingRecipes() {
        Set<MeltingRecipe> recipes = new HashSet<>();

        for(ResourceLocation rl : TinkerRegistry.getAllEntityMeltingRecipes().keySet()) {
            ItemStack spawnEgg = new ItemStack(Items.SPAWN_EGG);
            ItemMonsterPlacer.applyEntityIdToItemStack(spawnEgg, rl);
            if(!spawnEgg.isEmpty()) {
                String name = spawnEgg.getDisplayName().replace(I18n.format("item.monsterPlacer.name"), "").trim();
                spawnEgg.setStackDisplayName(I18n.format("gui.jei.living.info", name));
                FluidStack fluidOutput = TinkerRegistry.getAllEntityMeltingRecipes().get(rl);
                if(fluidOutput != null) {
                    RecipeMatch recipeMatch = new RecipeMatch.Item(spawnEgg, 1, fluidOutput.amount);
                    MeltingRecipe entityRecipe = new MeltingRecipe(recipeMatch, fluidOutput);
                    recipes.add(entityRecipe);
                }
            }
        }

        return recipes;
    }
}
