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
import net.minecraftforge.registries.RegistryObject;

public class ElectrolyzerRecipe extends IESerializableRecipe {
    public static RegistryObject<IERecipeSerializer<ElectrolyzerRecipe>> SERIALIZER;

    public final IngredientWithSize[] inputs;
    public final FluidTagInput input_fluid;
    public final ItemStack output;
    public final FluidStack output_fluid;
    public final int time;
    public final int tickEnergy;
    public final boolean flag;

    public ElectrolyzerRecipe(ResourceLocation id, ItemStack output, IngredientWithSize[] input, FluidTagInput input_fluid, FluidStack output_fluid, int time, int tickEnergy, boolean flag) {
        super(Lazy.of(()->output),IIRecipes.ELECTROLYZER, id);
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
    public ItemStack getResultItem(RegistryAccess ra) {
        return this.output;
    }

    public static CachedRecipeList<ElectrolyzerRecipe> recipeList = new CachedRecipeList<>(IIRecipes.ELECTROLYZER);
    public static boolean isValidRecipeInput(Level l,ItemStack input) {
        for (ElectrolyzerRecipe recipe : recipeList.getRecipes(l))
            for(IngredientWithSize is:recipe.inputs) {
            	if(is.testIgnoringSize(input))
            		return true;
            }
        return false;
    }

    public static RecipeProcessResult<ElectrolyzerRecipe> findRecipe(Level l,ItemStack input, ItemStack input2, FluidStack input_fluid,boolean isLarge) {
    	for (ElectrolyzerRecipe recipe : recipeList.getRecipes(l)) {
    		RecipeProcessResult<ElectrolyzerRecipe> data=test(recipe,input,input2,input_fluid,isLarge);
    		if(data!=null)
    			return data;
    	}
    	
        return null;
    }
    public static RecipeProcessResult<ElectrolyzerRecipe> executeRecipe(Level l,ResourceLocation rl,ItemStack input, ItemStack input2, FluidStack input_fluid,boolean isLarge) {
    	return test(recipeList.getById(l, rl),input,input2,input_fluid,isLarge);
    }
    public static RecipeProcessResult<ElectrolyzerRecipe> test(ElectrolyzerRecipe recipe,ItemStack input, ItemStack input2, FluidStack input_fluid,boolean isLarge) {
    	int size=(input.isEmpty()?0:1)+(input2.isEmpty()?0:1);
    	if(isLarge||!recipe.flag) {
    		Map<Integer,Integer> slotOps=null;
    		if(recipe.inputs.length>0) {
    			if(recipe.inputs.length>size) 
    				return null;
				RecipeSimulateHelper helper=new RecipeSimulateHelper(input,input2);
				slotOps=helper.simulateExtract(recipe.inputs);
				if(slotOps==null)
					return null;
    		}
    		if(recipe.input_fluid!=null&&!recipe.input_fluid.test(input_fluid))
    			return null;
    		return new RecipeProcessResult<>(recipe, slotOps);
    	}
        return null;
    }
    public static boolean isValidRecipeFluid(Level l,FluidStack input_fluid) {
        for (ElectrolyzerRecipe recipe : recipeList.getRecipes(l))
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
