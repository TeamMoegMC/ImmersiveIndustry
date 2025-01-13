package com.teammoeg.immersiveindustry.content.rotarykiln;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.teammoeg.immersiveindustry.content.steamturbine.SteamTurbineState;
import com.teammoeg.immersiveindustry.util.RecipeHandler;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraint;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler.IOConstraintGroup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class RotaryKilnState implements IMultiblockState{
	
	boolean active;
	public int angle;// angle for animation in degrees
	public IItemHandlerModifiable inventory;
	public MutableEnergyStorage energyStorage = new MutableEnergyStorage(32000);
	public FluidTank[] tankout = new FluidTank[]{new FluidTank(32000)};
	RecipeHandler<RotaryKilnRecipe> handler=new RecipeHandler<>(()->{},(lr,cr)->{
		if(cr.getId().equals(lr)) {
			return cr.time/16;
		}
		return cr.time;
	});
	
	StoredCapability<IEnergyStorage> energyCap=new StoredCapability<>(energyStorage);
	StoredCapability<IItemHandler> invCap;
	public RotaryKilnState(IInitialMultiblockContext<SteamTurbineState> capabilitySource) {
		Runnable markDirty=capabilitySource.getMarkDirtyRunnable();
		inventory=handler.createItemHanlderWrapper(
			SlotwiseItemHandler.makeWithGroups(()->{handler.onContainerChanged();markDirty.run();},
			new IOConstraintGroup(IOConstraint.input(r->RotaryKilnRecipe.isValidRecipeInput(capabilitySource.levelSupplier().get(), r)),1),
			new IOConstraintGroup(IOConstraint.BLOCKED,2),
			new IOConstraintGroup(IOConstraint.OUTPUT,2)
			),1);
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt) {
		handler.writeCustomNBT(nbt, false);
		
	}

	@Override
	public void readSaveNBT(CompoundTag nbt) {
		handler.readCustomNBT(nbt, false);
		
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
