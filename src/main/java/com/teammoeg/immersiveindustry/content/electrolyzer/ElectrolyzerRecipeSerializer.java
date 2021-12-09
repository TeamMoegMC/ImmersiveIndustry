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

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;

public class ElectrolyzerRecipeSerializer extends IERecipeSerializer<ElectrolyzerRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.crucible);
    }

    @Override
    public ElectrolyzerRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize[] inputs =null;
        if(json.has("inputs")) {
        	JsonArray ja=json.get("inputs").getAsJsonArray();
        	inputs=new IngredientWithSize[ja.size()];
        	int i=-1;
        	for(JsonElement je:ja) {
        		inputs[++i]=IngredientWithSize.deserialize(je);
        	}
        }else if(json.has("input")) {
        	inputs=new IngredientWithSize[1];
        	inputs[0]=IngredientWithSize.deserialize(json.get("input"));
        }else inputs=new IngredientWithSize[0];
        FluidTagInput input_fluid =null;
        if(json.has("fluid"))
        	FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid"));
        int time = JSONUtils.getInt(json, "time");
        boolean flag = JSONUtils.getBoolean(json, "large_only",false);
        FluidTagInput result_fluid =null;
        if(json.has("result_fluid"))
        	result_fluid=FluidTagInput.deserialize(json.get("result_fluid"));
        if(inputs.length==0&&input_fluid==null)
        	throw new JsonSyntaxException("Must contain more than 1 input");
        return new ElectrolyzerRecipe(recipeId, output, inputs, input_fluid,result_fluid, time, flag);
    }

    @Nullable
    @Override
    public ElectrolyzerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize[] inputs=new IngredientWithSize[buffer.readVarInt()];
        for(int i=0;i<inputs.length;i++)
        	inputs[i]=IngredientWithSize.read(buffer);
        FluidTagInput input_fluid =null;
        FluidTagInput output_fluid =null;
        if(buffer.readBoolean())
        	input_fluid=FluidTagInput.read(buffer);
        if(buffer.readBoolean())
        	output_fluid=FluidTagInput.read(buffer);
        int time = buffer.readInt();
        boolean flag = buffer.readBoolean();
        return new ElectrolyzerRecipe(recipeId, output, inputs, input_fluid,output_fluid, time, flag);
    }

    @Override
    public void write(PacketBuffer buffer, ElectrolyzerRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        buffer.writeVarInt(recipe.inputs.length);
        for(IngredientWithSize input:recipe.inputs)
        	input.write(buffer);
        if(recipe.input_fluid!=null) {
        	buffer.writeBoolean(true);
        	recipe.input_fluid.write(buffer);
        }else buffer.writeBoolean(false);
        if(recipe.output_fluid!=null) {
        	buffer.writeBoolean(true);
        	recipe.output_fluid.write(buffer);
        }else buffer.writeBoolean(false);
        buffer.writeInt(recipe.time);
        buffer.writeBoolean(recipe.flag);
    }
}
