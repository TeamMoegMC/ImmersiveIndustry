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

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collections;
import java.util.List;

public class CarKilnRecipe extends IESerializableRecipe {
    public static IRecipeType<CarKilnRecipe> TYPE;
    public static RegistryObject<IERecipeSerializer<CarKilnRecipe>> SERIALIZER;

    public final IngredientWithSize[] inputs;
    public final ItemStack output;
    public final FluidStack input_fluid;
    public final int time;
    public final int tickEnergy;

    public CarKilnRecipe(ResourceLocation id, ItemStack output, IngredientWithSize[] inputs, FluidStack input_fluid, int time, int tickEnergy) {
        super(output, TYPE, id);
        this.output = output;
        this.inputs = inputs;
        this.input_fluid = input_fluid;
        this.time = time;
        this.tickEnergy = tickEnergy;
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return SERIALIZER.get();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    // Initialized by reload listener
    public static List<CarKilnRecipe> recipeList = Collections.emptyList();

    public static boolean isValidInput(ItemStack stack) {
        for (CarKilnRecipe recipe : recipeList)
            for (IngredientWithSize is : recipe.inputs) {
                if (is.testIgnoringSize(stack))
                    return true;
            }
        return false;
    }
    //returns recipe
    public static CarKilnRecipe findRecipe(List<ItemStack> input,FluidStack f,int startIndex,int endIndex) {
    	int size=0;
    	for(int i=startIndex;i<endIndex;i++) {
    		if(!input.get(i).isEmpty())
    			size++;
    	}
    	if(size<=0)return null;
    	exter:
    	for (CarKilnRecipe recipe : recipeList)
        	if(recipe.inputs.length<=size) {
        		if(!f.containsFluid(recipe.input_fluid))continue;
        		outer:
        		for(IngredientWithSize iws:recipe.inputs) {
        			for(int i=startIndex;i<endIndex;i++)
        				if(iws.test(input.get(i)))
        					continue outer;
        			continue exter;
        		}
        		return recipe;
        	}
        return null;
    }


    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for (IngredientWithSize is : this.inputs)
            nonnulllist.add(is.getBaseIngredient());
        return nonnulllist;
    }
}
