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

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class RotaryKilnRecipeSerializer extends IERecipeSerializer<RotaryKilnRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.rotary_kiln);
    }

    @Override
    public RotaryKilnRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize input = IngredientWithSize.deserialize(json.get("input"));
        FluidStack result_fluid = null;
        if (json.has("result_fluid"))
            result_fluid = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "result_fluid"));
        int time=200;
        if(json.has("time"))
        	time=json.get("time").getAsInt();
        boolean heated=false;
        if(json.has("heated"))
        	heated=json.get("heated").getAsBoolean();
        return new RotaryKilnRecipe(recipeId, output, input, result_fluid,time,heated);
    }

    @Nullable
    @Override
    public RotaryKilnRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize input = IngredientWithSize.read(buffer);
        FluidStack output_fluid = null;
        if (buffer.readBoolean())
            output_fluid = FluidStack.readFromPacket(buffer);
        return new RotaryKilnRecipe(recipeId, output, input, output_fluid,buffer.readVarInt(),buffer.readBoolean());
    }

    @Override
    public void write(PacketBuffer buffer, RotaryKilnRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        if (recipe.output_fluid != null) {
            buffer.writeBoolean(true);
            recipe.output_fluid.writeToPacket(buffer);
        } else buffer.writeBoolean(false);
        buffer.writeVarInt(recipe.time);
        buffer.writeBoolean(recipe.heat);
    }
}
