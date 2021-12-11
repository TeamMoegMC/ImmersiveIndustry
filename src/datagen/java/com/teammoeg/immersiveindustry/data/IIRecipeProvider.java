/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.data;

import java.util.HashMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.teammoeg.immersiveindustry.IIMain;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.util.ResourceLocation;

public class IIRecipeProvider extends RecipeProvider {
    private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();

    public IIRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> out) {
       // recipesGenerator(out);
    }

    private ResourceLocation toRL(String s) {
        if (!s.contains("/"))
            s = "crafting/" + s;
        if (PATH_COUNT.containsKey(s)) {
            int count = PATH_COUNT.get(s) + 1;
            PATH_COUNT.put(s, count);
            return new ResourceLocation(IIMain.MODID, s + count);
        }
        PATH_COUNT.put(s, 1);
        return new ResourceLocation(IIMain.MODID, s);
    }
}
