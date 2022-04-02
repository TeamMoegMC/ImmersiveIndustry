package com.teammoeg.immersiveindustry.content.carklin;

import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.IIContent.IITileTypes;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class CarKilnTileEntity extends MultiblockPartTileEntity<CarKilnTileEntity>
/*implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
IEBlockInterfaces.IInteractionObjectIE*/{
	int pos;//animation process from 0-51, 0=idle 51=working
	int app=1;//test code, can delete
	public CarKilnTileEntity() {
		super(IIMultiblocks.CAR_KILN,IITileTypes.CAR_KILN.get(),false);
	}

	@Override
	protected boolean canDrainTankFrom(int arg0, Direction arg1) {
		return false;
	}

	@Override
	protected boolean canFillTankFrom(int arg0, Direction arg1, FluidStack arg2) {
		return false;
	}

	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction arg0) {
		return new IFluidTank[0];
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		BlockPos bp=this.getPos();
		return new AxisAlignedBB(bp.getX()-(getFacing().getAxis()==Axis.Z?1: 2),
				bp.getY(),
				bp.getZ()-(getFacing().getAxis()==Axis.X?1: 2),
				bp.getX()+(getFacing().getAxis()==Axis.Z?2: 1),
				bp.getY()+3,
				bp.getZ()+(getFacing().getAxis()==Axis.X?2: 1));
	}
	@Override
	public void tick() {
		pos+=app;
		if(pos>51)
			app=-1;
		if(pos<0)
			app=1;
	}

}
