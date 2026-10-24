package com.teammoeg.immersiveindustry.content.steamturbine;

import net.minecraft.core.HolderLookup;
import java.util.function.BooleanSupplier;

import com.google.common.collect.ImmutableList;
import com.teammoeg.immersiveindustry.IIConfig;

import blusunrize.immersiveengineering.api.energy.NullEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.teammoeg.immersiveindustry.util.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class SteamTurbineState implements IMultiblockState{
	public final static TagKey<Fluid> fluidTag=FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "steam"));
	public final RSState rsstate=RSState.enabledByDefault();
	public boolean active = false;
	@SuppressWarnings("deprecation")
	public FluidTank tanks= new FluidTank(4*20*IIConfig.SERVER.steamTurbineInputMax.get(), fluidStack -> {
        return fluidStack.getFluid().is(fluidTag);
    });
	BooleanSupplier isSoundPlaying=()->false;
	final IFluidHandler fluidCap;
	final IEnergyStorage energyView=NullEnergyStorage.INSTANCE;
	ImmutableList<CapabilityReference<IEnergyStorage>> energyOutputs;
	float saturation=0;
	int energyBuffer=0;
	public SteamTurbineState(IInitialMultiblockContext<SteamTurbineState> capabilitySource) {
		ImmutableList.Builder<CapabilityReference<IEnergyStorage>> outputs = ImmutableList.builder();
		
		outputs.add(SteamTurbineLogic.ENERGY_OUT1.getFacingCapability(capabilitySource, Capabilities.EnergyStorage.BLOCK));
		outputs.add(SteamTurbineLogic.ENERGY_OUT2.getFacingCapability(capabilitySource, Capabilities.EnergyStorage.BLOCK));
		this.energyOutputs = outputs.build();
		fluidCap=ArrayFluidHandler.fillOnly(tanks, capabilitySource.getMarkDirtyRunnable());
		
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		tanks.writeToNBT(provider, nbt);
		nbt.putBoolean("active", active);
		nbt.putFloat("saturation", saturation);
		nbt.putInt("energyBuffer", energyBuffer);
	}

	@Override
	public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		tanks.readFromNBT(provider, nbt);
		active = nbt.getBoolean("active");
		saturation=nbt.getFloat("saturation");
		energyBuffer=nbt.getInt("energyBuffer");
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