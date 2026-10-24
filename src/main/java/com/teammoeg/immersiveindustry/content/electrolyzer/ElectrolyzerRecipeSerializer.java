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

import java.util.List;
import java.util.Optional;

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
 * Codec-based serializer for {@link ElectrolyzerRecipe}.
 *
 * <p>Field names match the 1.20.1 JSON so existing data packs keep loading. Two behaviours the
 * old hand-written reader had are preserved through DualCodecs helpers rather than by
 * hand: {@code listOrSingle} keeps accepting both {@code inputs} and a lone {@code input},
 * and {@code optionalFieldOf(name, default)} keeps the historic defaults for
 * {@code time} / {@code tickEnergy} / {@code large_only}.
 */
public class ElectrolyzerRecipeSerializer extends IERecipeSerializer<ElectrolyzerRecipe> {

    private static final DualCodec<RegistryFriendlyByteBuf, SizedFluidIngredient> FLUID_INGREDIENT =
            new DualCodec<>(SizedFluidIngredient.FLAT_CODEC, SizedFluidIngredient.STREAM_CODEC);

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIBlocks.electrolyzer.get());
    }

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, ElectrolyzerRecipe> codecs() {
        return DualCompositeMapCodecs.composite(
                DualCodecs.ITEM_STACK.fieldOf("result"),
                (ElectrolyzerRecipe r) -> r.output,
                IngredientWithSize.CODECS.listOf().optionalFieldOf("inputs", List.of()),
                (ElectrolyzerRecipe r) -> List.of(r.inputs),
                FLUID_INGREDIENT.optionalFieldOf("fluid"),
                (ElectrolyzerRecipe r) -> Optional.ofNullable(r.input_fluid),
                optionalFluidOutput("result_fluid"),
                (ElectrolyzerRecipe r) -> r.output_fluid == null ? FluidStack.EMPTY : r.output_fluid,
                DualCodecs.INT.optionalFieldOf("time", 200),
                (ElectrolyzerRecipe r) -> r.time,
                DualCodecs.INT.optionalFieldOf("tickEnergy", 32),
                (ElectrolyzerRecipe r) -> r.tickEnergy,
                DualCodecs.BOOL.optionalFieldOf("large_only", false),
                (ElectrolyzerRecipe r) -> r.flag,
                (ItemStack output, List<IngredientWithSize> inputs, Optional<SizedFluidIngredient> fluid,
                 FluidStack outputFluid, Integer time, Integer tickEnergy, Boolean flag) -> new ElectrolyzerRecipe(
                        output,
                        inputs.toArray(new IngredientWithSize[0]),
                        fluid.orElse(null),
                        outputFluid,
                        time,
                        tickEnergy,
                        flag));
    }
}
