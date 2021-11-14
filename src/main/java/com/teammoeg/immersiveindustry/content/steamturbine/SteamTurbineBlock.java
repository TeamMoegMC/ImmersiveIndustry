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

package com.teammoeg.immersiveindustry.content.steamturbine;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;

import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class SteamTurbineBlock<T extends MultiblockPartTileEntity<? super T>> extends MetalMultiblockBlock {

    public SteamTurbineBlock(String name, RegistryObject type) {
        super(name, type);
    }

    @Override
    public ResourceLocation createRegistryName() {
        return new ResourceLocation(IIMain.MODID, name);
    }
}

