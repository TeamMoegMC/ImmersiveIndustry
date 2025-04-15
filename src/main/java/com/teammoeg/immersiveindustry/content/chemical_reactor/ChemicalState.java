package com.teammoeg.immersiveindustry.content.chemical_reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor;
import com.teammoeg.immersiveindustry.util.CapabilityProcessor.CapabilityBuilder;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.RangedCheckedInputWrapper;
import com.teammoeg.immersiveindustry.util.RangedOutputWrapper;
import com.teammoeg.immersiveindustry.util.RecipeHandler;
import com.teammoeg.immersiveindustry.util.ItemRecipeProcessResult;
import com.teammoeg.immersiveindustry.util.MultipleTankHandler;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;

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
    FluidTank[] inTank=new FluidTank[] {new RecipeResetFluidTank(2000),new RecipeResetFluidTank(2000),new RecipeResetFluidTank(2000)};
    FluidTank[] outTank=new FluidTank[] {new RecipeResetFluidTank(2000),new RecipeResetFluidTank(2000),new RecipeResetFluidTank(2000)};
    public MutableEnergyStorage energyStorage = new MutableEnergyStorage(32000);
    RecipeHandler<ChemicalRecipe> recipe;
    //client properties
    boolean active;
	BooleanSupplier isSoundPlaying = () -> false;
    //capability handlers
	final StoredCapability<IItemHandler> inputHandler;
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
		inputHandler=new StoredCapability<>(new RangedCheckedInputWrapper(inventory,0,4,(i,r)->ChemicalRecipe.isValidInput(level.get(),r)));
		outputHandler=new RangedOutputWrapper(inventory,4,7);
		//fluidio definition
		int num=0;
		CapabilityBuilder<IFluidHandler> fluid=capabilities.fluidHandler();
		for(CapabilityFacing i:ChemicalLogic.out) {
			ArrayFluidHandler todrain=ArrayFluidHandler.drainOnly(inTank[num++], capabilitySource.getMarkDirtyRunnable());
			outputFluidCap.add(i.getFacingCapability(capabilitySource, ForgeCapabilities.FLUID_HANDLER));
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
		outputItemCap=ChemicalLogic.itemout.getFacingCapability(capabilitySource, ForgeCapabilities.ITEM_HANDLER);
		//energyio definition
		capabilities.energy().addCapability(ChemicalLogic.energy, energyStorage);
		
	}

	@Override
	public void writeSaveNBT(CompoundTag nbt) {
		nbt.put("inv", inventory.serializeNBT());
		recipe.writeCustomNBT(nbt, false);
		nbt.put("energy", energyStorage.serializeNBT());
		ListTag tanks=new ListTag();
		for(FluidTank tank:inTank) {
			tanks.add(tank.writeToNBT(new CompoundTag()));
		}
		for(FluidTank tank:outTank) {
			tanks.add(tank.writeToNBT(new CompoundTag()));
		}
		nbt.put("tanks", tanks);
	}

	@Override
	public void readSaveNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt.getCompound("inv"));
        recipe.readCustomNBT(nbt, false);
        energyStorage.deserializeNBT(nbt.get("energy"));
        ListTag tanks=nbt.getList("tanks", Tag.TAG_COMPOUND);
        for(int i=0;i<3;i++)
        	inTank[i].readFromNBT(tanks.getCompound(i));
        for(int i=0;i<3;i++)
        	outTank[i].readFromNBT(tanks.getCompound(i+3));
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
