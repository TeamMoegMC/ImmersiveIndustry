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

package com.teammoeg.immersiveindustry.content.crucible;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
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
import java.util.Map;

public class CrucibleRecipe extends IESerializableRecipe {
    public static IRecipeType<CrucibleRecipe> TYPE;
    public static RegistryObject<IERecipeSerializer<CrucibleRecipe>> SERIALIZER;

    public final IngredientWithSize inputs[];
    public final ItemStack output;
    public final FluidStack output_fluid;
    public final int time;
    public final int temperature ;

    public CrucibleRecipe(ResourceLocation id, ItemStack output, FluidStack output_fluid, IngredientWithSize[] input, int time, int temperature) {
        super(output, TYPE, id);
        this.output = output;
        this.output_fluid = output_fluid;
        this.inputs = input;
        this.time = time;
        this.temperature = temperature;
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
    public static Map<ResourceLocation, CrucibleRecipe> recipeList = Collections.emptyMap();


    public static boolean isValidInput(ItemStack stack) {
        for (CrucibleRecipe recipe : recipeList.values())
            for (IngredientWithSize is : recipe.inputs) {
                if (is.testIgnoringSize(stack))
                    return true;
            }
        return false;
    }
    public static int getFuelTime(ItemStack stack) {
        return BlastFurnaceFuel.getBlastFuelTime(stack);//stack.getItem().getTags().contains("coal_coke");
    }

    public static CrucibleRecipe findRecipe(ItemStack input, ItemStack input2, ItemStack input3, ItemStack input4) {
        int size = (input.isEmpty() ? 0 : 1) + (input2.isEmpty() ? 0 : 1) + (input3.isEmpty() ? 0 : 1) + (input4.isEmpty() ? 0 : 1);
        outer:
        for (CrucibleRecipe recipe : recipeList.values()) {
            if (recipe.inputs.length > 0) {
                if (recipe.inputs.length <= size) {
                    for (IngredientWithSize is : recipe.inputs) {
                        if (!is.test(input) && !is.test(input2) && !is.test(input3) && !is.test(input4))
                            continue outer;
                    }
                } else continue outer;
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
