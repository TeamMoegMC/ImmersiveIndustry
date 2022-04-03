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

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.gui.IESlot;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerRecipe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class CarKilnContainer extends IEBaseContainer<CarKilnTileEntity> {

    public CarKilnContainer(int id, PlayerInventory inventoryPlayer, CarKilnTileEntity tile) {
        super(tile, id);

        // input
        for (int i = 0; i < 4; ++i) {
            this.addSlot(new IESlot(this, this.inv, i, 35 + i % 2 * 18, 15 + i / 2 * 18) {
                @Override
                public boolean isItemValid(ItemStack itemStack) {
                    return ElectrolyzerRecipe.isValidRecipeInput(itemStack);
                }
            });
        }
        // output
        for (int i = 0; i < 5; ++i) {
            this.addSlot(new IESlot.Output(this, this.inv, 4 + i, 96 + i % 3 * 18, 50 + i / 3 * 18));
        }

        this.slotCount = 4;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 96 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 154));

    }
}

