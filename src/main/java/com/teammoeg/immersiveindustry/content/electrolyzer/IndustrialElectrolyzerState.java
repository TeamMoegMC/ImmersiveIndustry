package com.teammoeg.immersiveindustry.content.electrolyzer;

import net.minecraft.core.HolderLookup;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.teammoeg.immersiveindustry.util.CapabilityProcessor;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

public class IndustrialElectrolyzerState implements IMultiblockState {
	//common properties
	public RecipeHandler<ElectrolyzerRecipe> recipe;
	public MutableEnergyStorage energyStorage = new MutableEnergyStorage(32000);
	public FluidTank[] tank = new FluidTank[]{null,
			new FluidTank(16000)};
	public ChangeDetectedItemHandler inventory;
	//client properties
	boolean hasElectrode1=false;
	boolean hasElectrode2=false;
	boolean active;
	BooleanSupplier isSoundPlaying = () -> false;
	//transient capability and interfaces
	CapabilityReference<IItemHandler> outInvCap1;
	CapabilityReference<IFluidHandler> outFluidCap1;
	CapabilityReference<IItemHandler> outInvCap2;
	CapabilityReference<IFluidHandler> outFluidCap2;
	CapabilityProcessor capabilities=new CapabilityProcessor();
	public final RSState state=RSState.enabledByDefault();
	
	public IndustrialElectrolyzerState(IInitialMultiblockContext<IndustrialElectrolyzerState> capabilitySource) {
		Supplier<Level> l=capabilitySource.levelSupplier();
		inventory=new ChangeDetectedItemHandler(5,capabilitySource.getMarkDirtyRunnable());
		recipe=new RecipeHandler<>((t,r)->r.time);
		inventory.addSlotListener(0,2, recipe::onContainerChanged);
		tank[0]=new FluidTank(16000, f->ElectrolyzerRecipe.isValidRecipeFluid(l.get(),f));
		ArrayFluidHandler inFluidHandler1=ArrayFluidHandler.fillOnly(tank[0], ()->{
			capabilitySource.getMarkDirtyRunnable().run();
			recipe.onContainerChanged();
		});
		ArrayFluidHandler inFluidHandler2=ArrayFluidHandler.fillOnly(tank[0], ()->{
			capabilitySource.getMarkDirtyRunnable().run();
			recipe.onContainerChanged();
		});
		outFluidCap1=IndustrialElectrolyzerLogic.out1.getFacingCapability(capabilitySource, Capabilities.FluidHandler.BLOCK);
		outFluidCap2=IndustrialElectrolyzerLogic.out2.getFacingCapability(capabilitySource, Capabilities.FluidHandler.BLOCK);
		outInvCap1=IndustrialElectrolyzerLogic.out1.getFacingCapability(capabilitySource, Capabilities.ItemHandler.BLOCK);
		outInvCap2=IndustrialElectrolyzerLogic.out2.getFacingCapability(capabilitySource, Capabilities.ItemHandler.BLOCK);
		capabilities.fluidHandler()
		.addCapability(IndustrialElectrolyzerLogic.in1,inFluidHandler1)
		.addCapability(IndustrialElectrolyzerLogic.in2,inFluidHandler2)
		.addCapability(IndustrialElectrolyzerLogic.out1,ArrayFluidHandler.drainOnly(tank[1], capabilitySource.getMarkDirtyRunnable()))
		.addCapability(IndustrialElectrolyzerLogic.out2,ArrayFluidHandler.drainOnly(tank[1], capabilitySource.getMarkDirtyRunnable()));
		capabilities.itemHandler()
		.addCapability(IndustrialElectrolyzerLogic.in1,new RangedCheckedInputWrapper(inventory,0,2,(i,r)->ElectrolyzerRecipe.isValidRecipeInput(l.get(),r)))
		.addCapability(IndustrialElectrolyzerLogic.in2,new RangedCheckedInputWrapper(inventory,0,2,(i,r)->ElectrolyzerRecipe.isValidRecipeInput(l.get(),r)))
		.addCapability(IndustrialElectrolyzerLogic.out1,new RangedOutputWrapper(inventory,4,5))
		.addCapability(IndustrialElectrolyzerLogic.out2,new RangedOutputWrapper(inventory,4,5));
		capabilities.energy()
		.addCapability(IndustrialElectrolyzerLogic.energy, new WrappingEnergyStorage(energyStorage,true,false,capabilitySource.getMarkDirtyRunnable()));
		
	}
	@Override
	public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		recipe.writeCustomNBT(nbt, false);
		nbt.putInt("energy", energyStorage.getEnergyStored());
		nbt.put("tank0",tank[0].writeToNBT(provider, new CompoundTag()));
		nbt.put("tank1",tank[1].writeToNBT(provider, new CompoundTag()));
		nbt.put("inv",inventory.serializeNBT(provider));
		
		nbt.putBoolean("elec1",hasElectrode1);
		nbt.putBoolean("elec2",hasElectrode2);
	}
	@Override
	public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		recipe.readCustomNBT(nbt, false);
		energyStorage.setStoredEnergy(nbt.getInt("energy"));
		tank[0].readFromNBT(provider, nbt.getCompound("tank0"));
		tank[1].readFromNBT(provider, nbt.getCompound("tank1"));
		inventory.deserializeNBT(provider, nbt.getCompound("inv"));
		
		hasElectrode1=nbt.getBoolean("elec1");
		hasElectrode2=nbt.getBoolean("elec2");
		
	}
	
	@Override
	public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.putBoolean("active", active);
		nbt.putBoolean("elec1",hasElectrode1);
		nbt.putBoolean("elec2",hasElectrode2);
	}
	@Override
	public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		active=nbt.getBoolean("active");
		hasElectrode1=nbt.getBoolean("elec1");
		hasElectrode2=nbt.getBoolean("elec2");
	}

}
