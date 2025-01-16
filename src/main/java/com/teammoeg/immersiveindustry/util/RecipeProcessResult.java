package com.teammoeg.immersiveindustry.util;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.IItemHandlerModifiable;

public record RecipeProcessResult<T extends Recipe<?>>(T recipe,Map<Integer, Integer> operations) {
	public void runOperations(IItemHandlerModifiable inventory) {
		if (operations != null) {
			for (Entry<Integer, Integer> i : operations.entrySet()) {
				ItemStack stack = inventory.getStackInSlot(i.getKey());
				int ncount = stack.getCount() + i.getValue();
				if (ncount > 0)
					inventory.setStackInSlot(i.getKey(), stack.copyWithCount(ncount));
				else
					inventory.setStackInSlot(i.getKey(), ItemStack.EMPTY);
			}
		}
	}
}
