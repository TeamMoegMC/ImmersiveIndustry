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

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIContent;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class CrucibleRecipeSerializer extends IERecipeSerializer<CrucibleRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.crucible);
    }

    @Override
    public CrucibleRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize input = IngredientWithSize.deserialize(json.get("input"));
        IngredientWithSize input2 = IngredientWithSize.deserialize(json.get("input2"));
        int time = JSONUtils.getInt(json, "time");
        return new CrucibleRecipe(recipeId, output, input, input2, time);
    }

    @Nullable
    @Override
    public CrucibleRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize input = IngredientWithSize.read(buffer);
        IngredientWithSize input2 = IngredientWithSize.read(buffer);
        int time = buffer.readInt();
        return new CrucibleRecipe(recipeId, output, input, input2, time);
    }

    @Override
    public void write(PacketBuffer buffer, CrucibleRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        recipe.input2.write(buffer);
        buffer.writeInt(recipe.time);
    }
}
