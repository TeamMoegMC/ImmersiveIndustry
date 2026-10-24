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

import net.minecraft.world.item.crafting.RecipeSerializer;
import com.teammoeg.immersiveindustry.IIContent.IIRecipes;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
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
import net.neoforged.neoforge.registries.DeferredHolder;

public class RotaryKilnRecipe extends IESerializableRecipe {
    public static DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<RotaryKilnRecipe>> SERIALIZER;

    public final IngredientWithSize input;
    public final ItemStack output;
    public final StackWithChance secoutput;
    public final FluidStack output_fluid;
    public final int time;
    public final int tickEnergy;


    public RotaryKilnRecipe(ItemStack output, IngredientWithSize input,
			FluidStack output_fluid, int time,
			int tickEnergy, StackWithChance secoutput) {
		super(new TagOutput(output), IIRecipes.ROTARY_KILN);
		this.input = input;
		this.output = output;
		this.secoutput = secoutput;
		this.output_fluid = output_fluid;
		this.time = time;
		this.tickEnergy = tickEnergy;
	}

	@Override
    protected IERecipeSerializer<?> getIESerializer() {
        return SERIALIZER.get();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider ra) {
        return this.output;
    }

    // Initialized by reload listener
    public static CachedRecipeList<RotaryKilnRecipe> recipeList = new CachedRecipeList<>(IIRecipes.ROTARY_KILN);

    public boolean matches(ItemStack input) {
        if (this.input != null && this.input.test(input))
            return true;
        return false;
    }
    public static RotaryKilnRecipe findRecipe(IMultiblockContext<RotaryKilnState> context) {
        return findRecipe(context.getLevel().getRawLevel(),context.getState().inventory.getStackInSlot(0));
    }
    public static RotaryKilnRecipe findRecipe(Level l,ItemStack input) {
        for (RecipeHolder<RotaryKilnRecipe> holder : recipeList.getRecipes(l)) {
            RotaryKilnRecipe recipe = holder.value();
            if (recipe.matches(input))
                return recipe;
        }
        return null;
    }
    
    public static boolean isValidRecipeInput(Level l,ItemStack input) {
        for (RecipeHolder<RotaryKilnRecipe> holder : recipeList.getRecipes(l)) {
            RotaryKilnRecipe recipe = holder.value();
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
