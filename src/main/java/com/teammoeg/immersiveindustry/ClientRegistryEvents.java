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

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.teammoeg.immersiveindustry.IIContent.IIMenus;
import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.content.carkiln.CarKilnRenderer;
import com.teammoeg.immersiveindustry.content.carkiln.CarKilnScreen;
import com.teammoeg.immersiveindustry.content.chemical_reactor.ChemicalScreen;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleScreen;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerScreen;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerRenderer;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerScreen;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnRenderer;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnScreen;
import com.teammoeg.immersiveindustry.util.DynamicBlockModelReference;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualEntry.SpecialElementData;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = IIMain.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEvents {
    private static Tree.InnerNode<ResourceLocation, ManualEntry> CATEGORY;

    @SuppressWarnings("unused")
        /**
     * Screens are registered through the mod bus in NeoForge; vanilla's
     * {@code MenuScreens.register} is no longer callable from mod code.
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(IIMenus.ELECTROLYZER.get(), ElectrolyzerScreen::new);
        event.register(IIMenus.ROTARY_KILN.getType(), RotaryKilnScreen::new);
        event.register(IIMenus.INDUSTRIAL_ELECTROLYZER.getType(), IndustrialElectrolyzerScreen::new);
        event.register(IIMenus.CAR_KILN.getType(), CarKilnScreen::new);
        event.register(IIMenus.CRUCIBLE.getType(), CrucibleScreen::new);
        event.register(IIMenus.CHEMICAL.getType(), ChemicalScreen::new);
    }

@SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        /*registerIEScreen(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "crucible"), CrucibleScreen::new);
        registerIEScreen(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "electrolyzer"), ElectrolyzerScreen::new);
        registerIEScreen(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "industrial_electrolyzer"), IndustrialElectrolyzerScreen::new);
        registerIEScreen(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "car_kiln"), CarKilnScreen::new);
        registerIEScreen(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "rotary_kiln"), RotaryKilnScreen::new);
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.crucible, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.steam_turbine, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IIContent.IIBlocks.electrolyzer, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.industrial_electrolyzer, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.rotary_kiln, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.car_kiln, RenderType.getCutout());*/
       /* ClientRegistry.bindTileEntityRenderer(IITileTypes.IND_ELE.get(), IndustrialElectrolyzerRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IITileTypes.ROTARY_KILN.get(), RotaryKilnRenderer::new);
        ClientRegistry.bindTileEntityRenderer(IITileTypes.CAR_KILN.get(), CarKilnRenderer::new);*/
        addManual();
    }
	@SubscribeEvent
	public static void registerBERenders(RegisterRenderers event){
        event.registerBlockEntityRenderer(IIMultiblocks.INDUSTRIAL_ELECTROLYZER.masterBE().get(), IndustrialElectrolyzerRenderer::new);
        event.registerBlockEntityRenderer(IIMultiblocks.ROTARY_KILN.masterBE().get(), RotaryKilnRenderer::new);
        event.registerBlockEntityRenderer(IIMultiblocks.CAR_KILN.masterBE().get(), CarKilnRenderer::new);
	}
	@SubscribeEvent
	public static void registerModels(ModelEvent.RegisterAdditional ev)
	{
		DynamicBlockModelReference.registeredModels.forEach(rl->{
			ev.register(net.minecraft.client.resources.model.ModelResourceLocation.standalone(rl));
		});
	}

    public static void addManual() {
        ManualInstance man = ManualHelper.getManual();
        CATEGORY = man.getRoot().getOrCreateSubnode(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "main"), 100);
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement(new SpecialElementData("crucible", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.Multiblock.CRUCIBLE.get())));
            builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "crucible"));
            man.addEntry(CATEGORY, builder.create(), 0);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "electrolyzer"));
            man.addEntry(CATEGORY, builder.create(), 1);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement(new SpecialElementData("industrial_electrolyzer", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.Multiblock.INDUSTRIAL_ELECTROLYZER.get())));
            builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "industrial_electrolyzer"));
            man.addEntry(CATEGORY, builder.create(), 2);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement(new SpecialElementData("car_kiln", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.Multiblock.CAR_KILN.get())));
            builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "car_kiln"));
            man.addEntry(CATEGORY, builder.create(), 3);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement(new SpecialElementData("rotary_kiln", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.Multiblock.ROTARY_KILN.get())));
            builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "rotary_kiln"));
            man.addEntry(CATEGORY, builder.create(), 4);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement(new SpecialElementData("steam_turbine", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.Multiblock.STEAMTURBINE.get())));
            builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "steam_turbine"));
            man.addEntry(CATEGORY, builder.create(), 5);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement(new SpecialElementData("chemical_reactor", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.Multiblock.CHEMICAL.get())));
            builder.readFromFile(ResourceLocation.fromNamespaceAndPath(IIMain.MODID, "chemical_reactor"));
            man.addEntry(CATEGORY, builder.create(), 6);
        }
        extras.put("steamTurbineGenerator", () -> IIConfig.SERVER.steamTurbineGenerator.get());
        extras.put("SteamTurbineMaximumInput", () -> IIConfig.SERVER.steamTurbineInputMax.get());
        extras.put("SteamTurbineMinimumInput", () -> IIConfig.SERVER.steamTurbineInputMin.get());
        
        extras.put("electrodeCost", () -> IIConfig.SERVER.electrodeCost.get());
        ManualHelper.ADD_CONFIG_GETTER.get().accept((s) -> {
            if (s.startsWith(IIMain.MODID)) {
                String path = s.substring(s.indexOf(".") + 1);
                if (extras.containsKey(path))
                    return extras.get(path).get();
            }
			return null;
		});
    }

	public final static Map<String,Supplier<Object>> extras=new HashMap<>();

}
