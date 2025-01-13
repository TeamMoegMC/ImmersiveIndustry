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
import blusunrize.immersiveengineering.api.crafting.StackWithChance;

import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.fluids.FluidStack;

public class RotaryKilnRecipeSerializer extends IERecipeSerializer<RotaryKilnRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.ROTARY_KILN.blockItem().get());
    }

	@Override
	public @org.jetbrains.annotations.Nullable RotaryKilnRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		 return new RotaryKilnRecipe(recipeId,buffer.readItem(), IngredientWithSize.read(buffer),FluidStack.readFromPacket(buffer),buffer.readVarInt(),buffer.readVarInt(),buffer.readBoolean()?StackWithChance.read(buffer):null);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, RotaryKilnRecipe recipe) {
		buffer.writeItem(recipe.output);
        recipe.input.write(buffer);
        recipe.output_fluid.writeToPacket(buffer);
        buffer.writeVarInt(recipe.time);
        buffer.writeVarInt(recipe.tickEnergy);
        if(recipe.secoutput==null)
        	buffer.writeBoolean(false);
        else {
        	buffer.writeBoolean(true);
        	recipe.secoutput.write(buffer);
        }
	}

	@Override
	public RotaryKilnRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context) {
		ItemStack output = readOutput(json.get("result")).get();
        IngredientWithSize input = IngredientWithSize.deserialize(json.get("input"));
        FluidStack result_fluid = FluidStack.EMPTY;
        if (json.has("result_fluid"))
            result_fluid = ApiUtils.jsonDeserializeFluidStack(json.get("result_fluid").getAsJsonObject());

        int time = 200;
        if (json.has("time"))
            time = json.get("time").getAsInt();
        int tickEnergy = IIConfig.COMMON.rotaryKilnBase.get();
        if (json.has("tickEnergy"))
            tickEnergy = json.get("tickEnergy").getAsInt();
        StackWithChance byout=null;
        if(json.has("byproduct")) {
        	byout=readConditionalStackWithChance(json.get("byproduct"),context);
        }
        	

        return new RotaryKilnRecipe(recipeId, output, input, result_fluid, time, tickEnergy,byout);
	}
}
