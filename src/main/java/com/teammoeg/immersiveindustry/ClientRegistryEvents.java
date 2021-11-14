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

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.client.manual.ManualElementMultiblock;
import blusunrize.immersiveengineering.common.gui.GuiHandler;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import blusunrize.lib.manual.Tree;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleScreen;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerScreen;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = IIMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEvents {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        ;
        registerIEScreen(new ResourceLocation(IIMain.MODID, "crucible"), CrucibleScreen::new);
        registerIEScreen(new ResourceLocation(IIMain.MODID, "electrolyzer"), ElectrolyzerScreen::new);
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.crucible, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.steam_turbine, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(IIContent.IIBlocks.electrolyzer, RenderType.getCutoutMipped());
        // addManual();
    }

    public static <C extends Container, S extends Screen & IHasContainer<C>> void
    registerIEScreen(ResourceLocation containerName, ScreenManager.IScreenFactory<C, S> factory) {
        ContainerType<C> type = (ContainerType<C>) GuiHandler.getContainerType(containerName);
        ScreenManager.registerFactory(type, factory);
    }

    public static void addManual() {
        ManualInstance Man = ManualHelper.getManual();
        Tree.InnerNode<ResourceLocation, ManualEntry> iiCat = Man.getRoot().getOrCreateSubnode(new ResourceLocation(IIMain.MODID, "machine"), 100);
        {
            ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(Man);
            builder.addSpecialElement("multiblock", 0, new ManualElementMultiblock(Man, IIContent.IIMultiblocks.CRUCIBLE));
            builder.readFromFile(new ResourceLocation(IIMain.MODID, "crucible"));
            Man.addEntry(iiCat, builder.create(), 0);
        }
    }

}
