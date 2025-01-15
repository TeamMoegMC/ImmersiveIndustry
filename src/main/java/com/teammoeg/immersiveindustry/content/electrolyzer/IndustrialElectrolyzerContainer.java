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

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu.MultiblockMenuContext;

import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.OutputSlot;
import com.teammoeg.immersiveindustry.util.IIContainerData.CustomDataSlot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class IndustrialElectrolyzerContainer extends IIBaseContainer {
	CustomDataSlot<FluidStack> tankSlot1=IIContainerData.SLOT_TANK.create(this);
	CustomDataSlot<FluidStack> tankSlot2=IIContainerData.SLOT_TANK.create(this);
	CustomDataSlot<Float> process=IIContainerData.SLOT_FIXED.create(this);
	CustomDataSlot<Integer> energySlot=IIContainerData.SLOT_INT.create(this);
	FluidTank[] tank;
	MutableEnergyStorage energy;
	public static final TagKey<Item> Electrode_Tag=ItemTags.create(new ResourceLocation(IIMain.MODID,"electrodes"));
    public IndustrialElectrolyzerContainer(MenuType<IndustrialElectrolyzerContainer> type, int windowId, Inventory inventoryPlayer, MultiblockMenuContext<IndustrialElectrolyzerState> te){
        super(type, windowId,inventoryPlayer.player,5);
        IndustrialElectrolyzerState state=te.mbContext().getState();
        process.bind(()->state.recipe.getProgressRatio());
        tank=state.tank;
        tankSlot1.bind(()->tank[0].getFluid());
        tankSlot2.bind(()->tank[1].getFluid());
        energy=state.energyStorage;
        energySlot.bind(()->energy.getEnergyStored());
        addSlots(state.inventory,inventoryPlayer);
    }
    public IndustrialElectrolyzerContainer(MenuType<IndustrialElectrolyzerContainer> type, int windowId, Inventory inventoryPlayer){
    	super(type, windowId,inventoryPlayer.player,5);
    	addSlots(new ItemStackHandler(5),inventoryPlayer);
    	tank=new FluidTank[] {new FluidTank(16000),new FluidTank(16000)};
    	tankSlot1.bind(t->tank[0].setFluid(t));
    	tankSlot2.bind(t->tank[1].setFluid(t));
    	energy= new MutableEnergyStorage(32000);
    	energySlot.bind(t->energy.setStoredEnergy(t));
    }
    public void addSlots(IItemHandlerModifiable inv,Inventory inventoryPlayer) {
        // input
        this.addSlot(new InputSlot(inv, 0, 34, 39,inventoryPlayer.player.level()) );
        this.addSlot(new InputSlot(inv, 1, 52, 39,inventoryPlayer.player.level()) );

        this.addSlot(new ElectrodeSlot(inv, 2, 34, 10));
        this.addSlot(new ElectrodeSlot(inv, 3, 52, 10) );
        this.addSlot(new OutputSlot(inv, 4, 108, 39));
    }
    public static class ElectrodeSlot extends SlotItemHandler{

		public ElectrodeSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
			// TODO Auto-generated constructor stub
		}
		
        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Electrode_Tag);
        }
        
    }
    public static class InputSlot extends SlotItemHandler{
    	Level l;
		public InputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,Level l) {
			super(itemHandler, index, xPosition, yPosition);
			this.l=l;
			// TODO Auto-generated constructor stub
		}
		
		@Override
        public boolean mayPlace(ItemStack itemStack) {
            return ElectrolyzerRecipe.isValidRecipeInput(l,itemStack);
        }
        
    }
}

