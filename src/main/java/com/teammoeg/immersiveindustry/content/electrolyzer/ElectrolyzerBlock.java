package com.teammoeg.immersiveindustry.content.electrolyzer;

import com.teammoeg.immersiveindustry.content.IIBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class ElectrolyzerBlock extends IIBaseBlock implements ILiquidContainer {
    public ElectrolyzerBlock(String name, Properties blockProps, BiFunction<Block, Item.Properties, Item> createItemBlock) {
        super(name, blockProps, createItemBlock);
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
