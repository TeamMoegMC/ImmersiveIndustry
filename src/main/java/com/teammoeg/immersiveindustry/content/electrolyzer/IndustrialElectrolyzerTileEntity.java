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
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class IndustrialElectrolyzerTileEntity extends MultiblockPartTileEntity<IndustrialElectrolyzerTileEntity> implements
        IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory, IEBlockInterfaces.IInteractionObjectIE {
    public int process = 0;
    public int processMax = 0;
    public final int energyConsume;
    public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(32000);
    EnergyHelper.IEForgeEnergyWrapper wrapper = new EnergyHelper.IEForgeEnergyWrapper(this, null);
    public FluidTank[] tank = new FluidTank[]{new FluidTank(16000, ElectrolyzerRecipe::isValidRecipeFluid), new FluidTank(16000)};
    private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);


    public IndustrialElectrolyzerTileEntity() {
        super(IIContent.IIMultiblocks.IND_ELE, IIContent.IITileTypes.IND_ELE.get(), true);
        energyConsume = IIConfig.COMMON.electrolyzerConsume.get() * 2;
    }


    @Nonnull
    @Override
    public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx) {
        return VoxelShapes.fullCube();
    }


    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        IndustrialElectrolyzerTileEntity master = master();
        if (master != null) {
            if (posInMultiblock.getZ() == 3 && posInMultiblock.getY() == 0
                    && side == null || side == getFacing().getOpposite()) {

                return new FluidTank[]{master.tank[0]};
            }
        }
        return new FluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int i, Direction direction, FluidStack fluidStack) {
        return ElectrolyzerRecipe.isValidRecipeFluid(fluidStack);
    }

    @Override
    protected boolean canDrainTankFrom(int i, Direction direction) {
        return false;
    }


    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        energyStorage.readFromNBT(nbt);
        tank[0].readFromNBT(nbt.getCompound("tank0"));
        tank[1].readFromNBT(nbt.getCompound("tank1"));
        process = nbt.getInt("process");
        processMax = nbt.getInt("processMax");
        ItemStackHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        energyStorage.writeToNBT(nbt);
        nbt.put("tank0", tank[0].writeToNBT(new CompoundNBT()));
        nbt.put("tank1", tank[1].writeToNBT(new CompoundNBT()));
        nbt.putInt("process", process);
        nbt.putInt("processMax", processMax);
        ItemStackHelper.saveAllItems(nbt, inventory);
    }

    @Override
    public void tick() {
        checkForNeedlessTicking();
        if (!isDummy()) {
            if (!world.isRemote) {
                if (!isRSDisabled() && energyStorage.getEnergyStored() >= energyConsume) {
                    ElectrolyzerRecipe recipe = getRecipe();
                    if (process > 0) {
                        if (inventory.get(0).isEmpty()) {
                            process = 0;
                            processMax = 0;
                        }
                        // during process
                        else {
                            if (recipe == null || recipe.time != processMax) {
                                process = 0;
                                    processMax = 0;
                                } else {
                                    process--;
                                    energyStorage.extractEnergy(energyConsume, false);
                                }
                            }
                            this.markContainingBlockForUpdate(null);
                        } else if (recipe != null) {
                            if (processMax == 0) {
                                this.process = recipe.time / 2;
                                this.processMax = process;
                            } else {
                                Utils.modifyInvStackSize(inventory, 0, -recipe.input.getCount());
                                tank[0].drain(recipe.input_fluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                                if (!inventory.get(1).isEmpty())
                                    inventory.get(1).grow(recipe.output.copy().getCount());
                                else if (inventory.get(1).isEmpty())
                                    inventory.set(1, recipe.output.copy());
                                processMax = 0;
                            }
                        }
                    } else if (process > 0) {
                        process = processMax;
                        this.markContainingBlockForUpdate(null);
                    }
            }
        }
    }

    @Nullable
    public ElectrolyzerRecipe getRecipe() {
        if (inventory.get(0).isEmpty())
            return null;
        ElectrolyzerRecipe recipe = ElectrolyzerRecipe.findRecipe(inventory.get(0), inventory.get(1), tank[0].getFluid());
        if (recipe == null)
            return null;
        if (inventory.get(1).isEmpty() || (ItemStack.areItemsEqual(inventory.get(1), recipe.output) &&
                inventory.get(1).getCount() + recipe.output.getCount() <= getSlotLimit(1))) {
            return recipe;
        }
        return null;
    }

    LazyOptional<IItemHandler> invHandler = registerConstantCap(
            new IEInventoryHandler(2, this, 0, new boolean[]{true, true, false, true, true},
                    new boolean[]{false, false, true, false, false})
    );

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return invHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack) {
        if (slot == 0)
            return ElectrolyzerRecipe.isValidRecipeInput(stack, false);
        else if (slot == 1)
            return ElectrolyzerRecipe.isValidRecipeInput(stack, true);
        return false;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Nonnull
    @Override
    public IEEnums.IOSideConfig getEnergySideConfig(Direction facing) {
        return this.formed && this.isEnergyPos() ? IEEnums.IOSideConfig.INPUT : IEEnums.IOSideConfig.NONE;
    }

    @Nonnull
    @Override
    public FluxStorage getFluxStorage() {
        IndustrialElectrolyzerTileEntity master = this.master();
        return master != null ? master.energyStorage : this.energyStorage;
    }

    public boolean isEnergyPos() {
        return this.getEnergyPos().contains(this.posInMultiblock);
    }

    public Set<BlockPos> getEnergyPos() {
        return ImmutableSet.of(new BlockPos(1, 1, 0));
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return ImmutableSet.of(
                new BlockPos(1, 1, 0)
        );
    }

    @Nullable
    @Override
    public EnergyHelper.IEForgeEnergyWrapper getCapabilityWrapper(Direction facing) {
        return this.formed && this.isEnergyPos() ? this.wrapper : null;
    }

    public void postEnergyTransferUpdate(int energy, boolean simulate) {
        if (!simulate) {
            this.updateMasterBlock(null, energy != 0);
        }

    }

    @Nullable
    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return master();
    }

    @Override
    public boolean canUseGui(PlayerEntity player) {
        return formed;
    }
}