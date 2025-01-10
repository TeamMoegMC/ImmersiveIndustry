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
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.Map;


public class CrucibleRecipe extends IESerializableRecipe {
    public static RegistryObject<RecipeType<CrucibleRecipe>> TYPE;
    public static RegistryObject<IERecipeSerializer<CrucibleRecipe>> SERIALIZER;
    // Initialized by reload listener
    public static Map<ResourceLocation, CrucibleRecipe> recipeList = Collections.emptyMap();
    public final IngredientWithSize inputs[];
    public final Lazy<ItemStack> output;
    public final FluidStack output_fluid;
    public final int time;
    public final int temperature ;
    public static Lazy<TypeWithClass<CrucibleRecipe>> IEType=Lazy.of(()->new TypeWithClass<>(TYPE, CrucibleRecipe.class));
    public CrucibleRecipe(ResourceLocation id, Lazy<ItemStack> output2, FluidStack output_fluid, IngredientWithSize[] input, int time, int temperature) {
        super(output2, IEType.get(), id);
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




    public static boolean isValidInput(ItemStack stack) {
        for (CrucibleRecipe recipe : recipeList.values())
            for (IngredientWithSize is : recipe.inputs) {
                if (is.testIgnoringSize(stack))
                    return true;
            }
        return false;
    }
    public static int getFuelTime(Level l,ItemStack stack) {
        return BlastFurnaceFuel.getBlastFuelTime(l, stack);//stack.getItem().getTags().contains("coal_coke");
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
