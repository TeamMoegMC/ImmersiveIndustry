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

import javax.annotation.Nullable;

import com.teammoeg.immersiveindustry.mixin.MixinIETemplateMultiblockAccess;
import com.teammoeg.immersiveindustry.mixin.MixinMultiblockPartBlockAccess;
import com.teammoeg.immersiveindustry.mixin.MixinTemplateMultiblockAccess;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = IIMain.MODID)
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
                // One shared anchor for both the hologram and the actual placement, so what the
                // player sees is exactly what gets built: the structure's base sits on top of
                // the clicked block, shifted so a rotated structure still occupies the space
                // the preview box was drawn around.
                var rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, event.getEntity().getDirection());
                ((MixinTemplateMultiblockAccess) multiblock).callForm(level,
                        getPlacementOrigin(pos, event.getEntity().getDirection()),
                        rot, Mirror.NONE, event.getEntity().getDirection().getOpposite());
            }
       
        }
    }
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if(!event.getEntity().level().isClientSide) return;
        var itemInOffHand = event.getEntity().getOffhandItem();
        var itemInMainHand = event.getEntity().getMainHandItem();
        if(!updateMultiblockInHand(event.getEntity(), itemInMainHand) && !updateMultiblockInHand(event.getEntity(), itemInOffHand)) {
            multiblockCache = null;
            rotCache = null;
            multiblockBound = null;
        }
    }
    
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
        return new AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(BlockPos.ZERO), net.minecraft.world.phys.Vec3.atLowerCornerOf(TemplateMultiblock.getAbsoluteOffset(new BlockPos(multiblock.getSize(level)),mirror,rot)));
    }

    /**
     * Where a directly placed multiblock should start.
     *
     * <p>Two things matter: the base sits on top of the clicked block (so the structure is not
     * buried in it), and a rotated structure grows towards the other side, so the origin has to
     * move as well. The client hologram uses this same method, which keeps the preview box and
     * the finished machine in exactly the same space.
     */
    public static BlockPos getPlacementOrigin(BlockPos clicked, Direction facing) {
        BlockPos pos = clicked.above();
        switch (facing) {
            case EAST -> pos = pos.east();
            case WEST -> pos = pos.south();
            case SOUTH -> pos = pos.east().south();
            default -> {
            }
        }
        return pos;
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
                        be.loadWithComponents(aBlock.nbt(), level.registryAccess());
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
