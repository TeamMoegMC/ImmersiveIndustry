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
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.*;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import blusunrize.immersiveengineering.common.blocks.metal.ClocheBlockEntity;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.config.IEClientConfig;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIContent.IIMenus;
import com.teammoeg.immersiveindustry.IIContent.IITileTypes;
import com.teammoeg.immersiveindustry.util.LangUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ElectrolyzerBlockEntity extends IEBaseBlockEntity implements
        IIEInventory, IEServerTickableBE,
        IProcessBE, IStateBasedDirectional, IConfigurableSides, IBlockOverlayText,MenuProvider{

    public static final int NUM_SLOTS = 2;
    public static final int ENERGY_CAPACITY = 20000;
    public static final int TANK_CAPACITY = 8 * FluidType.BUCKET_VOLUME;

    public int process = 0;
    public int processMax = 0;
    public int tickEnergy = 0;
    public ItemStack result = ItemStack.EMPTY;
    public MutableEnergyStorage energyStorage = new MutableEnergyStorage(ENERGY_CAPACITY);
    private final ResettableCapability<IEnergyStorage> energyCap = registerEnergyInput(energyStorage);

    public FluidTank tank = new FluidTank(TANK_CAPACITY, r->ElectrolyzerRecipe.isValidRecipeFluid(this.getLevel(),r));
    private final ResettableCapability<IFluidHandler> tankCap = registerFluidInput(tank);
    private NonNullList<ItemStack> inventory = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
    ResettableCapability<IItemHandler> invHandler = registerCapability(
            new IEInventoryHandler(2, this, 0, new boolean[]{true, false},
                    new boolean[]{false, true})
    );

    public ElectrolyzerBlockEntity(BlockPos pos, BlockState state) {
        super(IITileTypes.ELECTROLYZER.get(), pos, state);
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
        EnergyHelper.deserializeFrom(energyStorage, nbt);
        tank.readFromNBT(nbt.getCompound("tank"));
        process = nbt.getInt("process");
        processMax = nbt.getInt("processMax");
        int[] sideConfigArray = nbt.getIntArray("sideConfig");
        for(Direction d : DirectionUtils.VALUES)
            sideConfig.put(d, IEEnums.IOSideConfig.VALUES[sideConfigArray[d.ordinal()]]);
        if (!descPacket) {
            result = ItemStack.of(nbt.getCompound("result"));
            tickEnergy = nbt.getInt("tickEnergy");
            Collections.fill(inventory, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(nbt, inventory);
        }
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
        EnergyHelper.serializeTo(energyStorage, nbt);
        nbt.put("tank", tank.writeToNBT(new CompoundTag()));
        nbt.putInt("process", process);
        nbt.putInt("processMax", processMax);
        int[] sideConfigArray = new int[6];
        for(Direction d : DirectionUtils.VALUES)
            sideConfigArray[d.ordinal()] = sideConfig.get(d).ordinal();
        nbt.putIntArray("sideConfig", sideConfigArray);
        if (!descPacket) {
            nbt.put("result", result.serializeNBT());
            nbt.putInt("tickEnergy", tickEnergy);
            ContainerHelper.saveAllItems(nbt, inventory);
        }
    }

    @Override
    public void tickServer() {
        if (!level.isClientSide) {
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
                	} else if (inventory.get(1).is(result.getItem())) {
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
        ElectrolyzerRecipe recipe = ElectrolyzerRecipe.findRecipe(this.getLevel(),inventory.get(0),ItemStack.EMPTY, tank.getFluid(),false);
        if (recipe == null)
            return null;
        if (inventory.get(1).isEmpty() || (ItemStack.isSameItem(inventory.get(1), recipe.output) &&
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

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {
        if(facing!=null&&facing.getAxis()!=this.getFacing().getClockWise().getAxis()) {
            if(capability==ForgeCapabilities.ENERGY && sideConfig.get(facing) == IEEnums.IOSideConfig.INPUT)
                return energyCap.cast();
	    	if (facing!=Direction.UP&&capability == ForgeCapabilities.FLUID_HANDLER)
	            return tankCap.cast();
	        if (capability == ForgeCapabilities.ITEM_HANDLER)
	            return invHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    public Map<Direction, IEEnums.IOSideConfig> sideConfig = new EnumMap<>(Direction.class);

    {
        for(Direction d : DirectionUtils.VALUES)
        {
            if(d == this.getFacing().getClockWise())
                sideConfig.put(d, IEEnums.IOSideConfig.INPUT);
            else
                sideConfig.put(d, IEEnums.IOSideConfig.NONE);
        }
    }

    @Nonnull
    @Override
    public IEEnums.IOSideConfig getSideConfig(@Nullable Direction facing) {
        return sideConfig.get(facing);
    }

    @Override
    public boolean toggleSide(Direction side, Player p) {
        return false;
    }

    @Override
    public Component[] getOverlayText(Player player, HitResult mop, boolean hammer)
    {
        if(hammer&& IEClientConfig.showTextOverlay.get()&&mop instanceof BlockHitResult)
        {
            BlockHitResult brtr = (BlockHitResult)mop;
            IEEnums.IOSideConfig i = sideConfig.get(brtr.getDirection());
            IEEnums.IOSideConfig j = sideConfig.get(brtr.getDirection().getOpposite());
            return TextUtils.sideConfigWithOpposite(Lib.DESC_INFO+"blockSide.connectFluid.", i, j);
        }
        return null;
    }

    @Override
    public boolean useNixieFont(Player player, HitResult mop) {
        return false;
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



    public float getGuiProgress() {
        return Mth.clamp(process/processMax, 0, 1);
    }

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return ElectrolyzerContainer.makeServer(IIMenus.ELECTROLYZER.get(), pContainerId, pPlayerInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return LangUtil.translate("block.immersiveindustry.electrolyzer");
	}
}
