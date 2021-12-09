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

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.items.IEItems;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class IndustrialElectrolyzerContainer extends IEBaseContainer<IndustrialElectrolyzerTileEntity> {

    public IndustrialElectrolyzerContainer(int id, PlayerInventory inventoryPlayer, IndustrialElectrolyzerTileEntity tile) {
        super(inventoryPlayer, tile, id);

        // input
        this.addSlot(new IESlot(this, this.inv, 0, 34, 39) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return ElectrolyzerRecipe.isValidRecipeInput(itemStack);
            }
        });
        this.addSlot(new IESlot(this, this.inv, 1, 52, 39) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return ElectrolyzerRecipe.isValidRecipeInput(itemStack);
            }
        });
        // output
        this.addSlot(new IESlot.Output(this, this.inv, 2, 108, 39));
        slotCount = 3;

        this.addSlot(new IESlot(this, this.inv, 3, 34, 10) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem() == IEItems.Misc.graphiteElectrode;
            }
        });
        this.addSlot(new IESlot(this, this.inv, 4, 52, 10) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem() == IEItems.Misc.graphiteElectrode;
            }
        });

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 142));

    }
}

