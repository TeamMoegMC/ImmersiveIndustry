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

package com.teammoeg.immersiveindustry.content.electrolyzer;

import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.teammoeg.immersiveindustry.content.IIBaseBlock;

import net.minecraftforge.fluids.FluidUtil;

public class ElectrolyzerBlock extends IIBaseBlock{
    public ElectrolyzerBlock(Properties blockProps) {
        super(blockProps);
    }
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape ELECShape=Block.makeCuboidShape(0,0,0,16,15,16);//1 px lower may avoid render glitch?
    @Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return ELECShape;
	}

	@Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ElectrolyzerBlockEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    	if (FluidUtil.interactWithFluidHandler(player, handIn,world, pos,hit.getFace()))
			return ActionResultType.SUCCESS;
    	if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
        }
        return ActionResultType.SUCCESS;
    }

}
