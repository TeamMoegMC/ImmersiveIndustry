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

package com.teammoeg.immersiveindustry;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IIMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Remapper {
    @SubscribeEvent
    public static void registerBlockRemappings(final RegistryEvent.MissingMappings<Block> event) {
        Map<ResourceLocation, Block> blockRemappings = IIRemappings.getBlockRemappings();
        ImmutableList<RegistryEvent.MissingMappings.Mapping<Block>> mappings = event.getAllMappings();
        for (RegistryEvent.MissingMappings.Mapping<Block> map : mappings) {
            if (blockRemappings.containsKey(map.key)) {
                map.remap(blockRemappings.get(map.key));
            }
        }
    }

    public static class IIRemappings {
        public static Map<ResourceLocation, Block> blockRemappings = new HashMap<>();

        public static Map<ResourceLocation, Block> getBlockRemappings() {
            blockRemappings.put(new ResourceLocation("frostedheart:crucible"), IIContent.IIMultiblocks.crucible);
            blockRemappings.put(new ResourceLocation("frostedheart:steam_turbine"), IIContent.IIMultiblocks.steam_turbine);
            blockRemappings.put(new ResourceLocation("frostedheart:burning_chamber"), IIContent.IIBlocks.burning_chamber);
            return blockRemappings;
        }

    }
}
