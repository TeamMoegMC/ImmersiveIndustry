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
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collections;
import java.util.Map;

public class ElectrolyzerRecipe extends IESerializableRecipe {
    public static IRecipeType<ElectrolyzerRecipe> TYPE;
    public static RegistryObject<IERecipeSerializer<ElectrolyzerRecipe>> SERIALIZER;

    public final IngredientWithSize input;
    public final IngredientWithSize input2;
    public final FluidTagInput input_fluid;
    public final ItemStack output;
    public final int time;
    public final boolean flag;

    public ElectrolyzerRecipe(ResourceLocation id, ItemStack output, IngredientWithSize input, IngredientWithSize input2, FluidTagInput input_fluid, int time, boolean flag) {
        super(output, TYPE, id);
        this.output = output;
        this.input = input;
        this.input2 = input2;
        this.input_fluid = input_fluid;
        this.time = time;
        this.flag = flag;
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
    public static Map<ResourceLocation, ElectrolyzerRecipe> recipeList = Collections.emptyMap();

    public static boolean isValidRecipeInput(ItemStack input, boolean b) {
        for (ElectrolyzerRecipe recipe : recipeList.values())
            if (recipe != null) {
                if (recipe.input.test(input) && b)
                    return true;
                else if (recipe.input2.test(input) && !b) ;
                return true;
            }
        return false;
    }

    public static ElectrolyzerRecipe findRecipe(ItemStack input, ItemStack input2, FluidStack input_fluid) {
        for (ElectrolyzerRecipe recipe : recipeList.values())
            if (recipe != null && recipe.input.test(input))
                if (recipe.input2.test(input2) || recipe.input2 == null)
                    if (recipe.input_fluid.test(input_fluid))
                        return recipe;
        return null;
    }

    public static boolean isValidRecipeFluid(FluidStack input_fluid) {
        for (ElectrolyzerRecipe recipe : recipeList.values())
            if (recipe != null && recipe.input_fluid.testIgnoringAmount(input_fluid))
                return true;
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.input.getBaseIngredient());
        nonnulllist.add(this.input2.getBaseIngredient());
        return nonnulllist;
    }
}
