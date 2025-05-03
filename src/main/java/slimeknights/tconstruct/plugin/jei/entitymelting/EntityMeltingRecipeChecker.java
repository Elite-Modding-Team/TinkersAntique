package slimeknights.tconstruct.plugin.jei.entitymelting;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.TinkerRegistry;

public class EntityMeltingRecipeChecker {
	
    public static Set<EntityMeltingRecipe> getEntityMeltingRecipes() {
        Set<EntityMeltingRecipe> recipes = new HashSet<>();

        for(Entry<ResourceLocation, FluidStack> entry : TinkerRegistry.getAllEntityMeltingRecipes().entrySet()) {
        	
            ItemStack spawnEgg = new ItemStack(Items.SPAWN_EGG);
            ItemMonsterPlacer.applyEntityIdToItemStack(spawnEgg, entry.getKey());
        	
        	recipes.add(new EntityMeltingRecipe(entry.getKey(), entry.getValue(), spawnEgg));
        }

        return recipes;
    }
}
