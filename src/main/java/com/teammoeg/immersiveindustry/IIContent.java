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

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import blusunrize.immersiveengineering.common.register.IEBlocks;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.frostedheart.bootstrap.common.FHBlockEntityTypes;
import com.teammoeg.frostedheart.bootstrap.common.FHBlocks;
import com.teammoeg.frostedheart.bootstrap.common.FHItems;
import com.teammoeg.immersiveindustry.content.IIBaseBlock;
import com.teammoeg.immersiveindustry.content.IIBaseItem;
import com.teammoeg.immersiveindustry.content.IIBlockItem;
import com.teammoeg.immersiveindustry.content.carkiln.*;
import com.teammoeg.immersiveindustry.content.crucible.*;
import com.teammoeg.immersiveindustry.content.electrolyzer.*;
import com.teammoeg.immersiveindustry.content.misc.IIDirectionalBlock;
import com.teammoeg.immersiveindustry.content.misc.IIHorizontalBlock;
import com.teammoeg.immersiveindustry.content.rotarykiln.*;
import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineBlock;
import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineMultiblock;
import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineBlockEntity;
import mezz.jei.library.load.registration.GuiHandlerRegistration;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class IIContent {

    public static class IIBlocks {
        public static void init() {
        }
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, IIMain.MODID);
        
        protected static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, String itemName, Function<T, Item> item) {
            RegistryObject<T> blk = BLOCKS.register(name, block);
            IItems.ITEMS.register(itemName, () -> item.apply(blk.get()));
            return blk;
        }

        protected static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
            return register(name, block, name, IIBlockItem::new);
        }

        
        protected static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Function<T, Item> item) {
            return register(name, block, name, item);
        }
        

        public static RegistryObject<ElectrolyzerBlock> electrolyzer = register("electrolyzer", ()->new ElectrolyzerBlock(IIProps.MACHINEProps));
        public static RegistryObject<IIBaseBlock> burning_chamber = register("burning_chamber", ()->new IIBaseBlock(IIProps.METALProps));
        public static RegistryObject<IIHorizontalBlock> car_kiln_brick = register("car_kiln_brick", ()->new IIHorizontalBlock(IIProps.MACHINEProps));
        public static RegistryObject<IIDirectionalBlock> rotary_kiln_cylinder = register("rotary_kiln_cylinder", ()->new IIDirectionalBlock(IIProps.MACHINEProps));
    }

    public static class IItems {
        public static void init() {
        }
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IIMain.MODID);
        static Item.Properties createProps() {
            return new Item.Properties();
        }
        public static RegistryObject<IIBaseItem> refractory_kiln_brick = ITEMS.register("refractory_kiln_brick",()->new IIBaseItem(createProps()));
    }

    public static class IIMultiblocks {
    	public static final MultiblockRegistration<?> CRUCIBLE = null;
        public static final MultiblockRegistration<?> STEAMTURBINE = null;
        public static final MultiblockRegistration<?> IND_ELE = null;
        public static final MultiblockRegistration<?> ROTARY_KILN = null;
        public static final MultiblockRegistration<?> CAR_KILN = null;
    	
		private static <S extends IMultiblockState> IEMultiblockBuilder<S> stone(IMultiblockLogic<S> logic, String name, boolean solid) {
			Properties properties = Properties.of()
				.mapColor(MapColor.STONE)
				.instrument(NoteBlockInstrument.BASEDRUM)
				.strength(2, 20);
			if (!solid)
				properties.noOcclusion();
			return new IEMultiblockBuilder<>(logic, name)
				.notMirrored()
				.customBlock(
					IIBlocks.BLOCKS, IItems.ITEMS,
					r -> new NonMirrorableWithActiveBlock<>(properties, r),
					MultiblockItem::new)
				.defaultBEs(IITileTypes.REGISTER);
		}

		private static <S extends IMultiblockState> IEMultiblockBuilder<S> metal(IMultiblockLogic<S> logic, String name) {
			return new IEMultiblockBuilder<>(logic, name)
				.defaultBEs(IITileTypes.REGISTER)
				.customBlock(
					IIBlocks.BLOCKS, IItems.ITEMS,
					r -> new NonMirrorableWithActiveBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get(), r),
					MultiblockItem::new);
		}

        public static void init() {
        	Multiblock.init();
        }
        public static class Multiblock{
            public static final IETemplateMultiblock CRUCIBLE = new CrucibleMultiblock();
            public static final IETemplateMultiblock STEAMTURBINE = new SteamTurbineMultiblock();
            public static final IETemplateMultiblock IND_ELE = new IndustrialElectrolyzerMultiblock();
            public static final IETemplateMultiblock ROTARY_KILN = new RotaryKilnMultiblock();
            public static final IETemplateMultiblock CAR_KILN = new CarKilnMultiblock();
            public static void init() {
            	MultiblockHandler.registerMultiblock(CRUCIBLE);
                MultiblockHandler.registerMultiblock(STEAMTURBINE);
                MultiblockHandler.registerMultiblock(IND_ELE);
                MultiblockHandler.registerMultiblock(ROTARY_KILN);
                MultiblockHandler.registerMultiblock(CAR_KILN);
            }
        }
    }
    

    public static class IITileTypes {
        public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(
                ForgeRegistries.BLOCK_ENTITY_TYPES, IIMain.MODID);

        public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = REGISTER.register(
                "crucible", makeType(() -> new CrucibleBlockEntity(), () -> IIMultiblocks.crucible)
        );
        public static final RegistryObject<BlockEntityType<SteamTurbineBlockEntity>> STEAMTURBINE = REGISTER.register(
                "steam_turbine", makeType(() -> new SteamTurbineBlockEntity(), () -> IIMultiblocks.steam_turbine)
        );
        public static final RegistryObject<BlockEntityType<ElectrolyzerBlockEntity>> ELECTROLYZER = REGISTER.register(
                "electrolyzer", makeType(() -> new ElectrolyzerBlockEntity(), () -> IIBlocks.electrolyzer)
        );
        public static final RegistryObject<BlockEntityType<IndustrialElectrolyzerBlockEntity>> IND_ELE = REGISTER.register(
                "industrial_electrolyzer", makeType(() -> new IndustrialElectrolyzerBlockEntity(), () -> IIMultiblocks.industrial_electrolyzer)
        );
        public static final RegistryObject<BlockEntityType<RotaryKilnBlockEntity>> ROTARY_KILN= REGISTER.register(
                "rotary_kiln", makeType(() -> new RotaryKilnBlockEntity(), () -> IIMultiblocks.rotary_kiln)
        );
        public static final RegistryObject<BlockEntityType<CarKilnBlockEntity>> CAR_KILN= REGISTER.register(
                "car_kiln", makeType(() -> new CarKilnBlockEntity(), () -> IIMultiblocks.car_kiln)
        );
        private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeType(Supplier<T> create, Supplier<Block> valid) {
            return makeTypeMultipleBlocks(create, () -> ImmutableSet.of(valid.get()));
        }

        private static <T extends BlockEntity> Supplier<BlockEntityType<T>> makeTypeMultipleBlocks(Supplier<T> create, Supplier<Collection<Block>> valid) {
            return () -> new BlockEntityType<>(create, ImmutableSet.copyOf(valid.get()), null);
        }

    }

    public static class IIRecipes {
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
                ForgeRegistries.RECIPE_SERIALIZERS, IIMain.MODID
        );
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, IIMain.MODID
        	);
        static {
            CrucibleRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipeSerializer::new);
            ElectrolyzerRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("electrolyzer", ElectrolyzerRecipeSerializer::new);
            RotaryKilnRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("rotary_kiln", RotaryKilnRecipeSerializer::new);
            CarKilnRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("car_kiln",CarKilnRecipeSerializer::new);
            CrucibleRecipe.TYPE = register("crucible");
            ElectrolyzerRecipe.TYPE = register("electrolyzer");
            RotaryKilnRecipe.TYPE = register("rotary_kiln");
            CarKilnRecipe.TYPE = register("car_kiln");
        }
        public static void init() {}
    
        public static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name) {
        	return RECIPE_TYPES.register(name, ()->RecipeType.simple(new ResourceLocation(IIMain.MODID,name)));
        }
    }

    public static void registerContainers() {
        GuiHandler.register(CrucibleBlockEntity.class, new ResourceLocation(IIMain.MODID, "crucible"), CrucibleContainer::new);
        GuiHandler.register(ElectrolyzerBlockEntity.class, new ResourceLocation(IIMain.MODID, "electrolyzer"), ElectrolyzerContainer::new);
        GuiHandler.register(IndustrialElectrolyzerBlockEntity.class, new ResourceLocation(IIMain.MODID, "industrial_electrolyzer"), IndustrialElectrolyzerContainer::new);
        GuiHandler.register(CarKilnBlockEntity.class, new ResourceLocation(IIMain.MODID, "car_kiln"), CarKilnContainer::new);
        GuiHandler.register(RotaryKilnBlockEntity.class, new ResourceLocation(IIMain.MODID, "rotary_kiln"), RotaryKilnContainer::new);
    }

    public static class IIProps {
        public static void init() {
        }

        public static final Block.Properties METALProps = Block.Properties
                .of()
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .strength(2,10);
        public static final Block.Properties MACHINEProps = Block.Properties
                .of()
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .strength(3, 15)
                .noOcclusion();
    }
}
