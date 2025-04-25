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

import com.teammoeg.immersiveindustry.mixin.MixinMultiblockPartBlockAccess;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IIMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
//
//    @SubscribeEvent
//    public static void addReloadListeners(AddReloadListenerEvent event) {
//        DataPackRegistries dataPackRegistries = event.getDataPackRegistries();
//        event.addListener(new IIRecipeReloadListener(dataPackRegistries));
//    }
//
//    @SubscribeEvent
//    public static void addReloadListenersLowest(AddReloadListenerEvent event) {
//        DataPackRegistries dataPackRegistries = event.getDataPackRegistries();
//        event.addListener(new IIRecipeCachingReloadListener(dataPackRegistries));
//    }
//
//    @SubscribeEvent
//    @SuppressWarnings("deprecation")
//    public static void onTick(TickEvent.ServerTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) {
//            var list = BuiltInRegistries.ITEM.stream().filter(i -> i instanceof MultiblockItem).toList();
//            System.out.println(list);
//        }
//    }
    
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickBlock event) {
        var item = event.getItemStack();
        if(!(item.getItem() instanceof BlockItem blockItem)) return;
        if(blockItem.getBlock() instanceof MixinMultiblockPartBlockAccess block){
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            var level = event.getLevel();
            if(level.isClientSide()) return;
            var hitResult = event.getHitVec();
            var multiblock = block.getMultiblock();
            var structure = multiblock.getStructure().apply(level);
            for(var aBlock : structure){
                var pos = aBlock.pos().offset(hitResult.getBlockPos());
                if (aBlock.nbt() != null) {
                    BlockEntity blockentity = level.getBlockEntity(pos);
                    Clearable.tryClear(blockentity);
                    level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 20);
                }
                if (level.setBlock(pos,aBlock.state(), Block.UPDATE_ALL)) {
                    if (aBlock.nbt() != null) {
                        BlockEntity be = level.getBlockEntity(pos);
                        if (be != null) {
                            be.load(aBlock.nbt());
                            be.setChanged();
                        }
                    }
                }
            }
        }
    }
}
