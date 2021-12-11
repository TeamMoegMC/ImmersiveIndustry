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

import com.teammoeg.immersiveindustry.IIContent.IITileTypes;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleScreen;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerScreen;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerRenderer;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerScreen;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel.ModelType;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IIMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEvents {
    private static Tree.InnerNode<ResourceLocation, ManualEntry> CATEGORY;
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
    	IndustrialElectrolyzerRenderer.ELECTRODES = DynamicModel.createSided(
				new ResourceLocation(IIMain.MODID, "block/multiblocks/industrial_electrode.obj.ie"),
				"industrial_electrode", ModelType.IE_OBJ
		);;
        registerIEScreen(new ResourceLocation(IIMain.MODID, "crucible"), CrucibleScreen::new);
        registerIEScreen(new ResourceLocation(IIMain.MODID, "electrolyzer"), ElectrolyzerScreen::new);
        registerIEScreen(new ResourceLocation(IIMain.MODID, "industrial_electrolyzer"), IndustrialElectrolyzerScreen::new);
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.crucible, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.steam_turbine, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(IIContent.IIBlocks.electrolyzer, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.industrial_electrolyzer, RenderType.getCutoutMipped());
        ClientRegistry.bindTileEntityRenderer(IITileTypes.IND_ELE.get(), IndustrialElectrolyzerRenderer::new);
        addManual();
    }

    public static <C extends Container, S extends Screen & IHasContainer<C>> void
    registerIEScreen(ResourceLocation containerName, ScreenManager.IScreenFactory<C, S> factory) {
        ContainerType<C> type = (ContainerType<C>) GuiHandler.getContainerType(containerName);
        ScreenManager.registerFactory(type, factory);
    }

    public static void addManual() {
        ManualInstance man = ManualHelper.getManual();
        CATEGORY = man.getRoot().getOrCreateSubnode(new ResourceLocation(IIMain.MODID, "main"), 100);
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement("crucible", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.CRUCIBLE));
            builder.readFromFile(new ResourceLocation(IIMain.MODID, "crucible"));
            man.addEntry(CATEGORY, builder.create(), 0);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement("steam_turbine", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.STEAMTURBINE));
            builder.readFromFile(new ResourceLocation(IIMain.MODID, "steam_turbine"));
            man.addEntry(CATEGORY, builder.create(), 1);
        }
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
            builder.addSpecialElement("industrial_electrolyzer", 0, () -> new ManualElementMultiblock(man, IIContent.IIMultiblocks.IND_ELE));
            builder.readFromFile(new ResourceLocation(IIMain.MODID, "industrial_electrolyzer"));
            man.addEntry(CATEGORY, builder.create(), 2);
        }
    }

}
