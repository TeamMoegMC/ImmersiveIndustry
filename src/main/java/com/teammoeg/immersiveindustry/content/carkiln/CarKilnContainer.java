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

package com.teammoeg.immersiveindustry.content.carkiln;

import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerContainer;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerState;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerContainer.ElectrodeSlot;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerContainer.InputSlot;
import com.teammoeg.immersiveindustry.util.IIBaseContainer;
import com.teammoeg.immersiveindustry.util.IIContainerData;
import com.teammoeg.immersiveindustry.util.OutputSlot;
import com.teammoeg.immersiveindustry.util.IIContainerData.CustomDataSlot;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu.MultiblockMenuContext;
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

public class CarKilnContainer extends IIBaseContainer {
	CustomDataSlot<FluidStack> tankSlot=IIContainerData.SLOT_TANK.create(this);
	CustomDataSlot<Float> process=IIContainerData.SLOT_FIXED.create(this);
	CustomDataSlot<Integer> energySlot=IIContainerData.SLOT_INT.create(this);
	FluidTank tank;
	MutableEnergyStorage energy;
	public static final TagKey<Item> Electrode_Tag=ItemTags.create(new ResourceLocation(IIMain.MODID,"electrodes"));
    public CarKilnContainer(MenuType<CarKilnContainer> type, int windowId, Inventory inventoryPlayer, MultiblockMenuContext<CarKilnState> te){
        super(type, windowId,inventoryPlayer.player,9);
        CarKilnState state=te.mbContext().getState();
        process.bind(()->state.recipe.getProgressRatio());
        tank=state.tank;
        tankSlot.bind(()->tank.getFluid());
        energy=state.energyStorage;
        energySlot.bind(()->energy.getEnergyStored());
        addSlots(state.inventory,inventoryPlayer);
    }
    public CarKilnContainer(MenuType<CarKilnContainer> type, int windowId, Inventory inventoryPlayer){
    	super(type, windowId,inventoryPlayer.player,9);
    	addSlots(new ItemStackHandler(9),inventoryPlayer);
    	tank=new FluidTank(16000);
    	tankSlot.bind(t->tank.setFluid(t));
    	energy= new MutableEnergyStorage(32000);
    	energySlot.bind(t->energy.setStoredEnergy(t));
    }
    public void addSlots(IItemHandlerModifiable inv,Inventory inventoryPlayer) {
    	Level l=inventoryPlayer.player.level();
        // input
        for (int i = 0; i < 4; ++i) {
            this.addSlot(new InputSlot(inv, i, 35 + i % 2 * 18, 21 + i / 2 * 18,l));
        }
        for (int i = 0; i < 5; ++i) {
            this.addSlot(new OutputSlot(inv, 4 + i, 96 + i % 3 * 18, 50 + i / 3 * 18));
        }
        super.addPlayerInventory(inventoryPlayer, 8, 96, 154);
    }
    private static class InputSlot extends SlotItemHandler{
    	Level l;
		public InputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,Level l) {
			super(itemHandler, index, xPosition, yPosition);
			this.l=l;
		}
		 @Override
         public boolean mayPlace(ItemStack itemStack) {
             return CarKilnRecipe.isValidInput(l,itemStack);
         }
    }
}

