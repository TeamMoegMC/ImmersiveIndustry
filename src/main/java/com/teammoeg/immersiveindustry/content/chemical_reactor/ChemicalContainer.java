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

package com.teammoeg.immersiveindustry.content.chemical_reactor;

import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.IIContainerData.CustomDataSlot;
import com.teammoeg.immersiveindustry.util.OutputSlot;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu.MultiblockMenuContext;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ChemicalContainer extends IIBaseContainer {
    CustomDataSlot<Float> process=IIContainerData.SLOT_FIXED.create(this);
    //integral process for gui animation
    CustomDataSlot<Integer> process_num=IIContainerData.SLOT_INT.create(this);
    CustomDataSlot<FluidStack>[] tanks=new CustomDataSlot[6];
    {
    	for(int i=0;i<tanks.length;i++) {
    		tanks[i]=IIContainerData.SLOT_TANK.create(this);
    	}
    }
    FluidTank[] tank=new FluidTank[6];
    CustomDataSlot<Integer> energySlot=IIContainerData.SLOT_INT.create(this);
    CustomDataSlot<Boolean> active=IIContainerData.SLOT_BOOL.create(this);
	MutableEnergyStorage energy;
    public ChemicalContainer(MenuType<ChemicalContainer> type, int windowId, Inventory inventoryPlayer, MultiblockMenuContext<ChemicalState> te){
        super(type, windowId,inventoryPlayer.player,6);
        ChemicalState state=te.mbContext().getState();
        process.bind(()->state.recipe.getProgressRatio());
        process_num.bind(state.recipe::getProcess);
        int num=0;
        for(FluidTank curtank:state.inTank) {
        	int i=num++;
        	tank[i]=curtank;
        	tanks[i].bind(curtank::getFluid);
        }
        for(FluidTank curtank:state.outTank) {
        	int i=num++;
        	tank[i]=curtank;
        	tanks[i].bind(curtank::getFluid);
        }
        addSlots(state.inventory,inventoryPlayer);
        energy=state.energyStorage;
        energySlot.bind(energy::getEnergyStored);
        active.bind(()->state.active);
    }
    public ChemicalContainer(MenuType<ChemicalContainer> type, int windowId, Inventory inventoryPlayer){
    	super(type, windowId,inventoryPlayer.player,6);
    	addSlots(new ItemStackHandler(6),inventoryPlayer);

        for(int i=0;i<tanks.length;i++) {
        	tank[i]=new FluidTank(7000);
        	tanks[i].bind(tank[i]::setFluid);
        }
        energy=new MutableEnergyStorage(32000);
        energySlot.bind(energy::setStoredEnergy);
        
    }
    public void addSlots(IItemHandlerModifiable inv,Inventory inventoryPlayer) {
    	Level l=inventoryPlayer.player.level();
        // input
        this.addSlot(new ChemicalSlot(inv, 0, 11, 77,l));
        this.addSlot(new ChemicalSlot(inv, 1, 34, 77,l));
        this.addSlot(new ChemicalSlot(inv, 2, 57, 77,l));

        this.addSlot(new OutputSlot(inv, 3, 181, 77)); 
        this.addSlot(new OutputSlot(inv, 4, 205, 77)); 
        this.addSlot(new OutputSlot(inv, 5, 229, 77)); 
        super.addPlayerInventory(inventoryPlayer, 48, 112, 170);
    }
    static class ChemicalSlot extends SlotItemHandler{
    	Level l;
		public ChemicalSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,Level l) {
			
			super(itemHandler, index, xPosition, yPosition);
			this.l=l;
		}
		 @Override
         public boolean mayPlace(ItemStack itemStack) {
             return ChemicalRecipe.isValidInput(l,itemStack);
         }
    }
}

