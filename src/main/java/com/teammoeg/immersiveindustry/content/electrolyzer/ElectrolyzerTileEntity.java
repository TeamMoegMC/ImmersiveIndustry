package com.teammoeg.immersiveindustry.content.electrolyzer;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorageAdvanced;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ElectrolyzerTileEntity extends IEBaseTileEntity implements IIEInventory,
        ITickableTileEntity, IEBlockInterfaces.IProcessTile, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IInteractionObjectIE {
    public int process = 0;
    public int processMax = 0;
    public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(20000);
    public FluidTank tank = new FluidTank(8 * FluidAttributes.BUCKET_VOLUME, ElectrolyzerRecipe::isValidRecipeFluid);
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public ElectrolyzerTileEntity() {
        super(IIContent.IITileTypes.ELECTROLYZER.get());
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        energyStorage.readFromNBT(nbt);
        tank.readFromNBT(nbt.getCompound("tank"));
        process = nbt.getInt("process");
        processMax = nbt.getInt("processMax");
        ItemStackHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        energyStorage.writeToNBT(nbt);
        nbt.put("tank", tank.writeToNBT(new CompoundNBT()));
        nbt.putInt("process", process);
        nbt.putInt("processMax", processMax);
        ItemStackHelper.saveAllItems(nbt, inventory);
    }

    @Override
    public void tick() {

    }

    @Override
    public int[] getCurrentProcessesStep() {
        return new int[]{processMax - process};
    }

    @Override
    public int[] getCurrentProcessesMax() {
        return new int[]{processMax};
    }

    LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> tank);
    LazyOptional<IItemHandler> invHandler = registerConstantCap(
            new IEInventoryHandler(2, this, 0, new boolean[]{true, false},
                    new boolean[]{false, true})
    );

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return fluidHandler.cast();
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
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates(int slot) {

    }

    @Override
    public Property<Direction> getFacingProperty() {
        return IEProperties.FACING_ALL;
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
