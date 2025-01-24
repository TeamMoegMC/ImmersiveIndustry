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

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import java.util.ArrayList;

public class CarKilnRecipeSerializer extends IERecipeSerializer<CarKilnRecipe> {
	@Override
	public ItemStack getIcon() {
		return new ItemStack(IIContent.IIMultiblocks.CAR_KILN.blockItem().get());
	}

	@Override
	public @org.jetbrains.annotations.Nullable CarKilnRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int outs=buffer.readVarInt();
		ItemStack[] output=new ItemStack[outs] ;
		for(int i=0;i<outs;i++)
			output[i]= buffer.readItem();
		IngredientWithSize[] inputs = new IngredientWithSize[buffer.readVarInt()];
		for (int i = 0; i < inputs.length; i++)
			inputs[i] = IngredientWithSize.read(buffer);
		return new CarKilnRecipe(recipeId, output, inputs, FluidTagInput.read(buffer),buffer.readVarInt(),buffer.readVarInt());
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CarKilnRecipe recipe) {
		buffer.writeVarInt(recipe.output.length);
		for(int i=0;i<recipe.output.length;i++)
			buffer.writeItem(recipe.output[i]);
		buffer.writeVarInt(recipe.inputs.length);
		for (IngredientWithSize input : recipe.inputs)
			input.write(buffer);
		recipe.input_fluid.write(buffer);
		buffer.writeVarInt(recipe.time);
		buffer.writeVarInt(recipe.tickEnergy);
	}

	@Override
	public CarKilnRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context) {
		ItemStack[] output;
		if(json.has("results")) {
			JsonArray ja=json.get("results").getAsJsonArray();
			ArrayList<ItemStack> ops=new ArrayList<>();
			for(JsonElement je:ja) {
				ops.add(readOutput(je).get());
			}
			output=ops.toArray(new ItemStack[0]);
				
		}else
			output= new ItemStack[]{readOutput(json.get("result")).get()};
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
		FluidTagInput input_fluid = null;
		if (json.has("input_fluid"))
			input_fluid = FluidTagInput.deserialize(json.get("input_fluid"));

		int time = 200;
		if (json.has("time"))
			time = json.get("time").getAsInt();
		int tickEnergy = IIConfig.COMMON.carKilnBase.get();
		if (json.has("tickEnergy"))
			tickEnergy = json.get("tickEnergy").getAsInt();
		return new CarKilnRecipe(recipeId, output, inputs, input_fluid, time, tickEnergy);
	}
}
