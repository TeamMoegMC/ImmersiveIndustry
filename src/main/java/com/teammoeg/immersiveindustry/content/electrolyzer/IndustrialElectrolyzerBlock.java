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

import com.teammoeg.immersiveindustry.IIMain;

import blusunrize.immersiveengineering.common.blocks.IEMultiblockBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class IndustrialElectrolyzerBlock extends IEMultiblockBlock<IndustrialElectrolyzerTileEntity> {

    public IndustrialElectrolyzerBlock(String name, RegistryObject type) {
        super(name, Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(4.0F, 40.0F).notSolid(), type);
        
    }









	@Override
    public ResourceLocation createRegistryName() {
        return new ResourceLocation(IIMain.MODID, name);
    }
}

