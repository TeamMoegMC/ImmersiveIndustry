package com.teammoeg.immersiveindustry.content.rotarykiln;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineState;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor;
import com.teammoeg.immersiveindustry.util.RecipeHandler;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraintGroup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class RotaryKilnState implements IMultiblockState{
	
	//common properties
	boolean active;
	public SlotwiseItemHandler inventory;
	public MutableEnergyStorage energyStorage = new MutableEnergyStorage(32000);
	public FluidTank tankout = new FluidTank(32000);
	public RotaryKilnProcess[] processes=new RotaryKilnProcess[2];

	
	//client properties
	public int angle;// angle for animation in degrees
	
	
	//transient capability and interfaces
	CapabilityProcessor capabilities=new CapabilityProcessor();
	CapabilityReference<IItemHandler> outInvCap;
	CapabilityReference<IFluidHandler> outFluidCap;
	public final RSState state=RSState.enabledByDefault();
	public RotaryKilnState(IInitialMultiblockContext<RotaryKilnState> capabilitySource) {
		inventory=
			SlotwiseItemHandler.makeWithGroups(capabilitySource.getMarkDirtyRunnable(),
			new IOConstraintGroup(IOConstraint.input(r->RotaryKilnRecipe.isValidRecipeInput(capabilitySource.levelSupplier().get(), r)),1),
			new IOConstraintGroup(IOConstraint.BLOCKED,2),
			new IOConstraintGroup(IOConstraint.OUTPUT,2)
			);
		capabilities.itemHandler()
			.addCapability(RotaryKilnLogic.itemin, new RangedWrapper(inventory,0,1))
			.addCapability(RotaryKilnLogic.itemout, new RangedWrapper(inventory,3,5));
		capabilities.fluidHandler()
			.addCapability(RotaryKilnLogic.fluidout, ArrayFluidHandler.drainOnly(tankout, capabilitySource.getMarkDirtyRunnable()));
		capabilities.energy()
			.addCapability(RotaryKilnLogic.powerin, new WrappingEnergyStorage(energyStorage,true,false,capabilitySource.getMarkDirtyRunnable()));
		
		outInvCap=RotaryKilnLogic.itemout.getFacingCapability(capabilitySource, ForgeCapabilities.ITEM_HANDLER);
		outFluidCap=RotaryKilnLogic.fluidout.getFacingCapability(capabilitySource, ForgeCapabilities.FLUID_HANDLER);
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt) {
		nbt.putBoolean("active", active);
		nbt.put("inv",inventory.serializeNBT());
		nbt.put("energy",energyStorage.serializeNBT());
		nbt.put("fluid", tankout.writeToNBT(new CompoundTag()));
		if(processes[0]!=null)
			nbt.put("process0", processes[0].save());
		if(processes[1]!=null)
			nbt.put("process1", processes[1].save());
	}

	@Override
	public void readSaveNBT(CompoundTag nbt) {
		active = nbt.getBoolean("active");
		inventory.deserializeNBT(nbt.getCompound("inv"));
		energyStorage.deserializeNBT(nbt.getCompound("energy"));
		tankout.readFromNBT(nbt.getCompound("fluid"));
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
	public void writeSyncNBT(CompoundTag nbt) {
		nbt.putBoolean("active", active);
	}

	@Override
	public void readSyncNBT(CompoundTag nbt) {
		active = nbt.getBoolean("active");
	}
}
