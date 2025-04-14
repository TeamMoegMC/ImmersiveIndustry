package com.teammoeg.immersiveindustry.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandlerModifiable;

public record FluidRecipeProcessResult(List<FluidStack> operations) {
	public void runOperations(IFluidHandler inventory) {
		if (operations != null) {
			for (FluidStack i : operations) {
				inventory.drain(i, FluidAction.EXECUTE);
			}
		}
	}
	public void runOperations(IFluidHandler inventory,int multiplier) {
		if (operations != null) {
			for (FluidStack i : operations) {
				inventory.drain(new FluidStack(i,i.getAmount()*multiplier), FluidAction.EXECUTE);
			}
		}
	}
	public boolean tryRunOperations(IFluidHandler inventory) {
		if (operations != null) {
			for (FluidStack i : operations) {
				FluidStack drained=inventory.drain(i, FluidAction.SIMULATE);
				if(i.getAmount()>drained.getAmount()) {
					return false;
				}
			}
			for (FluidStack i : operations) {
				inventory.drain(new FluidStack(i,i.getAmount()), FluidAction.EXECUTE);
			}
		}
		return true;
	}
	public int getMaxRuns(IFluidHandler inventory) {
		int maxcount=64;
		if (operations != null) {
			for (FluidStack i : operations) {
				int count=0;
				for(int j=0;j<inventory.getTanks();j++) {
					FluidStack fs=inventory.getFluidInTank(j);
					if(i.equals(fs)) {
						count+=fs.getAmount();
					}
				}
				maxcount=Math.min(maxcount, i.getAmount()/count);
			}
		}
		return maxcount;
	}

}
