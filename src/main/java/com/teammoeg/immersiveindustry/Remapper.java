package com.teammoeg.immersiveindustry;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

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

    @SubscribeEvent
    public static void registerTileRemappings(final RegistryEvent.MissingMappings<TileEntityType<?>> event) {
        Map<ResourceLocation, TileEntityType<?>> teRemappings = IIRemappings.getTeRemappings();
        ImmutableList<RegistryEvent.MissingMappings.Mapping<TileEntityType<?>>> mappings = event.getAllMappings();
        for (RegistryEvent.MissingMappings.Mapping<TileEntityType<?>> map : mappings) {
            if (teRemappings.containsKey(map.key)) {
                map.remap(teRemappings.get(map.key));
            }
        }
    }

    public static class IIRemappings {
        public static Map<ResourceLocation, Block> blockRemappings = new HashMap<>();
        public static Map<ResourceLocation, TileEntityType<?>> teRemappings = new HashMap<>();

        public static Map<ResourceLocation, Block> getBlockRemappings() {
            blockRemappings.put(new ResourceLocation("frostedheart:crucible"), IIContent.IIMultiblocks.crucible);
            blockRemappings.put(new ResourceLocation("frostedheart:steam_turbine"), IIContent.IIMultiblocks.steam_turbine);
            return blockRemappings;
        }

        public static Map<ResourceLocation, TileEntityType<?>> getTeRemappings() {
            teRemappings.put(new ResourceLocation("frostedheart:crucible"), IIContent.IITileTypes.CRUCIBLE.get());
            teRemappings.put(new ResourceLocation("frostedheart:steam_turbine"), IIContent.IITileTypes.STEAMTURBINE.get());
            return teRemappings;
        }

    }
}
