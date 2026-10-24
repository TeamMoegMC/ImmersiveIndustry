package com.teammoeg.immersiveindustry.content.chemical_reactor;

import net.minecraft.core.HolderLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor.CapabilityBuilder;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.MultipleTankHandler;
import com.teammoeg.immersiveindustry.util.RangedCheckedInputWrapper;
import com.teammoeg.immersiveindustry.util.RangedOutputWrapper;
import com.teammoeg.immersiveindustry.util.RecipeHandler;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.teammoeg.immersiveindustry.util.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;

public class ChemicalState implements IMultiblockState {
	class RecipeResetFluidTank extends FluidTank{

		public RecipeResetFluidTank(int capacity) {
			super(capacity);
		}

		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			recipe.onContainerChanged();
		}
		
	}
	//common properties
    ChangeDetectedItemHandler inventory;
    FluidTank[] inTank=new FluidTank[] {new RecipeResetFluidTank(7000),new RecipeResetFluidTank(7000),new RecipeResetFluidTank(7000)};
    FluidTank[] outTank=new FluidTank[] {new RecipeResetFluidTank(7000),new RecipeResetFluidTank(7000),new RecipeResetFluidTank(7000)};
    public MutableEnergyStorage energyStorage = new MutableEnergyStorage(32000);
    RecipeHandler<ChemicalRecipe> recipe;
    //client properties
    boolean active;
	BooleanSupplier isSoundPlaying = () -> false;
    //capability handlers
	final IItemHandler inputHandler;
    final IItemHandler outputHandler;
    final MultipleTankHandler recipeOutputFluidHandler=new MultipleTankHandler(outTank);
    final MultipleTankHandler recipeInputFluidHandler=new MultipleTankHandler(inTank);
    List<CapabilityReference<IFluidHandler>> outputFluidCap=new ArrayList<>();
    CapabilityReference<IItemHandler> outputItemCap;
    CapabilityProcessor capabilities=new CapabilityProcessor();
    public RSState state=RSState.enabledByDefault();
	public ChemicalState(IInitialMultiblockContext<ChemicalState> capabilitySource) {
		Supplier<@Nullable Level> level=capabilitySource.levelSupplier();
		//initialize data
		recipe=new RecipeHandler<>((r,t)->t.time);
		inventory=new ChangeDetectedItemHandler(6, capabilitySource.getMarkDirtyRunnable());
		inventory.addSlotListener(0,3, recipe::onContainerChanged);
		inputHandler=new RangedCheckedInputWrapper(inventory,0,3,(i,r)->ChemicalRecipe.isValidInput(level.get(),r));
		outputHandler=new RangedOutputWrapper(inventory,3,6);
		//fluidio definition
		int num=0;
		CapabilityBuilder<IFluidHandler> fluid=capabilities.fluidHandler();
		for(CapabilityFacing i:ChemicalLogic.out) {
			ArrayFluidHandler todrain=ArrayFluidHandler.drainOnly(inTank[num++], capabilitySource.getMarkDirtyRunnable());
			outputFluidCap.add(i.getFacingCapability(capabilitySource, Capabilities.FluidHandler.BLOCK));
			fluid.addCapability(i, todrain);
		}
		num=0;
		for(CapabilityFacing i:ChemicalLogic.in) {
			ArrayFluidHandler tofill=ArrayFluidHandler.fillOnly(inTank[num++], capabilitySource.getMarkDirtyRunnable());
			fluid.addCapability(i, tofill);
		}
		//itemio definition
		/*CapabilityBuilder<IItemHandler> item=capabilities.itemHandler();
		for(CapabilityFacing i:ChemicalLogic.itemin) {
			item.addCapability(i, inputHandler);
		}*/
		outputItemCap=ChemicalLogic.itemout.getFacingCapability(capabilitySource, Capabilities.ItemHandler.BLOCK);
		//energyio definition
		capabilities.energy().addCapability(ChemicalLogic.energy, energyStorage);
		
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.put("inv", inventory.serializeNBT(provider));
		recipe.writeCustomNBT(nbt, false);
		nbt.putInt("energy", energyStorage.getEnergyStored());
		ListTag tanks=new ListTag();
		for(FluidTank tank:inTank) {
			tanks.add(tank.writeToNBT(provider, new CompoundTag()));
		}
		for(FluidTank tank:outTank) {
			tanks.add(tank.writeToNBT(provider, new CompoundTag()));
		}
		nbt.put("tanks", tanks);
	}

	@Override
	public void readSaveNBT(CompoundTag nbt, HolderLookup.Provider provider) {
        inventory.deserializeNBT(provider, nbt.getCompound("inv"));
        recipe.readCustomNBT(nbt, false);
        energyStorage.setStoredEnergy(nbt.getInt("energy"));
        ListTag tanks=nbt.getList("tanks", Tag.TAG_COMPOUND);
        for(int i=0;i<3;i++)
        	inTank[i].readFromNBT(provider, tanks.getCompound(i));
        for(int i=0;i<3;i++)
        	outTank[i].readFromNBT(provider, tanks.getCompound(i+3));
	}

	@Override
	public void writeSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.putBoolean("active", active);
	}

	@Override
	public void readSyncNBT(CompoundTag nbt, HolderLookup.Provider provider) {
		active=nbt.getBoolean("active");
	}

}
