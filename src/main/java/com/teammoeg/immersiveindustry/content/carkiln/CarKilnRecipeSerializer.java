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

package com.teammoeg.immersiveindustry.content.carkiln;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class CarKilnRecipeSerializer extends IERecipeSerializer<CarKilnRecipe> {
	@Override
	public ItemStack getIcon() {
		return new ItemStack(IIContent.IIMultiblocks.car_kiln);
	}

	@Override
	public CarKilnRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
		ItemStack output = readOutput(json.get("result"));
		IngredientWithSize[] inputs;
		if (json.has("inputs")) {
			JsonArray ja = json.get("inputs").getAsJsonArray();
			inputs = new IngredientWithSize[ja.size()];
			int i = -1;
			for (JsonElement je : ja) {
				inputs[++i] = IngredientWithSize.deserialize(je);
			}
		} else if (json.has("input")) {
			inputs = new IngredientWithSize[1];
			inputs[0] = IngredientWithSize.deserialize(json.get("input"));
		} else
			inputs = new IngredientWithSize[0];
		FluidStack input_fluid = FluidStack.EMPTY;
		if (json.has("input_fluid"))
			input_fluid = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "input_fluid"));

		int time = 200;
		if (json.has("time"))
			time = json.get("time").getAsInt();
		int tickEnergy = IIConfig.COMMON.carKilnBase.get();
		if (json.has("tickEnergy"))
			tickEnergy = json.get("tickEnergy").getAsInt();
		int start_fluid_cost=0;
		if(json.has("start_fluid_cost"))
			start_fluid_cost=json.get("start_fluid_cost").getAsInt();
		return new CarKilnRecipe(recipeId, output, inputs, input_fluid, time, tickEnergy,start_fluid_cost);
	}

	@Nullable
	@Override
	public CarKilnRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack output = buffer.readItemStack();
		IngredientWithSize[] inputs = new IngredientWithSize[buffer.readVarInt()];
		for (int i = 0; i < inputs.length; i++)
			inputs[i] = IngredientWithSize.read(buffer);
		return new CarKilnRecipe(recipeId, output, inputs, FluidStack.readFromPacket(buffer),buffer.readVarInt(),buffer.readVarInt(),buffer.readVarInt());
	}

	@Override
	public void write(PacketBuffer buffer, CarKilnRecipe recipe) {
		buffer.writeItemStack(recipe.output);
		buffer.writeVarInt(recipe.inputs.length);
		for (IngredientWithSize input : recipe.inputs)
			input.write(buffer);
		recipe.input_fluid.writeToPacket(buffer);
		buffer.writeVarInt(recipe.time);
		buffer.writeVarInt(recipe.tickEnergy);
		buffer.writeVarInt(recipe.start_fluid_cost);
	}
}
