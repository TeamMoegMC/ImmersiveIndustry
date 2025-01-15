package com.teammoeg.immersiveindustry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class RecipeSimulateHelper extends ItemStackHandler{
	
	public RecipeSimulateHelper(ItemStack...stacks) {
		super(stacks.length);
		for(int i=0;i<stacks.length;i++) {
			super.setStackInSlot(i, stacks[i].copy());
		}
	}
	private static final BiFunction<Integer,Integer,Integer> sum=(a,b)->a+b;
	public Map<Integer,Integer> simulateExtract(List<IngredientWithSize> ling) {
		
		Map<Integer,Integer> slotOps=new HashMap<>();
		for(IngredientWithSize ing:ling) {
			if(!simulateExtract(ing,slotOps))
				return null;
		}
		return slotOps;
	}
	public Map<Integer,Integer> simulateExtract(IngredientWithSize... ling) {
		
		Map<Integer,Integer> slotOps=new HashMap<>();
		for(IngredientWithSize ing:ling) {
			if(!simulateExtract(ing,slotOps))
				return null;
		}
		return slotOps;
	}
	
	private boolean simulateExtract(IngredientWithSize ing,Map<Integer,Integer> slotOps) {
		
		int sizeRemain=ing.getCount();
		for(int i=0;i<super.getSlots();i++) {
			ItemStack inslot=super.getStackInSlot(i);
			if(ing.testIgnoringSize(inslot)) {
				int extracted=super.extractItem(i, sizeRemain, false).getCount();
				sizeRemain-=extracted;
				slotOps.merge(i, -extracted, sum);
			}
			if(sizeRemain<=0)return true;
		}
		return false;
	}
}
