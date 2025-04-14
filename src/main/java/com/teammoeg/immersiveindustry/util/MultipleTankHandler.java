package com.teammoeg.immersiveindustry.util;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MultipleTankHandler implements IFluidHandler {
	final int numtanks;
	final FluidTank[] tanks;
	public MultipleTankHandler(int numtanks,int capacity) {
		this.numtanks=numtanks;
		tanks=new FluidTank[numtanks];
		for(int i=0;i<tanks.length;i++)
			tanks[i]=new FluidTank(capacity);
	}
	public MultipleTankHandler(FluidTank[] tanks) {
		this.numtanks=tanks.length;
		this.tanks=tanks;
	}

	@Override
	public int getTanks() {
		return numtanks;
	}
	public FluidTank getTank(int tank) {
		return tanks[tank];
	}
	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		if(tank>=numtanks)return FluidStack.EMPTY;
		return tanks[tank].getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		if(tank>=numtanks)
		return 0;
		return tanks[tank].getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		if(tank>=numtanks)return false;
		return tanks[tank].isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		for(int i=0;i<tanks.length;i++) {
			if(tanks[i].isFluidValid(resource)&&resource.isFluidEqual(tanks[i].getFluid())) {
				return tanks[i].fill(resource, action);
			}
		}
		for(int i=0;i<tanks.length;i++) {
			int filled=tanks[i].fill(resource, action);
			if(filled>0)
				return filled;
		}
		return 0;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		for(int i=0;i<tanks.length;i++) {
			FluidStack draineded=tanks[i].drain(resource, action);
			if(!draineded.isEmpty())
				return draineded;
		}
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		for(int i=0;i<tanks.length;i++) {
			FluidStack draineded=tanks[i].drain(maxDrain, action);
			if(!draineded.isEmpty())
				return draineded;
		}
		return FluidStack.EMPTY;
	}

}
