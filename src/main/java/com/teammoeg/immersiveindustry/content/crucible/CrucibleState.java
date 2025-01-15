package com.teammoeg.immersiveindustry.content.crucible;

import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnState;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.RecipeHandler;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CrucibleState implements IMultiblockState {
    ChangeDetectedItemHandler inventory;
    RecipeHandler recipe;
    public int temperature;
    public int burnTime;
    public int process = 0;
    public int processMax = 0;
    public int updatetick = 0;
    public FluidStack resultFluid = FluidStack.EMPTY;
    public FluidTank[] tank = new FluidTank[]{new FluidTank(14400)};
	public CrucibleState(IInitialMultiblockContext<RotaryKilnState> capabilitySource) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readSaveNBT(CompoundTag nbt) {
		// TODO Auto-generated method stub
		
	}

}
