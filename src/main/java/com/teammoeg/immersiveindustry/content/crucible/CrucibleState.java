package com.teammoeg.immersiveindustry.content.crucible;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.RangedCheckedInputWrapper;
import com.teammoeg.immersiveindustry.util.RangedOutputWrapper;
import com.teammoeg.immersiveindustry.util.RecipeHandler;
import com.teammoeg.immersiveindustry.util.RecipeProcessResult;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;

public class CrucibleState implements IMultiblockState {
	//common properties
    ChangeDetectedItemHandler inventory;
    RecipeHandler<CrucibleRecipe> recipe;
    public int temperature;
    public int burnTime;
    public int burnTimeMax;
    public FluidTank tank = new FluidTank(14400);
    //client properties
    boolean active;
    boolean hasPreheater;
    //capability handlers
    StoredCapability<IItemHandler> inputHandler;
    StoredCapability<IItemHandler> fuelHandler;
    StoredCapability<IItemHandler> outputHandler;
    StoredCapability<IFluidHandler> outputFluidHandler;
    CapabilityReference<IFluidHandler> outputFluidCap;
	public CrucibleState(IInitialMultiblockContext<CrucibleState> capabilitySource) {
		Supplier<@Nullable Level> level=capabilitySource.levelSupplier();
		recipe=new RecipeHandler<>((r,t)->t.time);
		inventory=new ChangeDetectedItemHandler(6, capabilitySource.getMarkDirtyRunnable());
		inventory.addSlotListener(0,5, recipe::onContainerChanged);
		inputHandler=new StoredCapability<>(new RangedCheckedInputWrapper(inventory,0,4,(i,r)->CrucibleRecipe.isValidInput(level.get(),r)));
		fuelHandler=new StoredCapability<>(new RangedCheckedInputWrapper(inventory,4,5,(i,r)->CrucibleRecipe.getFuelTime(level.get(),r)>0));
		outputHandler=new StoredCapability<>(new RangedOutputWrapper(inventory,5,6));
		outputFluidHandler=new StoredCapability<>(ArrayFluidHandler.drainOnly(tank, capabilitySource.getMarkDirtyRunnable()));
		outputFluidCap=CrucibleLogic.fluidout.getFacingCapability(capabilitySource, ForgeCapabilities.FLUID_HANDLER);
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt) {
		nbt.put("inv", inventory.serializeNBT());
		recipe.writeCustomNBT(nbt, false);
		nbt.putInt("temperature", temperature);
        nbt.putInt("burntime", burnTime);
        nbt.putInt("burnTimeMax", burnTimeMax);
        nbt.put("tank", tank.writeToNBT(new CompoundTag()));
	}

	@Override
	public void readSaveNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt.getCompound("inv"));
        recipe.readCustomNBT(nbt, false);
        temperature=nbt.getInt("temperature");
        burnTime=nbt.getInt("burntime");
        burnTimeMax=nbt.getInt("burnTimeMax");
        tank.readFromNBT(nbt.getCompound("tank"));
	}

	@Override
	public void writeSyncNBT(CompoundTag nbt) {
		nbt.putBoolean("active", active);
	}

	@Override
	public void readSyncNBT(CompoundTag nbt) {
		active=nbt.getBoolean("active");
	}

}
