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

import java.util.Optional;

import com.teammoeg.immersiveindustry.IIContent;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Codec-based serializer for {@link RotaryKilnRecipe}.
 *
 * <p>The old reader resolved the {@code byproduct} through a condition context, which no
 * longer travels with recipes in 1.21 - conditions are applied by the recipe manager before
 * the codec sees the JSON. The field is therefore decoded as a plain optional
 * {@link StackWithChance}.
 */
public class RotaryKilnRecipeSerializer extends IERecipeSerializer<RotaryKilnRecipe> {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.ROTARY_KILN.blockItem().get());
    }

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, RotaryKilnRecipe> codecs() {
        return DualCompositeMapCodecs.composite(
                DualCodecs.ITEM_STACK.optionalFieldOf("result", ItemStack.EMPTY),
                (RotaryKilnRecipe r) -> r.output,
                IngredientWithSize.CODECS.fieldOf("input"),
                (RotaryKilnRecipe r) -> r.input,
                optionalFluidOutput("result_fluid"),
                (RotaryKilnRecipe r) -> r.output_fluid == null ? FluidStack.EMPTY : r.output_fluid,
                DualCodecs.INT.optionalFieldOf("time", 200),
                (RotaryKilnRecipe r) -> r.time,
                DualCodecs.INT.optionalFieldOf("tickEnergy", 32),
                (RotaryKilnRecipe r) -> r.tickEnergy,
                StackWithChance.CODECS.optionalFieldOf("byproduct"),
                (RotaryKilnRecipe r) -> Optional.ofNullable(r.secoutput),
                (ItemStack output, IngredientWithSize input, FluidStack outputFluid,
                 Integer time, Integer tickEnergy, Optional<StackWithChance> byproduct) -> new RotaryKilnRecipe(
                        output,
                        input,
                        outputFluid,
                        time,
                        tickEnergy,
                        byproduct.orElse(null)));
    }
}
