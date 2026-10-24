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

import net.minecraft.world.item.crafting.RecipeSerializer;
import com.teammoeg.immersiveindustry.IIContent.IIRecipes;
import com.teammoeg.immersiveindustry.util.FluidRecipeProcessResult;
import com.teammoeg.immersiveindustry.util.FluidRecipeSimulator;
import com.teammoeg.immersiveindustry.util.ItemRecipeProcessResult;
import com.teammoeg.immersiveindustry.util.RecipeProcessResult;
import com.teammoeg.immersiveindustry.util.RecipeSimulateHelper;

import blusunrize.immersiveengineering.api.crafting.TagOutput;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ElectrolyzerRecipe extends IESerializableRecipe {
    public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<ElectrolyzerRecipe>> SERIALIZER;

    public final IngredientWithSize[] inputs;
    public final SizedFluidIngredient input_fluid;
    public final ItemStack output;
    public final FluidStack output_fluid;
    public final int time;
    public final int tickEnergy;
    public final boolean flag;

    public ElectrolyzerRecipe(ItemStack output, IngredientWithSize[] input, SizedFluidIngredient input_fluid, FluidStack output_fluid, int time, int tickEnergy, boolean flag) {
        super(new TagOutput(output), IIRecipes.ELECTROLYZER);
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
    public ItemStack getResultItem(HolderLookup.Provider ra) {
        return this.output;
    }

    public static CachedRecipeList<ElectrolyzerRecipe> recipeList = new CachedRecipeList<>(IIRecipes.ELECTROLYZER);
    public static boolean isValidRecipeInput(Level l,ItemStack input) {
        for (RecipeHolder<ElectrolyzerRecipe> holder : recipeList.getRecipes(l))
            for(IngredientWithSize is:holder.value().inputs) {
            	if(is.testIgnoringSize(input))
            		return true;
            }
        return false;
    }
    public static RecipeProcessResult<ElectrolyzerRecipe> findRecipe(ElectrolyzerBlockEntity te) {
    	return findRecipe(te.getLevel(),te.inventory.get(ElectrolyzerBlockEntity.SLOT_IN),ItemStack.EMPTY,te.tank,false);
    }
    public static RecipeProcessResult<ElectrolyzerRecipe> findRecipe(IMultiblockContext<IndustrialElectrolyzerState> context) {
    	return findRecipe(context.getLevel().getRawLevel(), context.getState().inventory.getStackInSlot(0), context.getState().inventory.getStackInSlot(1), context.getState().tank[0],true);
    }
    public static RecipeProcessResult<ElectrolyzerRecipe> findRecipe(Level l,ItemStack input, ItemStack input2, IFluidHandler input_fluid,boolean isLarge) {
    	for (RecipeHolder<ElectrolyzerRecipe> holder : recipeList.getRecipes(l)) {
            ElectrolyzerRecipe recipe = holder.value();
    		RecipeProcessResult<ElectrolyzerRecipe> data=test(recipe,input,input2,input_fluid,isLarge);
    		if(data!=null)
    			return data;
    	}
    	
        return null;
    }
    public static RecipeProcessResult<ElectrolyzerRecipe> executeRecipe(Level l,ResourceLocation rl,ItemStack input, ItemStack input2, IFluidHandler input_fluid,boolean isLarge) {
    	return test(recipeList.getById(l, rl),input,input2,input_fluid,isLarge);
    }
    public static RecipeProcessResult<ElectrolyzerRecipe> test(ElectrolyzerRecipe recipe,ItemStack input, ItemStack input2, IFluidHandler tanks,boolean isLarge) {
    	int size=(input.isEmpty()?0:1)+(input2.isEmpty()?0:1);
    	if(isLarge||!recipe.flag) {
    		ItemRecipeProcessResult slotOps=null;
    		if(recipe.inputs.length>0) {
    			if(recipe.inputs.length>size) 
    				return null;
				RecipeSimulateHelper helper=new RecipeSimulateHelper(input,input2);
				slotOps=helper.simulateExtract(recipe.inputs);
				if(slotOps==null)
					return null;
    		}
    		FluidRecipeProcessResult fluid=null;
    		if(recipe.input_fluid!=null) {
    			fluid=FluidRecipeSimulator.test(tanks,recipe.input_fluid);
    			if(fluid==null)return null;
    		}
    		return new RecipeProcessResult<>(recipe,slotOps,fluid);
    	}
        return null;
    }
    public static boolean isValidRecipeFluid(Level l,FluidStack input_fluid) {
        for (RecipeHolder<ElectrolyzerRecipe> holder : recipeList.getRecipes(l))
            if (holder.value().input_fluid!=null && holder.value().input_fluid.ingredient().test(input_fluid))
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
