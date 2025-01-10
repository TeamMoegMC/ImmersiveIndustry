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

package com.teammoeg.immersiveindustry.content.electrolyzer;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.Map;

public class ElectrolyzerRecipe extends IESerializableRecipe {
    public static RegistryObject<RecipeType<ElectrolyzerRecipe>> TYPE;
    public static RegistryObject<IERecipeSerializer<ElectrolyzerRecipe>> SERIALIZER;

    public final IngredientWithSize[] inputs;
    public final FluidTagInput input_fluid;
    public final ItemStack output;
    public final FluidStack output_fluid;
    public final int time;
    public final int tickEnergy;
    public final boolean flag;

    public ElectrolyzerRecipe(ResourceLocation id, ItemStack output, IngredientWithSize[] input, FluidTagInput input_fluid, FluidStack output_fluid, int time, int tickEnergy, boolean flag) {
        super(output, TYPE, id);
        this.output = output;
        this.inputs = input;
        this.input_fluid = input_fluid;
        this.time = time;
        this.tickEnergy = tickEnergy;
        this.flag = flag;
        this.output_fluid = output_fluid;
    }

    @Override
    protected IERecipeSerializer<ElectrolyzerRecipe> getIESerializer() {
        return SERIALIZER.get();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    // Initialized by reload listener
    public static Map<ResourceLocation, ElectrolyzerRecipe> recipeList = Collections.emptyMap();

    public static boolean isValidRecipeInput(ItemStack input) {
        for (ElectrolyzerRecipe recipe : recipeList.values())
            for(IngredientWithSize is:recipe.inputs) {
            	if(is.testIgnoringSize(input))
            		return true;
            }
        return false;
    }

    public static ElectrolyzerRecipe findRecipe(ItemStack input, ItemStack input2, FluidStack input_fluid,boolean isLarge) {
    	int size=(input.isEmpty()?0:1)+(input2.isEmpty()?0:1);
    	outer:
    	for (ElectrolyzerRecipe recipe : recipeList.values())
        	if(isLarge||!recipe.flag) {
        		if(recipe.inputs.length>0) {
        			if(recipe.inputs.length<=size) {
	        			for(IngredientWithSize is:recipe.inputs) {
	        				if(!is.test(input)&&!is.test(input2))
	        					continue outer;
	        			}
        			}else continue outer;
        		}

        		if(recipe.input_fluid!=null&&!recipe.input_fluid.test(input_fluid))
        			continue;
        		return recipe;
        	}
        return null;
    }

    public static boolean isValidRecipeFluid(FluidStack input_fluid) {
        for (ElectrolyzerRecipe recipe : recipeList.values())
            if (recipe.input_fluid!=null && recipe.input_fluid.testIgnoringAmount(input_fluid))
                return true;
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for(IngredientWithSize is:this.inputs)
        	nonnulllist.add(is.getBaseIngredient());
        return nonnulllist;
    }
}
