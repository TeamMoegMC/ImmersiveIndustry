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

import static com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerBlockEntity.*;

import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.IIContainerData.CustomDataSlot;
import com.teammoeg.immersiveindustry.util.OutputSlot;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ElectrolyzerContainer extends IIBaseContainer {
    public MutableEnergyStorage energyStorage;
    public FluidTank tank;
    public final CustomDataSlot<FluidStack> guiTank=IIContainerData.SLOT_TANK.create(this);
    public final CustomDataSlot<Float> guiProgress=IIContainerData.SLOT_FIXED.create(this);
    public final CustomDataSlot<Integer> energy=IIContainerData.SLOT_INT.create(this);
    public ElectrolyzerContainer(MenuType<?> type, int id, Inventory invPlayer,ElectrolyzerBlockEntity be) {
    	this(type,id,invPlayer,new ItemStackHandler(be.inventory));
    	energyStorage=be.energyStorage;
    	tank=be.tank;
    	energy.bind(energyStorage::getEnergyStored);
    	guiTank.bind(tank::getFluid);
    	guiProgress.bind(be::getGuiProgress);
    }

    public ElectrolyzerContainer(MenuType<?> type, int id, Inventory invPlayer) {
        this(type, id, invPlayer, new ItemStackHandler(NUM_SLOTS));
        this.energyStorage=new MutableEnergyStorage(ENERGY_CAPACITY);
        this.tank=new FluidTank(TANK_CAPACITY);
        guiTank.bind(tank::setFluid);
        energy.bind(energyStorage::setStoredEnergy);
    }

    protected ElectrolyzerContainer(MenuType<?> type, int id, Inventory invPlayer,IItemHandlerModifiable inv) {
        super(type,id,invPlayer.player,2);
        Level level = invPlayer.player.level();
        // input
        this.addSlot(new SlotItemHandler(inv, 0, 51, 34) {
        	@Override
            public boolean mayPlace(ItemStack stack) {
                return ElectrolyzerRecipe.isValidRecipeInput(level,stack);
            }
        });
        // output
        this.addSlot(new OutputSlot(inv, 1, 107, 34));
 
        super.addPlayerInventory(invPlayer, 8, 84, 142);
    }



}

