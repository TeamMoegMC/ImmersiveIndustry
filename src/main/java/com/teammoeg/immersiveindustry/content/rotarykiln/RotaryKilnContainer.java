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
import com.teammoeg.immersiveindustry.util.IIContainerData.FHDataSlot;

import blusunrize.immersiveengineering.common.gui.IEContainerMenu.MultiblockMenuContext;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class RotaryKilnContainer extends IIBaseContainer {
	FHDataSlot<FluidStack> tankSlot=IIContainerData.SLOT_TANK.create(this);
	FHDataSlot<Integer> process=IIContainerData.SLOT_INT.create(this);
	FHDataSlot<Integer> processMax=IIContainerData.SLOT_INT.create(this);
    public RotaryKilnContainer(MenuType<RotaryKilnContainer> type, int windowId, Inventory inventoryPlayer, MultiblockMenuContext<RotaryKilnState> te){
        super(type, windowId,inventoryPlayer.player,5);
        RotaryKilnState state=te.mbContext().getState();
        process.bind(()->{
        	if(state.processes[1]!=null) 
        		return state.processes[1].process;
        	if(state.processes[0]!=null)
        		return state.processes[0].process;
        	return 0;
        });
        processMax.bind(()->{
        	if(state.processes[1]!=null) 
        		return state.processes[1].processMax;
        	if(state.processes[0]!=null)
        		return state.processes[0].processMax;
        	return 0;
        });
        tankSlot.bind(()->state.tankout.getFluid());
        addSlots(state.inventory,inventoryPlayer);
    }
    public RotaryKilnContainer(MenuType<RotaryKilnContainer> type, int windowId, Inventory inventoryPlayer){
    	super(type, windowId,inventoryPlayer.player,5);
    	addSlots(new ItemStackHandler(5),inventoryPlayer);
    }
    public void addSlots(IItemHandlerModifiable inv,Inventory inventoryPlayer) {
        // input
        this.addSlot(new SlotItemHandler(inv, 0, 12, 40){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return RotaryKilnRecipe.isValidRecipeInput(inventoryPlayer.player.level(), itemStack);
            }
        });

        

        this.addSlot(new RotarySlot(inv, 1, 38, 40));
        this.addSlot(new RotarySlot(inv, 2, 64, 40));

        // output
        this.addSlot(new OutputSlot(inv, 3, 94, 63));
        this.addSlot(new OutputSlot(inv, 4, 112, 63));
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 96 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 154));
    }

    public static class RotarySlot extends SlotItemHandler {
        public RotarySlot(IItemHandlerModifiable inv, int id, int x, int y) {
            super( inv, id, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            return false;
        }
    }
    public static class OutputSlot extends SlotItemHandler {
        public OutputSlot(IItemHandlerModifiable inv, int id, int x, int y) {
            super( inv, id, x, y);
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            return false;
        }
    }
}

