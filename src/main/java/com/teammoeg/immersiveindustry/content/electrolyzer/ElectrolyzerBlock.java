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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

public class ElectrolyzerBlock extends IIBaseBlock implements ILiquidContainer {
    public ElectrolyzerBlock(String name, Properties blockProps, BiFunction<Block, Item.Properties, Item> createItemBlock) {
        super(name, blockProps, createItemBlock);
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
        return new ElectrolyzerTileEntity();
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
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean canContainFluid(IBlockReader w, BlockPos p, BlockState s, Fluid f) {
        TileEntity te = w.getTileEntity(p);
        if (te instanceof ElectrolyzerTileEntity) {
            ElectrolyzerTileEntity ele = (ElectrolyzerTileEntity) te;
            if (ele.tank.fill(new FluidStack(f, 1000), IFluidHandler.FluidAction.SIMULATE) == 1000)
                return true;
        }
        return false;
    }

    @Override
    public boolean receiveFluid(IWorld w, BlockPos p, BlockState s,
                                FluidState f) {
        TileEntity te = w.getTileEntity(p);
        if (te instanceof ElectrolyzerTileEntity) {
            ElectrolyzerTileEntity ele = (ElectrolyzerTileEntity) te;
            if (ele.tank.fill(new FluidStack(f.getFluid(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000) {
                ele.tank.fill(new FluidStack(f.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }
}
