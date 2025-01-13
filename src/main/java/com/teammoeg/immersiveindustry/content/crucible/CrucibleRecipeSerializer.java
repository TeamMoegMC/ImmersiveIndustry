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

package com.teammoeg.immersiveindustry.content.crucible;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teammoeg.immersiveindustry.IIContent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class CrucibleRecipeSerializer extends IERecipeSerializer<CrucibleRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.CRUCIBLE.blockItem().get());
    }

    @Override
    public CrucibleRecipe readFromJson(ResourceLocation recipeId, JsonObject json,IContext ctx) {
        Lazy<ItemStack> output = readOutput(json.get("result"));
        IngredientWithSize[] inputs;
        if (json.has("inputs")) {
            JsonArray ja = json.get("inputs").getAsJsonArray();
            inputs = new IngredientWithSize[ja.size()];
            int i = -1;
            for (JsonElement je : ja) {
                inputs[++i] = IngredientWithSize.deserialize(je);
            }
        } else inputs = new IngredientWithSize[0];
        FluidStack result_fluid = FluidStack.EMPTY;
        if (json.has("result_fluid"))
            result_fluid = ApiUtils.jsonDeserializeFluidStack(json.getAsJsonObject("result_fluid"));
        int time=400;
        if(json.has("time"))
        time = json.get("time").getAsInt();
        int temperature=0;
        if(json.has("temperature"))
        	temperature= json.get("temperature").getAsInt();
        if(inputs==null||inputs.length==0)
        	throw new RuntimeException("Error loading crucible recipe "+recipeId);
        return new CrucibleRecipe(recipeId, output, result_fluid, inputs, time, temperature);
    }

    @Nullable
    @Override
    public CrucibleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        ItemStack output = buffer.readItem();
        IngredientWithSize[] inputs = new IngredientWithSize[buffer.readVarInt()];
        for (int i = 0; i < inputs.length; i++)
            inputs[i] = IngredientWithSize.read(buffer);
        FluidStack output_fluid = null;
        if (buffer.readBoolean())
            output_fluid = FluidStack.readFromPacket(buffer);
        int time = buffer.readInt();
        int temperature = buffer.readInt();
        return new CrucibleRecipe(recipeId, Lazy.of(()->output), output_fluid, inputs, time, temperature);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CrucibleRecipe recipe) {
        buffer.writeItem(recipe.output.get());
        buffer.writeVarInt(recipe.inputs.length);
        for (IngredientWithSize input : recipe.inputs)
            input.write(buffer);
        if (recipe.output_fluid != null) {
            buffer.writeBoolean(true);
            recipe.output_fluid.writeToPacket(buffer);
        } else buffer.writeBoolean(false);
        buffer.writeInt(recipe.time);
        buffer.writeInt(recipe.temperature);
    }


}
