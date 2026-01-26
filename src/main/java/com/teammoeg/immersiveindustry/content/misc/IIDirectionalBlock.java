package com.teammoeg.immersiveindustry.content.misc;

import com.teammoeg.immersiveindustry.content.IDirectionPropertyBlock;
import com.teammoeg.immersiveindustry.content.IIBaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class IIDirectionalBlock extends IIBaseBlock implements IDirectionPropertyBlock {

	public IIDirectionalBlock(Properties blockProps) {
		super(blockProps);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing;
		Vec3 clickLocation = context.getClickLocation();
		BlockPos pos = context.getClickedPos();

		double relativeX = clickLocation.x - pos.getX();
		double relativeZ = clickLocation.z - pos.getZ();

		if (context.getClickedFace().getAxis() != Direction.Axis.Y) {
			facing = context.getClickedFace().getOpposite();
		}
		else {
			double xFromMid = relativeX - 0.5;
			double zFromMid = relativeZ - 0.5;

			double centerThreshold = 0.15;

			if (Math.abs(xFromMid) < centerThreshold && Math.abs(zFromMid) < centerThreshold) {

				Player player = context.getPlayer();
				if (player != null && player.isShiftKeyDown()) {
					facing = Direction.DOWN;
				} else {
					facing = Direction.UP;
				}
			}

			else {
				double max = Math.max(Math.abs(xFromMid), Math.abs(zFromMid));
				if (max == Math.abs(xFromMid)) {
					facing = xFromMid < 0 ? Direction.WEST : Direction.EAST;
				} else {
					facing = zFromMid < 0 ? Direction.NORTH : Direction.SOUTH;
				}
			}
		}
		return this.defaultBlockState().setValue(BlockStateProperties.FACING, facing);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BlockStateProperties.FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(BlockStateProperties.FACING, rot.rotate(state.getValue(BlockStateProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(BlockStateProperties.FACING, mirrorIn.mirror(state.getValue(BlockStateProperties.FACING)));
	}

	@Override
	public Property<Direction> getDirectionProperty() {
		return BlockStateProperties.FACING;
	}
}
