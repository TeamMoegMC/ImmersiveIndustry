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

package com.teammoeg.immersiveindustry.content.crucible;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterBlockEntity;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.content.IActiveState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tileentity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class CrucibleBlockEntity extends MultiblockPartBlock<CrucibleBlockEntity> implements IIEInventory,
        IActiveState, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessBE, IEBlockInterfaces.IBlockBounds, IMultiblockState {


    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        tank[0].readFromNBT(nbt.getCompound("tank"));
        temperature=nbt.getInt("temperature");
        burnTime=nbt.getInt("burntime");
        if (!descPacket) {
            process = nbt.getInt("process");
            processMax = nbt.getInt("processMax");
            resultFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("result_fluid"));
            ItemStackHelper.loadAllItems(nbt, inventory);
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("tank", tank[0].writeToNBT(new CompoundNBT()));
        nbt.putInt("temperature", temperature);
        nbt.putInt("burntime", burnTime);
        if (!descPacket) {
            nbt.putInt("process", process);
            nbt.putInt("processMax", processMax);
            nbt.put("result_fluid", resultFluid.writeToNBT(new CompoundNBT()));
            ItemStackHelper.saveAllItems(nbt, inventory);
        }
    }
    @Override
    public void tick() {
        checkForNeedlessTicking();
        if (!isDummy()) {
            if (!world.isRemote && formed) {

            }
            if (world != null && formed && getIsActive()) {
                
            }
        }
    }



    public int getFanSpeed(){
        return 128;
    }
}
