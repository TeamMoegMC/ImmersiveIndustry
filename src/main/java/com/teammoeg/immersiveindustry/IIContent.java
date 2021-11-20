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

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import com.google.common.collect.ImmutableSet;
import com.teammoeg.immersiveindustry.content.IIBaseBlock;
import com.teammoeg.immersiveindustry.content.IIBlockItem;
import com.teammoeg.immersiveindustry.content.crucible.*;
import com.teammoeg.immersiveindustry.content.electrolyzer.*;
import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineBlock;
import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineMultiblock;
import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class IIContent {
    public static List<Block> registeredBlocks = new ArrayList<>();
    public static List<Item> registeredItems = new ArrayList<>();

    public static class IIBlocks {
        public static void init() {
        }

        public static Block electrolyzer = new ElectrolyzerBlock("electrolyzer", IIProps.METALProps, IIBlockItem::new);
        public static Block burning_chamber = new IIBaseBlock("burning_chamber", IIProps.METALProps, IIBlockItem::new);
    }

    public static class IIMultiblocks {
        public static final IETemplateMultiblock CRUCIBLE = new CrucibleMultiblock();
        public static final IETemplateMultiblock STEAMTURBINE = new SteamTurbineMultiblock();

        public static Block crucible = new CrucibleBlock("crucible", IITileTypes.CRUCIBLE);
        public static Block steam_turbine = new SteamTurbineBlock("steam_turbine", IITileTypes.STEAMTURBINE);

        public static void init() {
            MultiblockHandler.registerMultiblock(IIMultiblocks.CRUCIBLE);
            MultiblockHandler.registerMultiblock(IIMultiblocks.STEAMTURBINE);
        }
    }

    public static class IITileTypes {
        public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(
                ForgeRegistries.TILE_ENTITIES, IIMain.MODID);

        public static final RegistryObject<TileEntityType<CrucibleTileEntity>> CRUCIBLE = REGISTER.register(
                "crucible", makeType(() -> new CrucibleTileEntity(), () -> IIMultiblocks.crucible)
        );
        public static final RegistryObject<TileEntityType<SteamTurbineTileEntity>> STEAMTURBINE = REGISTER.register(
                "steam_turbine", makeType(() -> new SteamTurbineTileEntity(), () -> IIMultiblocks.steam_turbine)
        );
        public static final RegistryObject<TileEntityType<ElectrolyzerTileEntity>> ELECTROLYZER = REGISTER.register(
                "electrolyzer", makeType(() -> new ElectrolyzerTileEntity(), () -> IIBlocks.electrolyzer)
        );

        private static <T extends TileEntity> Supplier<TileEntityType<T>> makeType(Supplier<T> create, Supplier<Block> valid) {
            return makeTypeMultipleBlocks(create, () -> ImmutableSet.of(valid.get()));
        }

        private static <T extends TileEntity> Supplier<TileEntityType<T>> makeTypeMultipleBlocks(Supplier<T> create, Supplier<Collection<Block>> valid) {
            return () -> new TileEntityType<>(create, ImmutableSet.copyOf(valid.get()), null);
        }

    }

    public static class IIRecipes {
        public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
                ForgeRegistries.RECIPE_SERIALIZERS, IIMain.MODID
        );

        static {
            CrucibleRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipeSerializer::new);
            ElectrolyzerRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("electrolyzer", ElectrolyzerRecipeSerializer::new);
        }

        public static void registerRecipeTypes() {
            CrucibleRecipe.TYPE = IRecipeType.register(IIMain.MODID + ":crucible");
            ElectrolyzerRecipe.TYPE = IRecipeType.register(IIMain.MODID + ":electrolyzer");
        }
    }

    public static void registerContainers() {
        GuiHandler.register(CrucibleTileEntity.class, new ResourceLocation(IIMain.MODID, "crucible"), CrucibleContainer::new);
        GuiHandler.register(ElectrolyzerTileEntity.class, new ResourceLocation(IIMain.MODID, "electrolyzer"), ElectrolyzerContainer::new);
    }

    public static class IIProps {
        public static void init() {
        }

        public static final AbstractBlock.Properties METALProps = AbstractBlock.Properties
                .create(Material.IRON)
                .sound(SoundType.METAL)
                .setRequiresTool()
                .harvestTool(ToolType.PICKAXE)
                .hardnessAndResistance(2, 10);
    }
}
