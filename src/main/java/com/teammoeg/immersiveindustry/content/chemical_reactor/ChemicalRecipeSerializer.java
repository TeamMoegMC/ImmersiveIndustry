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

import java.util.List;

import com.teammoeg.immersiveindustry.IIContent;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import malte0811.dualcodecs.DualCodec;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Codec-based serializer for {@link ChemicalRecipe}.
 *
 * <p>Both the item and the fluid sides are arrays here, so each becomes a {@code listOf()}
 * field. Fluid <em>stacks</em> use {@link FluidStack#OPTIONAL_CODEC} plus the matching
 * optional stream codec, because a recipe may produce no fluid at all.
 */
public class ChemicalRecipeSerializer extends IERecipeSerializer<ChemicalRecipe> {

    private static final DualCodec<RegistryFriendlyByteBuf, SizedFluidIngredient> FLUID_INGREDIENT =
            new DualCodec<>(SizedFluidIngredient.FLAT_CODEC, SizedFluidIngredient.STREAM_CODEC);

    private static final DualCodec<RegistryFriendlyByteBuf, FluidStack> FLUID_STACK =
            new DualCodec<>(FluidStack.OPTIONAL_CODEC, FluidStack.OPTIONAL_STREAM_CODEC);

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.CRUCIBLE.blockItem().get());
    }

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, ChemicalRecipe> codecs() {
        return DualCompositeMapCodecs.composite(
                IngredientWithSize.CODECS.listOf().fieldOf("inputs"),
                (ChemicalRecipe r) -> List.of(r.inputs),
                FLUID_INGREDIENT.listOf().fieldOf("input_fluids"),
                (ChemicalRecipe r) -> List.of(r.input_fluids),
                DualCodecs.ITEM_STACK.listOf().fieldOf("outputs"),
                (ChemicalRecipe r) -> List.of(r.outputs),
                FLUID_STACK.listOf().fieldOf("result_fluids"),
                (ChemicalRecipe r) -> List.of(r.output_fluids),
                DualCodecs.INT.optionalFieldOf("time", 200),
                (ChemicalRecipe r) -> r.time,
                DualCodecs.INT.optionalFieldOf("tickEnergy", 32),
                (ChemicalRecipe r) -> r.tickEnergy,
                (List<IngredientWithSize> inputs, List<SizedFluidIngredient> inputFluids,
                 List<ItemStack> outputs, List<FluidStack> outputFluids,
                 Integer time, Integer tickEnergy) -> new ChemicalRecipe(
                        outputs.toArray(new ItemStack[0]),
                        outputFluids.toArray(new FluidStack[0]),
                        inputs.toArray(new IngredientWithSize[0]),
                        inputFluids.toArray(new SizedFluidIngredient[0]),
                        time,
                        tickEnergy));
    }
}
