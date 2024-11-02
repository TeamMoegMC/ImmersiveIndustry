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

import com.teammoeg.immersiveindustry.content.IICreativeTab;
import com.teammoeg.immersiveindustry.data.IIRecipeReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(IIMain.MODID)
public class IIMain {
    public static final String MODID = "immersiveindustry";
    public static final String MODNAME = "Immersive Industry";
    public static boolean loadfailed=false;
    public IIMain() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus();
        mod.addListener(this::setup);
//        MinecraftForge.EVENT_BUS.addGenericListener(Block.class,this::onMissing);
        IICreativeTab.register(mod);
        IIConfig.register();
        IIContent.IIProps.init();
        IIContent.IIBlocks.init();
        IIContent.IItems.init();
        IIContent.IIMultiblocks.init();
        IIContent.registerContainers();
        IIContent.IITileTypes.REGISTER.register(mod);
        IIContent.IIRecipes.RECIPE_SERIALIZERS.register(mod);
        DistExecutor.safeRunWhenOn(Dist.CLIENT,()->ClientProxy::setup);
        IIContent.IIRecipes.registerRecipeTypes();
//        DeferredWorkQueue.runLater(IIContent.IIRecipes::registerRecipeTypes);
    }
//    public void onMissing(final MissingMappings<Block> ev) {
//    	ev.getAllMappings().forEach(e->{
//    		ResourceLocation rl=e.key;
//    		if(rl.getNamespace().equals(MODID)&&rl.getPath().equals("crucible")) {
//    			loadfailed=true;
//    			throw new RuntimeException("Mod Initialize failed, Please restart.");
//    		}
//    	});
//    }
    public void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new IIRecipeReloadListener(null));
    }
    
}
