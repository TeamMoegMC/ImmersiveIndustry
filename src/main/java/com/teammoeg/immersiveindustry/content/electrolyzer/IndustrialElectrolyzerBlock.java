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

import blusunrize.immersiveengineering.common.blocks.IEMultiblockBlock;
import blusunrize.immersiveengineering.common.util.Utils;
import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.items.ItemHandlerHelper;

public class IndustrialElectrolyzerBlock extends IEMultiblockBlock<IndustrialElectrolyzerBlockEntity> {

    public IndustrialElectrolyzerBlock(String name, RegistryObject type) {
        super(name, Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(4.0F, 40.0F).notSolid(), type);
        
    }
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult hit) {
		TileEntity te=Utils.getExistingTileEntity(world, pos);
		if(te instanceof IndustrialElectrolyzerBlockEntity) {
			IndustrialElectrolyzerBlockEntity iete=(IndustrialElectrolyzerBlockEntity) te;
			if(iete.offsetToMaster.getY()==1) {
				if(player.getHeldItem(hand).getItem().getTags().contains(IndustrialElectrolyzerContainer.Electrode_Tag)) {
					if (iete.getInventory() != null)
						for (int i = 2; i <= 3; i++) {
							if (iete.getInventory().get(i).isEmpty()) {
								iete.getInventory().set(i, ItemHandlerHelper.copyStackWithSize(player.getHeldItem(hand), 1));
								player.getHeldItem(hand).shrink(1);
								return ActionResultType.CONSUME;
							}
						}
				}
			}
		}
		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}
	@Override
    public ResourceLocation createRegistryName() {
        return new ResourceLocation(IIMain.MODID, name);
    }
}

