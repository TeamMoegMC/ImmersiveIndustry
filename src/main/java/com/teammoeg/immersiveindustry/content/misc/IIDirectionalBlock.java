package com.teammoeg.immersiveindustry.content.misc;

import com.teammoeg.immersiveindustry.content.IIBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;

import java.util.function.BiFunction;

public class IIDirectionalBlock extends IIBaseBlock {

	public IIDirectionalBlock(String name, Properties blockProps,
							  BiFunction<Block, net.minecraft.item.Item.Properties, Item> createItemBlock) {
		super(name, blockProps, createItemBlock);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(BlockStateProperties.FACING,context.getNearestLookingDirection());
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockStateProperties.FACING);
	}

}
