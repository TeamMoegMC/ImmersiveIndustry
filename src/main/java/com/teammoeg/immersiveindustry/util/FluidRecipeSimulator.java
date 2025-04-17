package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.List;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidRecipeSimulator {

	public FluidRecipeSimulator() {
		
	}
	public static List<List<FluidStack>> expand(FluidTagInput...ft){
		List<List<FluidStack>> li=new ArrayList<>(ft.length);
		for(FluidTagInput t:ft) {
			li.add(t.getMatchingFluidStacks());
		}
		return li;
	}
	public static FluidRecipeProcessResult test(IFluidHandler ft,FluidTagInput... ftis){
		List<FluidStack> li=new ArrayList<>(ftis.length);
		List<FluidStack> all=new ArrayList<>(ft.getTanks());
		for(int i=0;i<ft.getTanks();i++) {
			all.add(ft.getFluidInTank(i).copy());
		}
		for(FluidTagInput fti:ftis) {
			int cnt=fti.getAmount();
			for(FluidStack fs:all) {
				if(fti.test(fs)) {
					if(fs.getAmount()>=cnt) {
						li.add(new FluidStack(fs,cnt));
						fs.setAmount(fs.getAmount()-cnt);
						cnt=0;
					}else {
						li.add(fs.copy());
						cnt-=fs.getAmount();
						fs.setAmount(0);
						
					}
				}
				if(cnt<=0)break;
			}
			if(cnt>0)return null;
		}
		return new FluidRecipeProcessResult(li);
	}

}
