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

import static com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerBlockEntity.*;

import com.teammoeg.immersiveindustry.util.OutputSlot;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ElectrolyzerContainer extends IEContainerMenu {
    public final EnergyStorage energyStorage;
    public final FluidTank tank;
    public final GetterAndSetter<Float> guiProgress;

    public static ElectrolyzerContainer makeServer(MenuType<?> type, int id, Inventory invPlayer,
                                                   ElectrolyzerBlockEntity be) {
        return new ElectrolyzerContainer(
                blockCtx(type, id, be), invPlayer, new ItemStackHandler(be.getInventory()),
                be.energyStorage, be.tank,
                GetterAndSetter.getterOnly(be::getGuiProgress)
        );
    }

    public static ElectrolyzerContainer makeClient(MenuType<?> type, int id, Inventory invPlayer) {
        return new ElectrolyzerContainer(
                clientCtx(type, id), invPlayer, new ItemStackHandler(NUM_SLOTS),
                new MutableEnergyStorage(ENERGY_CAPACITY), new FluidTank(TANK_CAPACITY),
                GetterAndSetter.standalone(0f)
        );
    }

    public ElectrolyzerContainer(MenuContext ctx, Inventory inventoryPlayer, IItemHandlerModifiable inv,
                                 MutableEnergyStorage energyStorage, FluidTank tank,
                                 GetterAndSetter<Float> guiProgress) {
        super(ctx);
        this.energyStorage = energyStorage;
        this.tank = tank;
        this.guiProgress = guiProgress;
        Level level = inventoryPlayer.player.level();
        // input
        this.addSlot(new SlotItemHandler(inv, 0, 51, 34) {
        	@Override
            public boolean mayPlace(ItemStack stack) {
                return ElectrolyzerRecipe.isValidRecipeInput(level,stack);
            }
        });
        // output
        this.addSlot(new OutputSlot(inv, 1, 107, 34));
        this.ownSlotCount = NUM_SLOTS;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 142));

        addGenericData(GenericContainerData.energy(energyStorage));
        addGenericData(GenericContainerData.fluid(tank));
        addGenericData(new GenericContainerData<>(GenericDataSerializers.FLOAT, guiProgress));
    }



}

