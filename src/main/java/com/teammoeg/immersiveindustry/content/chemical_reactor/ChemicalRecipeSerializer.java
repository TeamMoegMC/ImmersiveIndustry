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

package com.teammoeg.immersiveindustry.content.chemical_reactor;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;

public class ChemicalRecipeSerializer extends IERecipeSerializer<ChemicalRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.CRUCIBLE.blockItem().get());
    }

    @Override
    public ChemicalRecipe readFromJson(ResourceLocation recipeId, JsonObject json,IContext ctx) {

        IngredientWithSize[] inputs;
        if (json.has("inputs")) {
            JsonArray ja = json.get("inputs").getAsJsonArray();
            inputs = new IngredientWithSize[ja.size()];
            int i = -1;
            for (JsonElement je : ja) {
                inputs[++i] = IngredientWithSize.deserialize(je);
            }
        } else inputs = new IngredientWithSize[0];
        FluidTagInput[] input_fluids;
        if (json.has("input_fluids")) {
        	JsonArray ja = json.get("input_fluids").getAsJsonArray();
        	input_fluids=new FluidTagInput[ja.size()];
        	int i = -1;
            for (JsonElement je : ja) {
            	input_fluids[++i] = FluidTagInput.deserialize(je);
            }
        }else input_fluids=new FluidTagInput[0];
        
        ItemStack[] outputs;
        if (json.has("outputs")) {
            JsonArray ja = json.get("outputs").getAsJsonArray();
            outputs = new ItemStack[ja.size()];
            int i = -1;
            for (JsonElement je : ja) {
            	outputs[++i] =readOutput(je).get();
            }
        } else outputs = new ItemStack[0];
        
        FluidStack[] output_fluids;
        if (json.has("result_fluids")) {
            JsonArray ja = json.get("result_fluids").getAsJsonArray();
            output_fluids = new FluidStack[ja.size()];
            int i = -1;
            for (JsonElement je : ja) {
            	output_fluids[++i] =FluidStack.CODEC.parse(JsonOps.INSTANCE, je).resultOrPartial(t->{throw new JsonSyntaxException(t);}).get();
            }
        } else output_fluids = new FluidStack[0];
        int time=400;
        if(json.has("time"))
        time = json.get("time").getAsInt();
        int tickEnergy=IIConfig.COMMON.chemicalBase.get();
        if(json.has("tickEnergy"))
        	tickEnergy = json.get("tickEnergy").getAsInt();
        if(inputs.length==0&&input_fluids.length==0)
        	throw new JsonSyntaxException("Error loading chemical recipe "+recipeId+" because no input found");
        if(outputs.length==0&&output_fluids.length==0)
        	throw new JsonSyntaxException("Error loading chemical recipe "+recipeId+" because no output found");
        return new ChemicalRecipe(recipeId, outputs, output_fluids, inputs, input_fluids,time,tickEnergy);
    }

    @Nullable
    @Override
    public ChemicalRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        IngredientWithSize[] inputs = new IngredientWithSize[buffer.readVarInt()];
        for (int i = 0; i < inputs.length; i++)
            inputs[i] = IngredientWithSize.read(buffer);
        FluidTagInput[] input_fluids=new FluidTagInput[buffer.readVarInt()];
        for (int i = 0; i < input_fluids.length; i++)
        	input_fluids[i] = FluidTagInput.read(buffer);
        ItemStack[] outputs=new ItemStack[buffer.readVarInt()];
        for (int i = 0; i < outputs.length; i++)
        	outputs[i] = buffer.readItem();
        FluidStack[] output_fluids = new FluidStack[buffer.readVarInt()];
        for (int i = 0; i < output_fluids.length; i++)
        	output_fluids[i] = FluidStack.readFromPacket(buffer);
        int time = buffer.readVarInt();
        int tickEnergy=buffer.readVarInt();
        return new ChemicalRecipe(recipeId, outputs, output_fluids, inputs, input_fluids,time,tickEnergy);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ChemicalRecipe recipe) {
        buffer.writeVarInt(recipe.inputs.length);
        for (IngredientWithSize input : recipe.inputs)
            input.write(buffer);
        
        buffer.writeVarInt(recipe.input_fluids.length);
        for (FluidTagInput input : recipe.input_fluids)
            input.write(buffer);
        
        buffer.writeVarInt(recipe.outputs.length);
        for (ItemStack input : recipe.outputs)
            buffer.writeItem(input);
        
        buffer.writeVarInt(recipe.output_fluids.length);
        for (FluidStack input : recipe.output_fluids)
        	input.writeToPacket(buffer);
        
        
        
        buffer.writeVarInt(recipe.time);
        buffer.writeVarInt(recipe.tickEnergy);
    }


}
