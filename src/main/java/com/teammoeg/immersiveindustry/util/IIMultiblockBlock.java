package com.teammoeg.immersiveindustry.util;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class IIMultiblockBlock<S extends IMultiblockState> extends NonMirrorableWithActiveBlock<S> {


	public IIMultiblockBlock(Properties properties, MultiblockRegistration<S> multiblock) {
		super(properties, multiblock);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return 0.8f;
	}

}
