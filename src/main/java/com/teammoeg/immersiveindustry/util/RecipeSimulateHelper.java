package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class RecipeSimulateHelper extends ItemStackHandler{
	int slotOffset=0;
	public RecipeSimulateHelper(ItemStack...stacks) {
		super(stacks.length);
		for(int i=0;i<stacks.length;i++) {
			super.setStackInSlot(i, stacks[i].copy());
		}
	}
	public RecipeSimulateHelper(IItemHandler inv,int minSlot,int maxSlot) {
		super(maxSlot-minSlot);
		slotOffset=minSlot;
		for(int i=minSlot;i<maxSlot;i++) {
			super.setStackInSlot(i, inv.getStackInSlot(i));
		}
	}
	private static final BiFunction<Integer,Integer,Integer> sum=(a,b)->a+b;
	public ItemRecipeProcessResult simulateExtract(List<IngredientWithSize> ling) {
		
		Map<Integer,Integer> slotOps=new HashMap<>();
		for(IngredientWithSize ing:ling) {
			if(!simulateExtract(ing,slotOps))
				return null;
		}
		return new ItemRecipeProcessResult(slotOps);
	}
	public ItemRecipeProcessResult simulateExtract(IngredientWithSize... ling) {
		
		Map<Integer,Integer> slotOps=new HashMap<>();
		for(IngredientWithSize ing:ling) {
			if(!simulateExtract(ing,slotOps))
				return null;
		}
		return new ItemRecipeProcessResult(slotOps);
	}
	private static ListTag catalyst=new ListTag();
	static {
		catalyst.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("gui.jei.tooltip.immersiveindustry.catalyst"))));
	}
	public static List<List<ItemStack>> expand(IngredientWithSize...ft){
		List<List<ItemStack>> li=new ArrayList<>(ft.length);
		
		for(IngredientWithSize t:ft) {
			if(t.getCount()==0) {
				ItemStack[] iss=t.getBaseIngredient().getItems();
				List<ItemStack> sli=new ArrayList<>(iss.length);
				for(ItemStack is:iss) {
					ItemStack cis=is.copy();
					
					cis.getOrCreateTagElement(ItemStack.TAG_DISPLAY).put(ItemStack.TAG_LORE, catalyst);
					sli.add(cis);
				}
				li.add(sli);
			}else
				li.add(t.getMatchingStackList());
		}
		return li;
	}
	private boolean simulateExtract(IngredientWithSize ing,Map<Integer,Integer> slotOps) {
		boolean has=false;
		int sizeRemain=ing.getCount();
		for(int i=0;i<super.getSlots();i++) {
			ItemStack inslot=super.getStackInSlot(i);
			if(ing.testIgnoringSize(inslot)) {
				has=true;
				if(sizeRemain>0) {
					int extracted=super.extractItem(i, sizeRemain, false).getCount();
					sizeRemain-=extracted;
					slotOps.merge(i+slotOffset, -extracted, sum);
				}
			}
			if(has&&sizeRemain<=0)return true;
		}
		return false;
	}
}
