package com.teammoeg.immersiveindustry.mixin;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IETemplateMultiblock.class)
public interface MixinIETemplateMultiblockAccess {
    
    @Accessor(remap = false)
    MultiblockRegistration<?> getLogic();
}
