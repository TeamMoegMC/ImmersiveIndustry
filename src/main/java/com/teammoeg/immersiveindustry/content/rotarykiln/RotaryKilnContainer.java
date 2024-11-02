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

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.gui.IESlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class RotaryKilnContainer extends IEBaseContainer<RotaryKilnBlockEntity> {

    public RotaryKilnContainer(int id, PlayerInventory inventoryPlayer, RotaryKilnBlockEntity tile) {
        super(tile, id);

        // input
        this.addSlot(new IESlot(this, this.inv, 0, 12, 40) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return RotaryKilnRecipe.isValidRecipeInput(itemStack);
            }
        });

        this.slotCount = 4;

        this.addSlot(new RotarySlot(this, this.inv, 1, 38, 40));
        this.addSlot(new RotarySlot(this, this.inv, 2, 64, 40));

        // output
        this.addSlot(new IESlot.Output(this, this.inv, 3, 94, 63));
        this.addSlot(new IESlot.Output(this, this.inv, 4, 112, 63));
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 96 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 154));
    }

    public static class RotarySlot extends IESlot {
        public RotarySlot(Container container, IInventory inv, int id, int x, int y) {
            super(container, inv, id, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack itemStack) {
            return false;
        }

        @Override
        public boolean canTakeStack(PlayerEntity playerIn) {
            return false;
        }
    }
}

