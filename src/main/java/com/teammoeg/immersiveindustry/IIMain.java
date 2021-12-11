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

import javax.annotation.Nonnull;

import com.teammoeg.immersiveindustry.data.IIRecipeReloadListener;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(IIMain.MODID)
public class IIMain {
    public static final String MODID = "immersiveindustry";
    public static final String MODNAME = "Immersive Industry";
    public static final ItemGroup itemGroup = new ItemGroup(MODID) {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(IIContent.IIBlocks.burning_chamber.asItem());
        }
    };

    public IIMain() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        mod.addListener(this::setup);
        DistExecutor.safeRunWhenOn(Dist.CLIENT,()->ClientProxy::setup);
        IIConfig.register();
        IIContent.IIProps.init();
        IIContent.IIBlocks.init();
        IIContent.IIMultiblocks.init();
        IIContent.registerContainers();
        IIContent.IITileTypes.REGISTER.register(mod);
        IIContent.IIRecipes.RECIPE_SERIALIZERS.register(mod);
        DeferredWorkQueue.runLater(IIContent.IIRecipes::registerRecipeTypes);
    }

    public void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new IIRecipeReloadListener(null));
    }

}
