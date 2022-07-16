package com.teammoeg.immersiveindustry.content.misc;

import com.teammoeg.immersiveindustry.content.IIBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import java.util.function.BiFunction;

public class IIHorizontalBlock extends IIBaseBlock {

	public IIHorizontalBlock(String name, Properties blockProps,
							 BiFunction<Block, Item.Properties, Item> createItemBlock) {
		super(name, blockProps, createItemBlock);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.get(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(BlockStateProperties.HORIZONTAL_FACING, mirrorIn.mirror(state.get(BlockStateProperties.HORIZONTAL_FACING)));
	}
}
