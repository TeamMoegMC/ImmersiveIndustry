package com.teammoeg.immersiveindustry.content.rotarykiln;

import net.minecraft.core.HolderLookup;
import java.util.function.BooleanSupplier;

import com.teammoeg.immersiveindustry.util.CapabilityProcessor;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.RangedCheckedInputWrapper;
import com.teammoeg.immersiveindustry.util.RangedOutputWrapper;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.teammoeg.immersiveindustry.util.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

public class RotaryKilnState implements IMultiblockState{
	
	//common properties
	boolean active;
	public ChangeDetectedItemHandler inventory;
	public MutableEnergyStorage energyStorage = new MutableEnergyStorage(16000);
	public FluidTank tankout = new FluidTank(32000);
	public RotaryKilnProcess[] processes=new RotaryKilnProcess[2];

	
	//client properties
	public int angle;// angle for animation in degrees
	BooleanSupplier isSoundPlaying = () -> false;
	
	
	//transient capability and interfaces
	CapabilityProcessor capabilities=new CapabilityProcessor();
	CapabilityReference<IItemHandler> outInvCap;
	CapabilityReference<IFluidHandler> outFluidCap;
	public final RSState state=RSState.enabledByDefault();
	public RotaryKilnState(IInitialMultiblockContext<RotaryKilnState> capabilitySource) {
		inventory=new ChangeDetectedItemHandler(5,capabilitySource.getMarkDirtyRunnable());
		capabilities.itemHandler()
			.addCapability(RotaryKilnLogic.itemin, new RangedCheckedInputWrapper(inventory,0,1,(s,r)->RotaryKilnRecipe.isValidRecipeInput(capabilitySource.levelSupplier().get(), r)))
			.addCapability(RotaryKilnLogic.itemout, new RangedOutputWrapper(inventory,3,5));
		capabilities.fluidHandler()
			.addCapability(RotaryKilnLogic.fluidout, ArrayFluidHandler.drainOnly(tankout, capabilitySource.getMarkDirtyRunnable()));
		capabilities.energy()
			.addCapability(RotaryKilnLogic.powerin, new WrappingEnergyStorage(energyStorage,true,false,capabilitySource.getMarkDirtyRunnable()));
		
		outInvCap=RotaryKilnLogic.itemout.getFacingCapability(capabilitySource, Capabilities.ItemHandler.BLOCK);
		outFluidCap=RotaryKilnLogic.fluidout.getFacingCapability(capabilitySource, Capabilities.FluidHandler.BLOCK);
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.putBoolean("active", active);
		nbt.put("inv",inventory.serializeNBT(provider)); 
		nbt.putInt("energy", energyStorage.getEnergyStored());
		nbt.put("fluid", tankout.writeToNBT(provider, new CompoundTag()));
		if(processes[0]!=null)
			nbt.put("process0", processes[0].save());
		if(processes[1]!=null)
			nbt.put("process1", processes[1].save());
	}

	@Override
	public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		active = nbt.getBoolean("active");
		inventory.deserializeNBT(provider, nbt.getCompound("inv"));
		energyStorage.setStoredEnergy(nbt.getInt("energy"));
		tankout.readFromNBT(provider, nbt.getCompound("fluid"));
		if(nbt.contains("process0")) {
			CompoundTag tag=nbt.getCompound("process0");
			if(processes[0]==null)
				processes[0]=new RotaryKilnProcess(tag);
			else
				processes[0].load(tag);
		}else
			processes[0]=null;
		if(nbt.contains("process1")) {
			CompoundTag tag=nbt.getCompound("process1");
			if(processes[1]==null)
				processes[1]=new RotaryKilnProcess(tag);
			else
				processes[1].load(tag);
		}else
			processes[1]=null;
	}
	@Override
	public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.putBoolean("active", active);
	}

	@Override
	public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		active = nbt.getBoolean("active");
	}
}
