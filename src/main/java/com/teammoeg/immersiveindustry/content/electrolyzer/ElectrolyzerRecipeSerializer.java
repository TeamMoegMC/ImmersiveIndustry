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

package com.teammoeg.immersiveindustry.content.electrolyzer;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ElectrolyzerRecipeSerializer extends IERecipeSerializer<ElectrolyzerRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.crucible);
    }

    @Override
    public ElectrolyzerRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize input = IngredientWithSize.deserialize(json.get("input"));
        FluidTagInput input_fluid = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid"));
        int time = JSONUtils.getInt(json, "time");
        return new ElectrolyzerRecipe(recipeId, output, input, input_fluid, time);
    }

    @Nullable
    @Override
    public ElectrolyzerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize input = IngredientWithSize.read(buffer);
        FluidTagInput input_fluid = FluidTagInput.read(buffer);
        int time = buffer.readInt();
        return new ElectrolyzerRecipe(recipeId, output, input, input_fluid, time);
    }

    @Override
    public void write(PacketBuffer buffer, ElectrolyzerRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        recipe.input_fluid.write(buffer);
        buffer.writeInt(recipe.time);
    }
}
