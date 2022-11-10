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
import com.teammoeg.immersiveindustry.IIConfig;
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
        FluidStack result_fluid = FluidStack.EMPTY;
        if (json.has("result_fluid"))
            result_fluid = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "result_fluid"));

        int time = 200;
        if (json.has("time"))
            time = json.get("time").getAsInt();
        int tickEnergy = IIConfig.COMMON.rotaryKilnBase.get();
        if (json.has("tickEnergy"))
            tickEnergy = json.get("tickEnergy").getAsInt();
        ItemStack byout=ItemStack.EMPTY;
        float chance=0;
        if(json.has("byproduct")) {
        	byout=readOutput(json.get("byproduct"));
	        chance=1;
	        if(json.has("chance"))
	        	chance=json.get("chance").getAsFloat();
        }
        	

        return new RotaryKilnRecipe(recipeId, output, input, result_fluid, time, tickEnergy,byout,chance);
    }

    @Nullable
    @Override
    public RotaryKilnRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
  
       
        return new RotaryKilnRecipe(recipeId,buffer.readItemStack(), IngredientWithSize.read(buffer),FluidStack.readFromPacket(buffer),buffer.readVarInt(),buffer.readVarInt(),buffer.readItemStack(),buffer.readFloat());
    }

    @Override
    public void write(PacketBuffer buffer, RotaryKilnRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        recipe.output_fluid.writeToPacket(buffer);
        buffer.writeVarInt(recipe.time);
        buffer.writeVarInt(recipe.tickEnergy);
        buffer.writeItemStack(recipe.secoutput);
        buffer.writeFloat(recipe.secoutputchance);
    }
}
