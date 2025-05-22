package com.teammoeg.immersiveindustry.content.steamturbine;

import java.util.function.BooleanSupplier;

import com.google.common.collect.ImmutableList;
import com.teammoeg.immersiveindustry.IIConfig;

import blusunrize.immersiveengineering.api.energy.NullEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class SteamTurbineState implements IMultiblockState{
	public final static TagKey<Fluid> fluidTag=FluidTags.create(new ResourceLocation("forge", "steam"));
	public final RSState rsstate=RSState.enabledByDefault();
	public boolean active = false;
	@SuppressWarnings("deprecation")
	public FluidTank tanks= new FluidTank(4*20*IIConfig.SERVER.steamTurbineInputMax.get(), fluidStack -> {
        return fluidStack.getFluid().is(fluidTag);
    });
	BooleanSupplier isSoundPlaying=()->false;
	final StoredCapability<IFluidHandler> fluidCap;
	final StoredCapability<IEnergyStorage> energyView=new StoredCapability<>(NullEnergyStorage.INSTANCE);
	ImmutableList<CapabilityReference<IEnergyStorage>> energyOutputs;
	float saturation=0;
	int energyBuffer=0;
	public SteamTurbineState(IInitialMultiblockContext<SteamTurbineState> capabilitySource) {
		ImmutableList.Builder<CapabilityReference<IEnergyStorage>> outputs = ImmutableList.builder();
		
		outputs.add(SteamTurbineLogic.ENERGY_OUT1.getFacingCapability(capabilitySource, ForgeCapabilities.ENERGY));
		outputs.add(SteamTurbineLogic.ENERGY_OUT2.getFacingCapability(capabilitySource, ForgeCapabilities.ENERGY));
		this.energyOutputs = outputs.build();
		fluidCap=new StoredCapability<>(ArrayFluidHandler.fillOnly(tanks, capabilitySource.getMarkDirtyRunnable()));
		
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt) {
		tanks.writeToNBT(nbt);
		nbt.putBoolean("active", active);
		nbt.putFloat("saturation", saturation);
		nbt.putInt("energyBuffer", energyBuffer);
	}

	@Override
	public void readSaveNBT(CompoundTag nbt) {
		tanks.readFromNBT(nbt);
		active = nbt.getBoolean("active");
		saturation=nbt.getFloat("saturation");
		energyBuffer=nbt.getInt("energyBuffer");
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