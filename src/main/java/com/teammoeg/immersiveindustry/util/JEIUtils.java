package com.teammoeg.immersiveindustry.util;

import java.util.List;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;

public class JEIUtils {

	private JEIUtils() {
		
	}
	public static void makeItemLayouts(RecipeIngredientRole role,IRecipeLayoutBuilder layout,List<List<ItemStack>> ingredients,int... positions) {
		int msize=positions.length/2;
		for(int i=0;i<msize;i++) {
			IRecipeSlotBuilder slot=layout.addSlot(role, positions[i*2], positions[i*2+1]);
			if(ingredients.size()>i) {
				List<ItemStack> lstack=ingredients.get(i);
				if(lstack!=null&&!lstack.isEmpty()) {
					slot.addItemStacks(lstack);
				}
			}
		}
	}

}
