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

package com.teammoeg.immersiveindustry.content.rotarykiln;

import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.IIContainerData.CustomDataSlot;
import com.teammoeg.immersiveindustry.util.NoAccessSlot;
import com.teammoeg.immersiveindustry.util.OutputSlot;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu.MultiblockMenuContext;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RotaryKilnContainer extends IIBaseContainer {
	CustomDataSlot<FluidStack> tankSlot=IIContainerData.SLOT_TANK.create(this);
	CustomDataSlot<Float> process=IIContainerData.SLOT_FIXED.create(this);
	CustomDataSlot<Integer> energySlot=IIContainerData.SLOT_INT.create(this);
	FluidTank tank;
	MutableEnergyStorage energy;
    public RotaryKilnContainer(MenuType<RotaryKilnContainer> type, int windowId, Inventory inventoryPlayer, MultiblockMenuContext<RotaryKilnState> te){
        super(type, windowId,inventoryPlayer.player,5);
        RotaryKilnState state=te.mbContext().getState();
        process.bind(()->{
        	if(state.processes[1]!=null) 
        		return (state.processes[1].processMax-state.processes[1].process)*1f/state.processes[1].processMax;
        	if(state.processes[0]!=null)
        		return (state.processes[0].processMax-state.processes[0].process)*1f/state.processes[0].processMax;
        	return 0f;
        });
        tank=state.tankout;
        tankSlot.bind(()->tank.getFluid());
        energy=state.energyStorage;
        energySlot.bind(()->energy.getEnergyStored());
        addSlots(state.inventory,inventoryPlayer);
    }
    public RotaryKilnContainer(MenuType<RotaryKilnContainer> type, int windowId, Inventory inventoryPlayer){
    	super(type, windowId,inventoryPlayer.player,5);
    	addSlots(new ItemStackHandler(5),inventoryPlayer);
    	tank=new FluidTank(32000);
    	tankSlot.bind(t->tank.setFluid(t));
    	energy= new MutableEnergyStorage(32000);
    	energySlot.bind(t->energy.setStoredEnergy(t));
    }
    public void addSlots(IItemHandlerModifiable inv,Inventory inventoryPlayer) {
        // input
        this.addSlot(new SlotItemHandler(inv, 0, 12, 40){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return RotaryKilnRecipe.isValidRecipeInput(inventoryPlayer.player.level(), itemStack);
            }
        });

        

        this.addSlot(new NoAccessSlot(inv, 1, 38, 40));
        this.addSlot(new NoAccessSlot(inv, 2, 64, 40));

        // output
        this.addSlot(new OutputSlot(inv, 3, 94, 63));
        this.addSlot(new OutputSlot(inv, 4, 112, 63));
        super.addPlayerInventory(inventoryPlayer, 8, 96, 154);
    }
}

