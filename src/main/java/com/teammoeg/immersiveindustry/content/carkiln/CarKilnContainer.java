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

import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import blusunrize.immersiveengineering.common.gui.IESlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.entity.player.Inventory;

public class CarKilnContainer extends IEBaseContainerOld<CarKilnBlockEntity> {

    public CarKilnContainer(int id, Inventory inventoryPlayer, CarKilnBlockEntity tile) {
        super(tile, id);

        // input
        for (int i = 0; i < 4; ++i) {
            this.addSlot(new IESlot(this, this.inv, i, 35 + i % 2 * 18, 21 + i / 2 * 18) {
                @Override
                public boolean isItemValid(ItemStack itemStack) {
                    return CarKilnRecipe.isValidInput(itemStack);
                }
            });
        }
        // output
        for (int i = 0; i < 5; ++i) {
            this.addSlot(new IESlot.Output(this, this.inv, 4 + i, 96 + i % 3 * 18, 50 + i / 3 * 18));
        }

        this.slotCount = 9;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 96 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 154));
    }
}

