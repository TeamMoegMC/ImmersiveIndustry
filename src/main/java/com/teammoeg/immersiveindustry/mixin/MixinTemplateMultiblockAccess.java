package com.teammoeg.immersiveindustry.mixin;

import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TemplateMultiblock.class)
public interface MixinTemplateMultiblockAccess {
    
    @Invoker(remap = false)
    void callForm(Level world, BlockPos pos, Rotation rot, Mirror mirror, Direction sideHit);
}
