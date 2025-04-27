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
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.teammoeg.immersiveindustry.mixin.MixinIETemplateMultiblockAccess;
import com.teammoeg.immersiveindustry.mixin.MixinMultiblockPartBlockAccess;
import com.teammoeg.immersiveindustry.mixin.MixinTemplateMultiblockAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

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
    public static ResourceLocation multiblockCache = null;
    public static Rotation rotCache = null;
    public static AABB multiblockBound = null;
    
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
            var multiblockReg = block.getMultiblock();
            var multiblock = getITEMFromReg(multiblockReg);
            var pos = hitResult.getBlockPos();
            if(multiblock == null){
                //should not happen.
                formMultiblockOld(level, pos, multiblockReg);
            }
            else {
                var placePos = pos.above(multiblock.getMasterFromOriginOffset().getY());
                var rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, event.getEntity().getDirection());
                ((MixinTemplateMultiblockAccess) multiblock).callForm(level, placePos, rot, Mirror.NONE, event.getEntity().getDirection().getOpposite());
            }
       
        }
    }
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side == LogicalSide.SERVER) return;
        if(event.phase == TickEvent.Phase.END) return;
        var itemInOffHand = event.player.getOffhandItem();
        var itemInMainHand = event.player.getMainHandItem();
        if(!updateMultiblockInHand(event.player, itemInMainHand) && !updateMultiblockInHand(event.player, itemInOffHand)) {
            multiblockCache = null;
            rotCache = null;
            multiblockBound = null;
        }
    }
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean updateMultiblockInHand(Player player, ItemStack itemStack){
        if(itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof MixinMultiblockPartBlockAccess block) {
            var multiblock = getITEMFromReg(block.getMultiblock());
            var rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, player.getDirection());
            if(multiblock != null && (rot != rotCache || multiblock.getUniqueName().equals(multiblockCache))){
                multiblockCache = multiblock.getUniqueName();
                rotCache = rot;
                multiblockBound = getStructureOutBounds(player.level(),multiblock,rot,Mirror.NONE);
            }
            return true;
        }
        return false;
    }
    
    public static AABB getStructureOutBounds(Level level, IETemplateMultiblock multiblock, Rotation rot, Mirror mirror) {
        return new AABB(BlockPos.ZERO, TemplateMultiblock.getAbsoluteOffset(new BlockPos(multiblock.getSize(level)),mirror,rot));
    }
    
    public static void formMultiblockOld(Level level, BlockPos pos, MultiblockRegistration<?> multiblockReg) {
        var structure = multiblockReg.getStructure().apply(level);
        for(var aBlock : structure){
            var pos_ = aBlock.pos().offset(pos);
            if (aBlock.nbt() != null) {
                BlockEntity blockentity = level.getBlockEntity(pos_);
                Clearable.tryClear(blockentity);
                level.setBlock(pos_, Blocks.BARRIER.defaultBlockState(), 20);
            }
            if (level.setBlock(pos_,aBlock.state(), Block.UPDATE_ALL)) {
                if (aBlock.nbt() != null) {
                    BlockEntity be = level.getBlockEntity(pos_);
                    if (be != null) {
                        be.load(aBlock.nbt());
                        be.setChanged();
                    }
                }
            }
        }
    }
    
    public static @Nullable IETemplateMultiblock getITEMFromReg(MultiblockRegistration<?> multiblockReg) {
        for(var imb : MultiblockHandler.getMultiblocks()){
            if(imb instanceof MixinIETemplateMultiblockAccess ietm){
                if (ietm.getLogic().equals(multiblockReg)) {
                    return (IETemplateMultiblock) ietm;
                }
            }
        }
        return null;
    }
}
