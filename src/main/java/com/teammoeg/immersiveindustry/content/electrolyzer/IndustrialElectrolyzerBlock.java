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

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEMultiblockBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;

public class IndustrialElectrolyzerBlock extends IEMultiblockBlock<IndustrialElectrolyzerTileEntity> {

    public IndustrialElectrolyzerBlock(String name, RegistryObject type) {
        super(name, Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(4.0F, 40.0F).notSolid(), type);
        
    }





/*	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
		Vector3i v3i=state.get(IEProperties.FACING_HORIZONTAL).getDirectionVec();
		return VoxelShapes.create(-v3i.getX()-1, -1, -v3i.getZ()-1,v3i.getX()+1,1,v3i.getZ()+1);
	}*/





	@Override
    public ResourceLocation createRegistryName() {
        return new ResourceLocation(IIMain.MODID, name);
    }
}

