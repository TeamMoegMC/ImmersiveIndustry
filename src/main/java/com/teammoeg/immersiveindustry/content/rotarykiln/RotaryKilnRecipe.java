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

package com.teammoeg.immersiveindustry.content.rotarykiln;

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

public class RotaryKilnRecipe extends IESerializableRecipe {
    public static IRecipeType<RotaryKilnRecipe> TYPE;
    public static RegistryObject<IERecipeSerializer<RotaryKilnRecipe>> SERIALIZER;

    public final IngredientWithSize input;
    public final ItemStack output;
    public final FluidStack output_fluid;
    public final int time;
    public final int tickEnergy;

    public RotaryKilnRecipe(ResourceLocation id, ItemStack output, IngredientWithSize input, FluidStack output_fluid, int time, int tickEnergy) {
        super(output, TYPE, id);
        this.output = output;
        this.input = input;
        this.output_fluid = output_fluid;
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
    public static Map<ResourceLocation, RotaryKilnRecipe> recipeList = Collections.emptyMap();

    public boolean matches(ItemStack input) {
        if (this.input != null && this.input.test(input))
            return true;
        return false;
    }

    public static RotaryKilnRecipe findRecipe(ItemStack input) {
        for (RotaryKilnRecipe recipe : recipeList.values())
            if (recipe != null && recipe.matches(input))
                return recipe;
        return null;
    }

    public static boolean isValidRecipeInput(ItemStack input) {
        for (RotaryKilnRecipe recipe : recipeList.values()) {
            if (recipe.input.test(input))
                return true;
        }
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.input.getBaseIngredient());
        return nonnulllist;
    }
}
