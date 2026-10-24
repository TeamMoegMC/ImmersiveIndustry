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
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Serialises {@link CarKilnRecipe} through a single {@link DualMapCodec}.
 *
 * <p>1.21 replaced the old {@code readFromJson} / {@code toNetwork} pair with one codec that
 * serves both the data pack and the network. IE's {@code IERecipeSerializer} keeps
 * {@code codec()} and {@code streamCodec()} final and asks subclasses for the dual codec, so
 * the fields are declared once here.
 *
 * <p>Note the JSON keys keep their 1.20.1 names ({@code results}, {@code inputs},
 * {@code input_fluid}, {@code time}, {@code tickEnergy}) so existing data packs keep working.
 */
public class CarKilnRecipeSerializer extends IERecipeSerializer<CarKilnRecipe> {

    private static final DualCodec<RegistryFriendlyByteBuf, SizedFluidIngredient> FLUID_INGREDIENT =
            new DualCodec<>(SizedFluidIngredient.FLAT_CODEC, SizedFluidIngredient.STREAM_CODEC);

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IIContent.IIMultiblocks.CAR_KILN.blockItem().get());
    }

    @Override
    protected DualMapCodec<RegistryFriendlyByteBuf, CarKilnRecipe> codecs() {
        return DualCompositeMapCodecs.composite(
                DualCodecs.ITEM_STACK.listOf().fieldOf("results"),
                (CarKilnRecipe r) -> List.of(r.output),
                IngredientWithSize.CODECS.listOf().optionalFieldOf("inputs", List.of()),
                (CarKilnRecipe r) -> List.of(r.inputs),
                FLUID_INGREDIENT.optionalFieldOf("input_fluid"),
                (CarKilnRecipe r) -> Optional.ofNullable(r.input_fluid),
                DualCodecs.INT.fieldOf("time"),
                (CarKilnRecipe r) -> r.time,
                DualCodecs.INT.fieldOf("tickEnergy"),
                (CarKilnRecipe r) -> r.tickEnergy,
                (List<ItemStack> output, List<IngredientWithSize> inputs, Optional<SizedFluidIngredient> fluid,
                 Integer time, Integer tickEnergy) -> new CarKilnRecipe(
                        output.toArray(new ItemStack[0]),
                        inputs.toArray(new IngredientWithSize[0]),
                        fluid.orElse(null),
                        time,
                        tickEnergy));
    }
}
