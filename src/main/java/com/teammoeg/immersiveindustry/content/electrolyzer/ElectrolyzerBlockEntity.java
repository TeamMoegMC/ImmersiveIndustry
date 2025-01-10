/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.content.electrolyzer;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorageAdvanced;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ElectrolyzerBlockEntity extends IEBaseTileEntity implements IIEInventory, EnergyHelper.IIEInternalFluxHandler,
        ITickableTileEntity, IEBlockInterfaces.IProcessTile, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IInteractionObjectIE {
    public int process = 0;
    public int processMax = 0;
    public int tickEnergy = 0;
    public ItemStack result = ItemStack.EMPTY;
    public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(20000);
    public FluidTank tank = new FluidTank(8 * FluidAttributes.BUCKET_VOLUME, ElectrolyzerRecipe::isValidRecipeFluid);
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);


    public ElectrolyzerBlockEntity() {
        super(IIContent.IITileTypes.ELECTROLYZER.get());
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        energyStorage.readFromNBT(nbt);
        tank.readFromNBT(nbt.getCompound("tank"));
        process = nbt.getInt("process");
        processMax = nbt.getInt("processMax");
        if (!descPacket) {
            result = ItemStack.read(nbt.getCompound("result"));
            tickEnergy = nbt.getInt("tickEnergy");
            ItemStackHelper.loadAllItems(nbt, inventory);
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        energyStorage.writeToNBT(nbt);
        nbt.put("tank", tank.writeToNBT(new CompoundNBT()));
        nbt.putInt("process", process);
        nbt.putInt("processMax", processMax);
        if (!descPacket) {
            nbt.put("result", result.serializeNBT());
            nbt.putInt("tickEnergy", tickEnergy);
            ItemStackHelper.saveAllItems(nbt, inventory);
        }
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (energyStorage.getEnergyStored() >= tickEnergy) {
                if (process > 0) {
                    process--;
                    energyStorage.extractEnergy(tickEnergy, false);
                    this.markContainingBlockForUpdate(null);
                    return;
                }
                if (!result.isEmpty()) {
                    if (inventory.get(1).isEmpty()) {
                        inventory.set(1, result);
                        result = ItemStack.EMPTY;
                        process = processMax = 0;
                        tickEnergy = 0;
                	} else if (inventory.get(1).isItemEqual(result)) {
                    	inventory.get(1).grow(result.getCount());
                    	result=ItemStack.EMPTY;
                        process = processMax = 0;
                        tickEnergy = 0;
                	}else return;
                }
                ElectrolyzerRecipe recipe=getRecipe();
                if (recipe != null) {
                    this.processMax = this.process = recipe.time;
                    this.tickEnergy = recipe.tickEnergy;
                    if (recipe.inputs.length > 0) {
                        Utils.modifyInvStackSize(inventory, 0, -recipe.inputs[0].getCount());
                    }
                    if (recipe.input_fluid != null)
                        tank.drain(recipe.input_fluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    result = recipe.output.copy();
                }
            } else if (process > 0) {
                process = processMax;
                this.markContainingBlockForUpdate(null);
            }
        }
    }

    @Nullable
    public ElectrolyzerRecipe getRecipe() {
        ElectrolyzerRecipe recipe = ElectrolyzerRecipe.findRecipe(inventory.get(0),ItemStack.EMPTY, tank.getFluid(),false);
        if (recipe == null)
            return null;
        if (inventory.get(1).isEmpty() || (ItemStack.areItemsEqual(inventory.get(1), recipe.output) &&
                inventory.get(1).getCount() + recipe.output.getCount() <= getSlotLimit(1))) {
            return recipe;
        }
        return null;
    }

    @Override
    public int[] getCurrentProcessesStep() {
        return new int[]{processMax - process};
    }

    @Override
    public int[] getCurrentProcessesMax() {
        return new int[]{processMax};
    }


    public LazyOptional<IFluidHandler> fluidHandler = registerConstantCap(new FluidHandler(this));
    LazyOptional<IItemHandler> invHandler = registerConstantCap(
            new IEInventoryHandler(2, this, 0, new boolean[]{true, false},
                    new boolean[]{false, true})
    );

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {
        if(facing!=null&&facing.getAxis()!=this.getFacing().rotateY().getAxis()) {
	    	if (facing!=Direction.UP&&capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
	            return fluidHandler.cast();
	        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
	            return invHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public FluxStorage getFluxStorage() {
        return energyStorage;
    }

    @Nonnull
    @Override
    public IEEnums.IOSideConfig getEnergySideConfig(@Nullable Direction facing) {
        return facing != this.getFacing().rotateY() ? IEEnums.IOSideConfig.NONE : IEEnums.IOSideConfig.INPUT;
    }

    EnergyHelper.IEForgeEnergyWrapper wrapper = null;

    @Nullable
    @Override
    public EnergyHelper.IEForgeEnergyWrapper getCapabilityWrapper(Direction facing) {
        if (facing != this.getFacing().rotateY()) {
            return null;
        }
		if (this.wrapper == null) {
		    this.wrapper = new EnergyHelper.IEForgeEnergyWrapper(this, this.getFacing().rotateY());
		}
		return wrapper;
    }

    static class FluidHandler implements IFluidHandler {
        ElectrolyzerBlockEntity tile;

        @Nullable
        FluidHandler(ElectrolyzerBlockEntity tile) {
            this.tile = tile;
        }

        @Override
        public int fill(FluidStack resource, FluidAction doFill) {
            if (resource == null)
                return 0;

            int i = tile.tank.fill(resource, doFill);
            if (i > 0) {
                tile.markDirty();
                tile.markContainingBlockForUpdate(null);
            }
            return i;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction doDrain) {
            if (resource == null)
                return FluidStack.EMPTY;
            return this.drain(resource.getAmount(), doDrain);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction doDrain) {
            FluidStack f = tile.tank.drain(maxDrain, doDrain);
            if (!f.isEmpty()) {
                tile.markDirty();
                tile.markContainingBlockForUpdate(null);
            }
            return f;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return tile.tank.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return tile.tank.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tile.tank.isFluidValid(tank, stack);
        }
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Override
    public Property<Direction> getFacingProperty() {
        return ElectrolyzerBlock.FACING;
    }

    @Override
    public PlacementLimitation getFacingLimitation() {
        return PlacementLimitation.PISTON_LIKE;
    }

    @Nullable
    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return this;
    }

    @Override
    public boolean canUseGui(PlayerEntity player) {
        return true;
    }
}
