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

import java.util.List;

import com.teammoeg.immersiveindustry.IIContent;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Codec-based serializer for {@link CrucibleRecipe}.
 *
 * <p>The item output is decoded straight as a {@link TagOutput}, which is what
 * {@code IESerializableRecipe} now wants as its preview stack. The 1.20.1 reader deferred that
 * with a {@code Lazy} because the item might not exist yet; the codec path resolves the same
 * problem by keeping the tag-vs-stack decision inside {@code TagOutput} itself.
 */
public class CrucibleRecipeSerializer extends IERecipeSerializer<CrucibleRecipe> {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.CRUCIBLE.blockItem().get());
    }

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, CrucibleRecipe> codecs() {
        return DualCompositeMapCodecs.composite(
                TagOutput.CODECS.fieldOf("result"),
                (CrucibleRecipe r) -> r.output,
                IngredientWithSize.CODECS.listOf().fieldOf("inputs"),
                (CrucibleRecipe r) -> List.of(r.inputs),
                optionalFluidOutput("result_fluid"),
                (CrucibleRecipe r) -> r.output_fluid == null ? FluidStack.EMPTY : r.output_fluid,
                DualCodecs.INT.optionalFieldOf("time", 400),
                (CrucibleRecipe r) -> r.time,
                DualCodecs.INT.optionalFieldOf("temperature", 0),
                (CrucibleRecipe r) -> r.temperature,
                (TagOutput output, List<IngredientWithSize> inputs, FluidStack outputFluid,
                 Integer time, Integer temperature) -> new CrucibleRecipe(
                        output,
                        outputFluid,
                        inputs.toArray(new IngredientWithSize[0]),
                        time,
                        temperature));
    }
}
