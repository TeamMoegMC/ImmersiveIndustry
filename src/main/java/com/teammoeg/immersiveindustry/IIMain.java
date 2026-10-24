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

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod(IIMain.MODID)
public class IIMain {
    public static final String MODID = "immersiveindustry";
    public static final String MODNAME = "Immersive Industry";
    public static boolean loadfailed=false;
    public IIMain(IEventBus mod, ModContainer container) {
        mod.addListener(this::setup);
//        MinecraftForge.EVENT_BUS.addGenericListener(Block.class,this::onMissing);
        IICreativeTab.register(mod);
        IIConfig.register(container);
        IIContent.IIBlocks.BLOCKS.register(mod);
        IIContent.IItems.ITEMS.register(mod);;
       
        IIContent.registerContainers();
        IIContent.IITileTypes.REGISTER.register(mod);
        IIContent.IIRecipes.RECIPE_SERIALIZERS.register(mod);
        IIContent.IIRecipes.RECIPE_TYPES.register(mod);
        IIContent.IIMenus.MENU_TYPES.register(mod);
        // NeoForge removed DistExecutor; the client dist is checked directly instead.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientProxy.setup();
        }
        
//        DeferredWorkQueue.runLater(IIContent.IIRecipes::registerRecipeTypes);
    }
    public static void ieInit() {
    	 IIContent.IIMultiblocks.init();
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
        //MinecraftForge.EVENT_BUS.register(new IIRecipeReloadListener());
    }
    
}
