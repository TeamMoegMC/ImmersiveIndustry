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

import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.IIContainerData.CustomDataSlot;
import com.teammoeg.immersiveindustry.util.OutputSlot;

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

public class CrucibleContainer extends IIBaseContainer {
    CustomDataSlot<Float> process=IIContainerData.SLOT_FIXED.create(this);
    CustomDataSlot<Float> fuelProcess=IIContainerData.SLOT_FIXED.create(this);
    CustomDataSlot<Integer> temperature=IIContainerData.SLOT_INT.create(this);
    CustomDataSlot<FluidStack> tankSlot=IIContainerData.SLOT_TANK.create(this);
    CustomDataSlot<Boolean> hasPreheater=IIContainerData.SLOT_BOOL.create(this);
    FluidTank tank;
    public CrucibleContainer(MenuType<CrucibleContainer> type, int windowId, Inventory inventoryPlayer, MultiblockMenuContext<CrucibleState> te){
        super(type, windowId,inventoryPlayer.player,6);
        CrucibleState state=te.mbContext().getState();
        process.bind(()->state.recipe.getProgressRatio());
        fuelProcess.bind(()->state.burnTime*1f/state.burnTimeMax);
        temperature.bind(()->state.temperature/100);
        tank=state.tank;
        tankSlot.bind(()->tank.getFluid());
        hasPreheater.bind(()->state.hasPreheater);
        addSlots(state.inventory,inventoryPlayer);
    }
    public CrucibleContainer(MenuType<CrucibleContainer> type, int windowId, Inventory inventoryPlayer){
    	super(type, windowId,inventoryPlayer.player,6);
    	addSlots(new ItemStackHandler(6),inventoryPlayer);
    	tank=new FluidTank(14400);
    	tankSlot.bind(t->tank.setFluid(t));
    }
    public void addSlots(IItemHandlerModifiable inv,Inventory inventoryPlayer) {
    	Level l=inventoryPlayer.player.level();
        // input
        this.addSlot(new CrucibleSlot(inv, 0, 30, 12,l));
        this.addSlot(new CrucibleSlot(inv, 1, 51, 12,l));
        this.addSlot(new CrucibleSlot(inv, 2, 30, 33,l));
        this.addSlot(new CrucibleSlot(inv, 3, 51, 33,l) );
        // input fuel
        this.addSlot(new SlotItemHandler(inv, 4, 80, 51) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return CrucibleRecipe.getFuelTime(l,itemStack) > 0;
            }
        });
        this.addSlot(new OutputSlot(inv, 5, 109, 12)); 
        super.addPlayerInventory(inventoryPlayer, 8, 84, 142);
    }
    static class CrucibleSlot extends SlotItemHandler{
    	Level l;
		public CrucibleSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,Level l) {
			
			super(itemHandler, index, xPosition, yPosition);
			this.l=l;
		}
		 @Override
         public boolean mayPlace(ItemStack itemStack) {
             return CrucibleRecipe.isValidInput(l,itemStack);
         }
    }
}

