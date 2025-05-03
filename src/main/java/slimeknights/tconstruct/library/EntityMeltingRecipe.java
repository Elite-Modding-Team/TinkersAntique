package slimeknights.tconstruct.library;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class EntityMeltingRecipe {

	public final ResourceLocation entity;
	public final FluidStack stack;
	public final ItemStack spawnEgg;
	
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
