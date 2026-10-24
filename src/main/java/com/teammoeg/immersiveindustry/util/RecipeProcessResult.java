package com.teammoeg.immersiveindustry.util;

import org.jetbrains.annotations.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public record RecipeProcessResult<T extends Recipe<?>>(T recipe,ItemRecipeProcessResult item,FluidRecipeProcessResult fluid) {
	public RecipeProcessResult(T recipe,ItemRecipeProcessResult item) {
		this(recipe,item,null);
	}
	public RecipeProcessResult(T recipe,FluidRecipeProcessResult fluid) {
		this(recipe,null,fluid);
	}
	/**
	 * Recipes no longer carry their registry id in 1.21 - it belongs to the enclosing
	 * {@code RecipeHolder} - so this returns null. Callers that persist a recipe should
	 * re-resolve it rather than store an id.
	 */
	public @Nullable ResourceLocation getId() {
		return null;
	}
	public void runOperations(IItemHandlerModifiable inventory,IFluidHandler hand) {
		if(item!=null)
			item.runOperations(inventory);
		if(fluid!=null)
			fluid.runOperations(hand);
	}
	public void runOperations(IItemHandlerModifiable inventory,IFluidHandler hand,int multiplier) {
		if(item!=null)
			item.runOperations(inventory,multiplier);
		if(fluid!=null)
			fluid.runOperations(hand,multiplier);
	}
	public boolean tryRunOperations(IItemHandlerModifiable inventory,IFluidHandler hand) {
		if((hand==null&&fluid!=null)||(inventory==null&&item!=null))return false;
		if(item!=null)
			if(!item.tryRunOperations(inventory))
				return false;
		if(fluid!=null)
			if(!fluid.tryRunOperations(hand))
				return false;
		return true;
	}
	public int getMaxRuns(IItemHandlerModifiable inventory,IFluidHandler hand) {
		int maxcount=64;
		if(item!=null)
			maxcount=Math.min(maxcount, item.getMaxRuns(inventory));
		if(fluid!=null)
			maxcount=Math.min(maxcount, fluid.getMaxRuns(hand));
		return maxcount;
	}
}
