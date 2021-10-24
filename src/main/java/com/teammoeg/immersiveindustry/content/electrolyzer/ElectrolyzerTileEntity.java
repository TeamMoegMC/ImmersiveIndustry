package com.teammoeg.immersiveindustry.content.electrolyzer;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;

public class ElectrolyzerTileEntity extends IEBaseTileEntity implements IIEInventory,
        ITickableTileEntity, IEBlockInterfaces.IProcessTile, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IInteractionObjectIE {
    public int process = 0;
    public int processMax = 0;
    public FluidTank tank = new FluidTank(8 * FluidAttributes.BUCKET_VOLUME, ElectrolyzerRecipe::isValidRecipeFluid);
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public ElectrolyzerTileEntity() {
        super(IIContent.IITileTypes.ELECTROLYZER.get());
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {

    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {

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
