package com.teammoeg.immersiveindustry.content.rotarykiln;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

public class Process {
    ItemStack result;
    FluidStack fresult;
    int process;
    int processMax;

    public Process(ItemStack is, FluidStack fs, int time) {
        result = is;
        fresult = fs;
        process = processMax = time;
    }

    public Process(CompoundNBT nc) {
        result = ItemStack.read(nc.getCompound("result"));
        fresult = FluidStack.loadFluidStackFromNBT(nc.getCompound("fluid"));
        process = nc.getInt("process");
        processMax = nc.getInt("processMax");
    }
	public CompoundNBT serialize() {
		CompoundNBT cn=new CompoundNBT();
		cn.put("result",result.serializeNBT());
		cn.put("fluid",fresult.writeToNBT(new CompoundNBT()));
		cn.putInt("process",process);
		cn.putInt("processMax",processMax);
		return cn;
	}
	public int tick(int lp) {
		if(process>lp&&process>0)
			process--;
		return process;
	}
	public boolean removable() {
		return result.isEmpty()&&fresult.isEmpty()&&process<=0;
	}
	public boolean finished() {
		return process<=0;
			
	}
}
