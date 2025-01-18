/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.content.carkiln;

import java.util.Map;

import com.teammoeg.immersiveindustry.IIContent.IIRecipes;
import com.teammoeg.immersiveindustry.util.RecipeProcessResult;
import com.teammoeg.immersiveindustry.util.RecipeSimulateHelper;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;

public class CarKilnRecipe extends IESerializableRecipe {
    public static RegistryObject<IERecipeSerializer<CarKilnRecipe>> SERIALIZER;

    public final IngredientWithSize[] inputs;
    public final ItemStack[] output;
    public final FluidTagInput input_fluid;
    public final int time;
    public final int tickEnergy;
    public int maxProcess;

    public CarKilnRecipe(ResourceLocation id, ItemStack[] output, IngredientWithSize[] inputs, FluidTagInput input_fluid, int time, int tickEnergy) {
        super(Lazy.of(()->output[0]), IIRecipes.CAR_KILN, id);
        this.output = output;
        this.inputs = inputs;
        this.input_fluid = input_fluid;
        this.time = time;
        this.tickEnergy = tickEnergy;
        maxProcess=64;
        for(ItemStack out:output) {
        	maxProcess=Math.min(maxProcess, out.getMaxStackSize()/out.getCount());
        }
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return SERIALIZER.get();
    }
    public int getInputAmount() {
    	int total=0;
    	for(IngredientWithSize iws:inputs) {
    		total+=iws.getCount();
    	}
    	return total;
    }
    // Initialized by reload listener
    public static CachedRecipeList<CarKilnRecipe> recipeList = new CachedRecipeList<>(IIRecipes.CAR_KILN);

    public static boolean isValidInput(Level l,ItemStack stack) {
        for (CarKilnRecipe recipe : recipeList.getRecipes(l))
            for (IngredientWithSize is : recipe.inputs) {
                if (is.testIgnoringSize(stack))
                    return true;
            }
        return false;
    }
    

    public static RecipeProcessResult<CarKilnRecipe> findRecipe(Level l,IItemHandler input, FluidStack input_fluid) {
    	for (CarKilnRecipe recipe : recipeList.getRecipes(l)) {
    		RecipeProcessResult<CarKilnRecipe> data=test(recipe,input,input_fluid);
    		if(data!=null)
    			return data;
    	}
    	
        return null;
    }
    public static RecipeProcessResult<CarKilnRecipe> executeRecipe(Level l,ResourceLocation rl,IItemHandler input, FluidStack input_fluid) {
    	return test(recipeList.getById(l, rl),input,input_fluid);
    }
    public static RecipeProcessResult<CarKilnRecipe> test(CarKilnRecipe recipe,IItemHandler input, FluidStack input_fluid) {
    	int size=0;
    	for(int i=0;i<4;i++) {
    		if(!input.getStackInSlot(i).isEmpty())
    			size++;
    	}
		Map<Integer,Integer> slotOps=null;
		if(recipe.inputs.length>0) {
			if(recipe.inputs.length>size) 
				return null;
			RecipeSimulateHelper helper=new RecipeSimulateHelper(input,0,4);
			slotOps=helper.simulateExtract(recipe.inputs);
			if(slotOps==null)
				return null;
		}
		if(recipe.input_fluid!=null&&!recipe.input_fluid.test(input_fluid))
			return null;
		return new RecipeProcessResult<>(recipe, slotOps);
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for (IngredientWithSize is : this.inputs)
            nonnulllist.add(is.getBaseIngredient());
        return nonnulllist;
    }

	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return output[0];
	}
}
