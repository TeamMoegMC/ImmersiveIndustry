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

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class ElectrolyzerRecipeSerializer extends IERecipeSerializer<ElectrolyzerRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIBlocks.electrolyzer);
    }

    @Override
    public ElectrolyzerRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize[] inputs;
        if(json.has("inputs")) {
        	JsonArray ja=json.get("inputs").getAsJsonArray();
        	inputs=new IngredientWithSize[ja.size()];
        	int i=-1;
            for (JsonElement je : ja) {
                inputs[++i] = IngredientWithSize.deserialize(je);
            }
        } else if (json.has("input")) {
            inputs = new IngredientWithSize[1];
            inputs[0] = IngredientWithSize.deserialize(json.get("input"));
        } else inputs = new IngredientWithSize[0];
        FluidTagInput input_fluid = null;
        if (json.has("fluid"))
            input_fluid = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid"));

        int time = 200;
        if (json.has("time"))
            time = json.get("time").getAsInt();
        int tickEnergy = 32;
        if (json.has("tickEnergy"))
            tickEnergy = json.get("tickEnergy").getAsInt();

        boolean flag = JSONUtils.getBoolean(json, "large_only", false);
        FluidStack result_fluid = null;
        if (json.has("result_fluid"))
            result_fluid = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "result_fluid"));

        return new ElectrolyzerRecipe(recipeId, output, inputs, input_fluid, result_fluid, time, tickEnergy, flag);
    }

    @Nullable
    @Override
    public ElectrolyzerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize[] inputs = new IngredientWithSize[buffer.readVarInt()];
        for (int i = 0; i < inputs.length; i++)
            inputs[i] = IngredientWithSize.read(buffer);
        FluidTagInput input_fluid = null;
        FluidStack output_fluid = null;
        if (buffer.readBoolean())
            input_fluid = FluidTagInput.read(buffer);
        if (buffer.readBoolean())
            output_fluid = FluidStack.readFromPacket(buffer);
        int time = buffer.readVarInt();
        int tickEnergy = buffer.readVarInt();
        boolean flag = buffer.readBoolean();
        return new ElectrolyzerRecipe(recipeId, output, inputs, input_fluid, output_fluid, time, tickEnergy, flag);
    }

    @Override
    public void write(PacketBuffer buffer, ElectrolyzerRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        buffer.writeVarInt(recipe.inputs.length);
        for (IngredientWithSize input : recipe.inputs)
            input.write(buffer);
        if (recipe.input_fluid != null) {
            buffer.writeBoolean(true);
            recipe.input_fluid.write(buffer);
        } else buffer.writeBoolean(false);
        if (recipe.output_fluid != null) {
            buffer.writeBoolean(true);
            recipe.output_fluid.writeToPacket(buffer);
        } else buffer.writeBoolean(false);
        buffer.writeVarInt(recipe.time);
        buffer.writeVarInt(recipe.tickEnergy);
        buffer.writeBoolean(recipe.flag);
    }
}
