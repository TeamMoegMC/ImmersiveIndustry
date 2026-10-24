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

import net.minecraft.world.item.crafting.RecipeSerializer;
import com.teammoeg.immersiveindustry.IIContent.IIRecipes;
import com.teammoeg.immersiveindustry.util.FluidRecipeProcessResult;
import com.teammoeg.immersiveindustry.util.FluidRecipeSimulator;
import com.teammoeg.immersiveindustry.util.ItemRecipeProcessResult;
import com.teammoeg.immersiveindustry.util.RecipeProcessResult;
import com.teammoeg.immersiveindustry.util.RecipeSimulateHelper;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Car kiln recipe.
 *
 * <p>1.21 changed two things about recipe identity: recipes no longer carry their own
 * {@code ResourceLocation} (the registry's {@code RecipeHolder} does), and
 * {@code IESerializableRecipe} now takes a {@link TagOutput} preview plus an
 * {@code IERecipeTypes.TypeWithClass} instead. IE's {@code FluidTagInput} is gone as well;
 * NeoForge's {@link SizedFluidIngredient} replaces it and, unlike the old class, is
 * codec-backed rather than hand-serialised.
 */
public class CarKilnRecipe extends IESerializableRecipe {
    public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<CarKilnRecipe>> SERIALIZER;

    public final IngredientWithSize[] inputs;
    public final ItemStack[] output;
    public final SizedFluidIngredient input_fluid;
    public final int time;
    public final int tickEnergy;
    public int maxProcess;

    public CarKilnRecipe(ItemStack[] output, IngredientWithSize[] inputs, SizedFluidIngredient input_fluid,
                         int time, int tickEnergy) {
        super(new TagOutput(output[0]), IIRecipes.CAR_KILN);
        this.output = output;
        this.inputs = inputs;
        this.input_fluid = input_fluid;
        this.time = time;
        this.tickEnergy = tickEnergy;
        maxProcess = 64;
        for (ItemStack out : output) {
            maxProcess = Math.min(maxProcess, out.getMaxStackSize() / out.getCount());
        }
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return SERIALIZER.get();
    }

    public int getInputAmount() {
        int total = 0;
        for (IngredientWithSize iws : inputs) {
            total += iws.getCount();
        }
        return total;
    }

    // Initialized by reload listener
    public static CachedRecipeList<CarKilnRecipe> recipeList = new CachedRecipeList<>(IIRecipes.CAR_KILN);

    public static boolean isValidInput(Level l, ItemStack stack) {
        for (RecipeHolder<CarKilnRecipe> holder : recipeList.getRecipes(l))
            for (IngredientWithSize is : holder.value().inputs) {
                if (is.testIgnoringSize(stack))
                    return true;
            }
        return false;
    }

    public static RecipeProcessResult<CarKilnRecipe> findRecipe(IMultiblockContext<CarKilnState> context) {
        return findRecipe(context.getLevel().getRawLevel(), context.getState().inventory, context.getState().tank);
    }

    public static RecipeProcessResult<CarKilnRecipe> findRecipe(Level l, IItemHandler input, IFluidHandler input_fluid) {
        for (RecipeHolder<CarKilnRecipe> holder : recipeList.getRecipes(l)) {
            RecipeProcessResult<CarKilnRecipe> data = test(holder.value(), input, input_fluid);
            if (data != null)
                return data;
        }

        return null;
    }

    public static RecipeProcessResult<CarKilnRecipe> executeRecipe(Level l, ResourceLocation rl, IItemHandler input, IFluidHandler input_fluid) {
        CarKilnRecipe recipe = recipeList.getById(l, rl);
        return recipe == null ? null : test(recipe, input, input_fluid);
    }

    public static RecipeProcessResult<CarKilnRecipe> test(CarKilnRecipe recipe, IItemHandler input, IFluidHandler tanks) {
        int size = 0;
        for (int i = 0; i < 4; i++) {
            if (!input.getStackInSlot(i).isEmpty())
                size++;
        }
        ItemRecipeProcessResult slotOps = null;
        if (recipe.inputs.length > 0) {
            if (recipe.inputs.length > size)
                return null;
            RecipeSimulateHelper helper = new RecipeSimulateHelper(input, 0, 4);
            slotOps = helper.simulateExtract(recipe.inputs);
            if (slotOps == null)
                return null;
        }
        FluidRecipeProcessResult fluid = null;
        if (recipe.input_fluid != null) {
            fluid = FluidRecipeSimulator.test(tanks, recipe.input_fluid);
            if (fluid == null)
                return null;
        }
        return new RecipeProcessResult<>(recipe, slotOps, fluid);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for (IngredientWithSize is : this.inputs)
            nonnulllist.add(is.getBaseIngredient());
        return nonnulllist;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return output[0];
    }
}
