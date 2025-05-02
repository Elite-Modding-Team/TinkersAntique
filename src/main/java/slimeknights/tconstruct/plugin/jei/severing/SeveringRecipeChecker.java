package slimeknights.tconstruct.plugin.jei.severing;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.SeveringRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;

import java.util.HashSet;
import java.util.Set;

public class SeveringRecipeChecker {
    public static Set<SeveringRecipe> getSeveringRecipes() {
        Set<SeveringRecipe> recipes = new HashSet<>();
        for(Class<? extends EntityLivingBase> entityClass : TinkerRegistry.getAllSeveringRecipes().keySet()) {
            ResourceLocation rl = EntityList.getKey(entityClass);
            if(rl == null) {
                continue;
            }
            ItemStack spawnEgg = new ItemStack(Items.SPAWN_EGG);
            ItemMonsterPlacer.applyEntityIdToItemStack(spawnEgg, rl);
            if(!spawnEgg.isEmpty()) {
                String name = spawnEgg.getDisplayName().replace(I18n.format("item.monsterPlacer.name"), "").trim();
                spawnEgg.setStackDisplayName(I18n.format("gui.jei.living.info", name));
                ItemStack headStack = TinkerRegistry.getAllSeveringRecipes().get(entityClass);
                if(headStack != null && !headStack.isEmpty()) {
                    RecipeMatch recipeMatch = new RecipeMatch.Item(spawnEgg, 1, 1);
                    SeveringRecipe severingRecipe = new SeveringRecipe(recipeMatch, headStack);
                    recipes.add(severingRecipe);
                }
            }
        }
        return recipes;
    }
}
