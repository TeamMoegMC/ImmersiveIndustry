package com.teammoeg.immersiveindustry.content.electrolyzer;

import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnState;
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
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;

public class IndustrialElectrolyzerState implements IMultiblockState {
	//common properties
	public RecipeHandler<ElectrolyzerRecipe,Pair<ElectrolyzerRecipe, Map<Integer, Integer>>> recipe;
	public MutableEnergyStorage energyStorage = new MutableEnergyStorage(32000);
	public FluidTank[] tank = new FluidTank[]{null,
			new FluidTank(16000)};
	public ChangeDetectedItemHandler inventory;
	//client properties
	boolean hasElectrode1=false;
	boolean hasElectrode2=false;
	boolean active;
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
		recipe=new RecipeHandler<>((t,r)->r.time,t->ElectrolyzerRecipe.executeRecipe(l.get(),t, inventory.getStackInSlot(0), inventory.getStackInSlot(1), tank[0].getFluid(), true));
		inventory.addSlotListener(0, recipe::onContainerChanged);
		inventory.addSlotListener(1, recipe::onContainerChanged);
		tank[0]=new FluidTank(16000, f->ElectrolyzerRecipe.isValidRecipeFluid(l.get(),f)) {

			@Override
			protected void onContentsChanged() {
				recipe.onContainerChanged();
			}
			
		};
		outFluidCap1=IndustrialElectrolyzerLogic.out1.getFacingCapability(capabilitySource, ForgeCapabilities.FLUID_HANDLER);
		outFluidCap2=IndustrialElectrolyzerLogic.out2.getFacingCapability(capabilitySource, ForgeCapabilities.FLUID_HANDLER);
		outInvCap1=IndustrialElectrolyzerLogic.out1.getFacingCapability(capabilitySource, ForgeCapabilities.ITEM_HANDLER);
		outInvCap2=IndustrialElectrolyzerLogic.out2.getFacingCapability(capabilitySource, ForgeCapabilities.ITEM_HANDLER);
		capabilities.fluidHandler()
		.addCapability(IndustrialElectrolyzerLogic.in1,ArrayFluidHandler.fillOnly(tank[0], capabilitySource.getMarkDirtyRunnable()))
		.addCapability(IndustrialElectrolyzerLogic.in2,ArrayFluidHandler.fillOnly(tank[0], capabilitySource.getMarkDirtyRunnable()))
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
	public void writeSaveNBT(CompoundTag nbt) {
		recipe.writeCustomNBT(nbt, false);
		nbt.put("energy",energyStorage.serializeNBT());
		nbt.put("tank0",tank[0].writeToNBT(new CompoundTag()));
		nbt.put("tank1",tank[1].writeToNBT(new CompoundTag()));
		nbt.put("inv",inventory.serializeNBT());
		
		nbt.putBoolean("elec1",hasElectrode1);
		nbt.putBoolean("elec2",hasElectrode2);
	}
	@Override
	public void readSaveNBT(CompoundTag nbt) {
		recipe.readCustomNBT(nbt, false);
		energyStorage.deserializeNBT(nbt.getCompound("energy"));
		tank[0].readFromNBT(nbt.getCompound("tank0"));
		tank[1].readFromNBT(nbt.getCompound("tank1"));
		inventory.deserializeNBT(nbt.getCompound("inv"));
		
		hasElectrode1=nbt.getBoolean("elec1");
		hasElectrode2=nbt.getBoolean("elec2");
		
	}
	
	@Override
	public void writeSyncNBT(CompoundTag nbt) {
		nbt.putBoolean("active", active);
		nbt.putBoolean("elec1",hasElectrode1);
		nbt.putBoolean("elec2",hasElectrode2);
	}
	@Override
	public void readSyncNBT(CompoundTag nbt) {
		active=nbt.getBoolean("active");
		hasElectrode1=nbt.getBoolean("elec1");
		hasElectrode2=nbt.getBoolean("elec2");
	}

}
