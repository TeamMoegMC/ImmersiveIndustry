package com.teammoeg.immersiveindustry.content.klin;

import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.IIContent.IITileTypes;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class RotaryKilnTileEntity extends MultiblockPartTileEntity<RotaryKilnTileEntity>
/*implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
IEBlockInterfaces.IInteractionObjectIE*/{
	int angle;
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
	public void tick() {
		angle+=10;
		if(angle>=360)
			angle=0;
	}

}
