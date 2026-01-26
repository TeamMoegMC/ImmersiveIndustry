package com.teammoeg.immersiveindustry.content.misc;

import com.teammoeg.immersiveindustry.content.IDirectionPropertyBlock;
import com.teammoeg.immersiveindustry.content.IIBaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class IIHorizontalBlock extends IIBaseBlock implements IDirectionPropertyBlock {

	public IIHorizontalBlock(Properties blockProps) {
		super(blockProps);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing;
		Vec3 clickLocation = context.getClickLocation();
		BlockPos pos = context.getClickedPos();
		clickLocation = clickLocation.subtract(pos.getX(), pos.getY(), pos.getZ());

		if(context.getClickedFace().getAxis()!= Direction.Axis.Y)
			facing = context.getClickedFace().getOpposite();
		else
		{
			double xFromMid = clickLocation.x - .5;
			double zFromMid = clickLocation.z - .5;
			double max = Math.max(Math.abs(xFromMid), Math.abs(zFromMid));
			if(max==Math.abs(xFromMid))
				facing = xFromMid < 0?Direction.WEST: Direction.EAST;
			else
				facing = zFromMid < 0?Direction.NORTH: Direction.SOUTH;
		}
		return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirrorIn.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override
	public Property<Direction> getDirectionProperty() {
		return BlockStateProperties.HORIZONTAL_FACING;
	}
	public Direction getDirectionForPlacement(Direction side, LivingEntity placer, Vec3 clickLocation)
	{
		if(side.getAxis()!= Direction.Axis.Y)
			return side.getOpposite();
		else
		{
			double xFromMid = clickLocation.x-.5;
			double zFromMid = clickLocation.z-.5;
			double max = Math.max(Math.abs(xFromMid), Math.abs(zFromMid));
			if(max==Math.abs(xFromMid))
				return xFromMid < 0?Direction.WEST: Direction.EAST;
			else
				return zFromMid < 0?Direction.NORTH: Direction.SOUTH;
		}
	}
}
