package com.teammoeg.immersiveindustry.content.rotarykiln;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;

public class RotaryKilnProcess {
    ItemStack result;
    ItemStack sresult=ItemStack.EMPTY;
    FluidStack fresult;
    int process;
    int processMax;

    public RotaryKilnProcess(ItemStack is, FluidStack fs, int time,ItemStack br,float ch) {
        result = is;
        fresult = fs;
        process = processMax = time;
        if(ch>0&&!br.isEmpty()&&Math.random()<ch) {
        	sresult=br.copy();
        }
    }

    public RotaryKilnProcess(CompoundNBT nc) {
        result = ItemStack.read(nc.getCompound("result"));
        fresult = FluidStack.loadFluidStackFromNBT(nc.getCompound("fluid"));
        process = nc.getInt("process");
        processMax = nc.getInt("processMax");
        sresult=ItemStack.read(nc.getCompound("by"));
    }
	public CompoundNBT serialize() {
		CompoundNBT cn=new CompoundNBT();
		cn.put("result",result.serializeNBT());
		cn.put("fluid",fresult.writeToNBT(new CompoundNBT()));
		cn.putInt("process",process);
		cn.putInt("processMax",processMax);
		cn.put("by", sresult.serializeNBT());
		return cn;
	}
	public int tick(int lp) {
		if(process>lp&&process>0)
			process--;
		return process;
	}
	public boolean removable() {
		return result.isEmpty()&&fresult.isEmpty()&&process<=0&&sresult.isEmpty();
	}
	public boolean finished() {
		return process<=0;
			
	}
}
