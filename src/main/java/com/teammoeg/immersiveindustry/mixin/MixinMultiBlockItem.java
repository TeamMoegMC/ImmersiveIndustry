package com.teammoeg.immersiveindustry.mixin;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiblockItem.class)
public class MixinMultiBlockItem extends BlockItem {
    
    public MixinMultiBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }
    
    @Inject(method = "place",at = @At("HEAD"),cancellable = true)
    public void onPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir){
        if(this.getBlock() instanceof MixinMultiblockPartBlockAccess block){
            var level = context.getLevel();
            var multiblock = block.getMultiblock();
            var structure = multiblock.getStructure().apply(level);
            for(var aBlock : structure){
                var pos = aBlock.pos().offset(context.getClickedPos());
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
        cir.setReturnValue(InteractionResult.SUCCESS);
        cir.cancel();
    }
}
