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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.fluids.FluidStack;

public class ElectrolyzerRecipeSerializer extends IERecipeSerializer<ElectrolyzerRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIBlocks.electrolyzer.get());
    }

	@Override
	public @org.jetbrains.annotations.Nullable ElectrolyzerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		ItemStack output = buffer.readItem();
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
	public void toNetwork(FriendlyByteBuf buffer, ElectrolyzerRecipe recipe) {
		buffer.writeItem(recipe.output);
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

	@Override
	public ElectrolyzerRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context) {
		ItemStack output = readOutput(json.get("result")).get();
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
            input_fluid = FluidTagInput.deserialize(json.get("fluid"));

        int time = 200;
        if (json.has("time"))
            time = json.get("time").getAsInt();
        int tickEnergy = IIConfig.COMMON.electrolyzerBase.get();
        if (json.has("tickEnergy"))
            tickEnergy = json.get("tickEnergy").getAsInt();

        boolean flag =false;
        if(json.has("large_only"))
        	flag=json.get("large_only").getAsBoolean();
        FluidStack result_fluid = FluidStack.EMPTY;
        if (json.has("result_fluid"))
            result_fluid = ApiUtils.jsonDeserializeFluidStack(json.get("result_fluid").getAsJsonObject());

        return new ElectrolyzerRecipe(recipeId, output, inputs, input_fluid, result_fluid, time, tickEnergy, flag);
	}
}
