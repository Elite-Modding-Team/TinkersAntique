package slimeknights.tconstruct.plugin.jei.entitymelting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class EntityMeltingRecipe {

	final ResourceLocation entity;
	final FluidStack stack;
	final ItemStack spawnEgg;
	
	public EntityMeltingRecipe(ResourceLocation entity, FluidStack stack, ItemStack spawnEgg) {
		this.entity = entity;
		this.stack = stack;
		this.spawnEgg = spawnEgg;
	}

	public ResourceLocation getEntity() {
		return entity;
	}

	public FluidStack getResultStack() {
		return stack.copy();
	}
}
