package com.teammoeg.immersiveindustry.mixin;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiblockPartBlock.class)
public interface MixinMultiblockPartBlockAccess {
    @Accessor(remap = false)
    MultiblockRegistration<?> getMultiblock();
}
