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
		int maxFill=resource.getAmount();
		for(int i=0;i<tanks.length;i++) {
			int amount=tanks[i].fill(resource, action);
			resource.shrink(amount);
			if(resource.isEmpty())
				return amount;
		}
		return maxFill-resource.getAmount();
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		FluidStack out=FluidStack.EMPTY;
		for(int i=0;i<tanks.length;i++) {
			FluidStack drained=tanks[i].drain(resource, action);
			if(!drained.isEmpty()) {
				if(out.isEmpty())
					out=drained;
				else {
					out.setAmount(out.getAmount()+drained.getAmount());
				}
				if(out.getAmount()>=resource.getAmount())
					break;
			}
		}
		return out;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		FluidStack filter=FluidStack.EMPTY;
		FluidStack out=FluidStack.EMPTY;
		for(int i=0;i<tanks.length;i++) {
			
			if(filter.isEmpty()) {
				FluidStack drained=tanks[i].drain(maxDrain, action);
				if(!drained.isEmpty()) {
					out=drained;
					filter=new FluidStack(out,maxDrain-out.getAmount());
				}
			}else{
				FluidStack drained=tanks[i].drain(filter, action);
				if(!drained.isEmpty()) {
					out.setAmount(out.getAmount()+drained.getAmount());
					filter.shrink(drained.getAmount());
				}
			}
			
			if(out.getAmount()>=maxDrain)
				break;
		}
		return out;
	}

}
