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

package com.teammoeg.immersiveindustry.content.chemical_reactor;

import com.teammoeg.immersiveindustry.IIContent.IIRecipes;
import com.teammoeg.immersiveindustry.util.FluidRecipeProcessResult;
import com.teammoeg.immersiveindustry.util.FluidRecipeSimulator;
import com.teammoeg.immersiveindustry.util.ItemRecipeProcessResult;
import com.teammoeg.immersiveindustry.util.RecipeProcessResult;
import com.teammoeg.immersiveindustry.util.RecipeSimulateHelper;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;


public class ChemicalRecipe extends IESerializableRecipe {
    public static RegistryObject<IERecipeSerializer<ChemicalRecipe>> SERIALIZER;
    // Initialized by reload listener
    public static CachedRecipeList<ChemicalRecipe> recipeList = new CachedRecipeList<>(IIRecipes.CHEMICAL);
    public final IngredientWithSize inputs[];
    public final FluidTagInput input_fluids[];
    public final ItemStack outputs[];
    public final FluidStack output_fluids[];
    public final int time;
    public final int tickEnergy;
    public ChemicalRecipe(ResourceLocation id, ItemStack[] output2, FluidStack[] output_fluid, IngredientWithSize[] input, FluidTagInput[] input_fluids, int time,int tickEnergy) {
        super(Lazy.of(()->ItemStack.EMPTY), IIRecipes.CHEMICAL, id);
        this.outputs = output2;
        this.output_fluids = output_fluid;
        this.inputs = input;
        this.time = time;
        this.input_fluids = input_fluids;
        this.tickEnergy=tickEnergy;
    }



    @Override
    protected IERecipeSerializer getIESerializer() {
        return SERIALIZER.get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess ra) {
        return ItemStack.EMPTY;
    }




    public static boolean isValidInput(Level l,ItemStack stack) {
        for (ChemicalRecipe recipe : recipeList.getRecipes(l))
            for (IngredientWithSize is : recipe.inputs) {
                if (is.testIgnoringSize(stack))
                    return true;
            }
        return false;
    }
    public static int getFuelTime(Level l,ItemStack stack) {
        return BlastFurnaceFuel.getBlastFuelTime(l, stack);//stack.getItem().getTags().contains("coal_coke");
    }
    public static RecipeProcessResult<ChemicalRecipe> findRecipe(IMultiblockContext<ChemicalState> context) {
    	return findRecipe(context.getLevel().getRawLevel(), context.getState().inventory, context.getState().recipeInputFluidHandler);
    }

    public static RecipeProcessResult<ChemicalRecipe> findRecipe(Level l,IItemHandler handler,IFluidHandler tanks) {
    	for (ChemicalRecipe recipe : recipeList.getRecipes(l)) {
    		RecipeProcessResult<ChemicalRecipe> data=test(recipe,handler,tanks);
    		if(data!=null)
    			return data;
    	}
    	
        return null;
    }
    public static RecipeProcessResult<ChemicalRecipe> executeRecipe(Level l,ResourceLocation rl,IItemHandler handler,IFluidHandler tanks) {
    	return test(recipeList.getById(l, rl),handler,tanks);
    }
    public static RecipeProcessResult<ChemicalRecipe> test(ChemicalRecipe recipe,IItemHandler handler,IFluidHandler tanks) {
    	int size=0;
    	for(int i=0;i<4;i++) {
    		if(!handler.getStackInSlot(i).isEmpty())
    			size++;
    	}
    	
    	ItemRecipeProcessResult slotOps=null;
		if(recipe.inputs.length>0) {
			if(recipe.inputs.length>size) 
				return null;
			RecipeSimulateHelper helper=new RecipeSimulateHelper(handler,0,4);
			slotOps=helper.simulateExtract(recipe.inputs);
			if(slotOps==null)
				return null;
		}
		FluidRecipeProcessResult fluid=null;
		if(recipe.input_fluids.length>0) {
			fluid=FluidRecipeSimulator.test(tanks, recipe.input_fluids);
			if(fluid==null)return null;
		}
		return new RecipeProcessResult<>(recipe,slotOps,fluid);
    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for (IngredientWithSize is : this.inputs)
            nonnulllist.add(is.getBaseIngredient());
        return nonnulllist;
    }
}
