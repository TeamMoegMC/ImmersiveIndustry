package com.teammoeg.immersiveindustry.content.carkiln;

import net.minecraft.core.HolderLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor.CapabilityBuilder;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.RangedCheckedInputWrapper;
import com.teammoeg.immersiveindustry.util.RangedOutputWrapper;
import com.teammoeg.immersiveindustry.util.RecipeHandler;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.teammoeg.immersiveindustry.util.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

public class CarKilnState implements IMultiblockState {

	ChangeDetectedItemHandler inventory;
	ItemStackHandler result=new ItemStackHandler(5);
	RecipeHandler<CarKilnRecipe> recipe;
	
	public MutableEnergyStorage energyStorage = new MutableEnergyStorage(32000);
	public FluidTank tank = new FluidTank(16000);
	public int maxProcessCount;
	//client properties
	
	int pos;//animation process from 0-52, 0=idle 52=working
	boolean active;
	BooleanSupplier isSoundPlaying = () -> false;
	//transient capabilities
	public RSState state=RSState.enabledByDefault();
	public List<CapabilityReference<IItemHandler>> outputItemCap=new ArrayList<>(3);
	RangedWrapper resultwrap;
	CapabilityProcessor capabilities=new CapabilityProcessor();
	public CarKilnState(IInitialMultiblockContext<CarKilnState> capabilitySource) {
		Supplier<Level> level=capabilitySource.levelSupplier();
		inventory=new ChangeDetectedItemHandler(9,capabilitySource.getMarkDirtyRunnable());
		
		recipe=new RecipeHandler<CarKilnRecipe>((r,t)->t.time);
		inventory.addSlotListener(0, 4,recipe::onContainerChanged);
		CapabilityBuilder<IItemHandler> itemHandler=capabilities.itemHandler();
		RangedOutputWrapper outwrap=new RangedOutputWrapper(inventory,4,9);
		resultwrap=new RangedWrapper(inventory,4,9);
		RangedCheckedInputWrapper inwrap=new RangedCheckedInputWrapper(inventory,0,4,(i,t)->CarKilnRecipe.isValidInput(level.get(), t));
		for(CapabilityFacing i:CarKilnLogic.outputItemCaps) {
			outputItemCap.add(i.getFacingCapability(capabilitySource, Capabilities.ItemHandler.BLOCK));
			itemHandler.addCapability(i, outwrap);
		}
		for(CapabilityFacing i:CarKilnLogic.inputItemCaps) {
			itemHandler.addCapability(i, inwrap);
		}
		capabilities.energy().addCapability(CarKilnLogic.inputEnergyCap, new WrappingEnergyStorage(energyStorage,true,false,capabilitySource.getMarkDirtyRunnable()));
		capabilities.fluidHandler().addCapability(CarKilnLogic.inputFluidCap, ArrayFluidHandler.fillOnly(tank, ()->{
			capabilitySource.getMarkDirtyRunnable().run();
			recipe.onContainerChanged();
			
		}));
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.put("inv", inventory.serializeNBT(provider));
		nbt.put("result", result.serializeNBT(provider));
		recipe.writeCustomNBT(nbt, false);
		nbt.putInt("processes", maxProcessCount);
		
		nbt.putInt("energy", energyStorage.getEnergyStored());
		nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
		nbt.putByte("pos", (byte) pos);
		
	}
	@Override
	public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		inventory.deserializeNBT(provider, nbt.getCompound("inv"));
		result.deserializeNBT(provider, nbt.getCompound("result"));
		recipe.readCustomNBT(nbt, false);
		pos=nbt.getByte("pos");
		maxProcessCount=nbt.getInt("processes");
		energyStorage.setStoredEnergy(nbt.getInt("energy"));
		tank.readFromNBT(provider, nbt.getCompound("tank"));
		
	}

	@Override
	public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.putByte("pos", (byte) pos);
		nbt.putBoolean("active", active);
		nbt.putByte("processes", (byte) maxProcessCount);
	}

	@Override
	public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		pos=nbt.getByte("pos");
		active=nbt.getBoolean("active");
		maxProcessCount=nbt.getByte("processes");
	}

}
