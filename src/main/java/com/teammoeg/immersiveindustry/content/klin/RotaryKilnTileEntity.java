package com.teammoeg.immersiveindustry.content.klin;

import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.IIContent.IITileTypes;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class RotaryKilnTileEntity extends MultiblockPartTileEntity<RotaryKilnTileEntity>
/*implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
IEBlockInterfaces.IInteractionObjectIE*/{
	int angle;//angle for animation in degrees
	public RotaryKilnTileEntity() {
		super(IIMultiblocks.ROTARY_KILN,IITileTypes.ROTARY_KILN.get(),false);
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
		return new AxisAlignedBB(bp.getX()-(getFacing().getAxis()==Axis.Z?1: 3),
				bp.getY(),
				bp.getZ()-(getFacing().getAxis()==Axis.X?1: 3),
				bp.getX()+(getFacing().getAxis()==Axis.Z?3: 1),
				bp.getY()+2,
				bp.getZ()+(getFacing().getAxis()==Axis.X?3: 1));
	}
	@Override
	public void tick() {
		angle+=10;
		if(angle>=360)
			angle=0;
	}

}
