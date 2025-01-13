package com.teammoeg.immersiveindustry.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class JEISlotBuilder<T> {
	public static record JEISlotBuilderBuilder<T>(IRecipeLayoutBuilder layout,IIngredientType<T> type,List<List<T>> ingredients){
		public JEISlotBuilder<T> asInput(){
			return new JEISlotBuilder<T>(RecipeIngredientRole.INPUT,layout,type,ingredients);
		}
		public JEISlotBuilder<T> asOutput(){
			return new JEISlotBuilder<T>(RecipeIngredientRole.INPUT,layout,type,ingredients);
		}
	} 
	RecipeIngredientRole role;
	IRecipeLayoutBuilder layout;
	IIngredientType<T> type;
	List<List<T>> ingredients;
	int num=0;
	public JEISlotBuilder(RecipeIngredientRole role, IRecipeLayoutBuilder layout, IIngredientType<T> type, List<List<T>> ingredients) {
		super();
		this.role = role;
		this.layout = layout;
		this.type = type;
		this.ingredients = ingredients;
	}
	public static JEISlotBuilderBuilder<ItemStack> itemStack(IRecipeLayoutBuilder layout,ItemStack[] items) {
		return new JEISlotBuilderBuilder<>(layout,VanillaTypes.ITEM_STACK,Arrays.stream(items).map(Arrays::asList).collect(Collectors.toList()));
	}
	public static JEISlotBuilderBuilder<ItemStack> itemStack(IRecipeLayoutBuilder layout,List<List<ItemStack>> items) {
		return new JEISlotBuilderBuilder<>(layout,VanillaTypes.ITEM_STACK,items);
	}
	public static JEISlotBuilderBuilder<ItemStack> ingredientStack(IRecipeLayoutBuilder layout,List<Ingredient> items) {
		return new JEISlotBuilderBuilder<>(layout,VanillaTypes.ITEM_STACK,items.stream().map(t->Arrays.asList(t.getItems())).collect(Collectors.toList()));
	}
	public static JEISlotBuilderBuilder<FluidStack> fluidStack(IRecipeLayoutBuilder layout,List<List<FluidStack>> items) {
		return new JEISlotBuilderBuilder<>(layout,ForgeTypes.FLUID_STACK,items);
	}
	public static <I> IRecipeSlotBuilder addIngredientSlot(RecipeIngredientRole role,IRecipeLayoutBuilder layout,IIngredientType<I> type,List<List<I>> ingredients,int x,int y,int slotNum) {
		IRecipeSlotBuilder slot=layout.addSlot(role, x, y);
		if(ingredients.size()>slotNum) {
			List<I> lstack=ingredients.get(slotNum);
			if(lstack!=null&&!lstack.isEmpty()) {
				slot.addIngredients(type, lstack);
			}
		}
		return slot;
	}
	public IRecipeSlotBuilder addSlot(int x,int y) {
		return addIngredientSlot(role,layout,type,ingredients,x,y,num++);
	}
}
