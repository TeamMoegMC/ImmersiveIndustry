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

import java.util.Collections;
import java.util.Map;

import blusunrize.immersiveengineering.api.crafting.BlastFurnaceFuel;
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


public class CrucibleRecipe extends IESerializableRecipe {
    public static TypeWithClass<CrucibleRecipe> TYPE;
    public static RegistryObject<IERecipeSerializer<CrucibleRecipe>> SERIALIZER;
    // Initialized by reload listener
    public static CachedRecipeList<CrucibleRecipe> recipeList = new CachedRecipeList<>(TYPE);
    public final IngredientWithSize inputs[];
    public final Lazy<ItemStack> output;
    public final FluidStack output_fluid;
    public final int time;
    public final int temperature ;
    public CrucibleRecipe(ResourceLocation id, Lazy<ItemStack> output2, FluidStack output_fluid, IngredientWithSize[] input, int time, int temperature) {
        super(output2, TYPE, id);
        this.output = output2;
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
    public ItemStack getResultItem(RegistryAccess ra) {
        return this.output.get();
    }




    public static boolean isValidInput(Level l,ItemStack stack) {
        for (CrucibleRecipe recipe : recipeList.getRecipes(l))
            for (IngredientWithSize is : recipe.inputs) {
                if (is.testIgnoringSize(stack))
                    return true;
            }
        return false;
    }
    public static int getFuelTime(Level l,ItemStack stack) {
        return BlastFurnaceFuel.getBlastFuelTime(l, stack);//stack.getItem().getTags().contains("coal_coke");
    }

    public static CrucibleRecipe findRecipe(Level l,ItemStack input, ItemStack input2, ItemStack input3, ItemStack input4) {
        int size = (input.isEmpty() ? 0 : 1) + (input2.isEmpty() ? 0 : 1) + (input3.isEmpty() ? 0 : 1) + (input4.isEmpty() ? 0 : 1);
        outer:
        for (CrucibleRecipe recipe : recipeList.getRecipes(l)) {
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
