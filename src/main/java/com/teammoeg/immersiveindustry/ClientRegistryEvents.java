/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry;

import blusunrize.immersiveengineering.common.gui.GuiHandler;
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
        RenderTypeLookup.setRenderLayer(IIContent.IIMultiblocks.steam_turbine, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(IIContent.IIBlocks.electrolyzer, RenderType.getCutoutMipped());

    }

    public static <C extends Container, S extends Screen & IHasContainer<C>> void
    registerIEScreen(ResourceLocation containerName, ScreenManager.IScreenFactory<C, S> factory) {
        ContainerType<C> type = (ContainerType<C>) GuiHandler.getContainerType(containerName);
        ScreenManager.registerFactory(type, factory);
    }


}
