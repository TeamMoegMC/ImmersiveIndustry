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

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SteamTurbineMultiblock extends IETemplateMultiblock {
    public SteamTurbineMultiblock() {
        super(new ResourceLocation(IIMain.MODID, "multiblocks/steam_turbine"),
                new BlockPos(1, 1, 3), new BlockPos(1, 1, 6), new BlockPos(3, 3, 7),
                () -> IIContent.IIMultiblocks.steam_turbine.getDefaultState());
        
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderFormedStructure() {
        return true;
    }

    @Override
	public BlockPos multiblockToModelPos(BlockPos posInMultiblock) {
		return posInMultiblock.subtract(new Vector3i(0,1,0));
	}
	@OnlyIn(Dist.CLIENT)
	private static ItemStack renderStack;
	@Override
    @OnlyIn(Dist.CLIENT)
    public void renderFormedStructure(MatrixStack transform, IRenderTypeBuffer buffer) {
		if(renderStack==null)
			renderStack = new ItemStack(IIContent.IIMultiblocks.steam_turbine);
		transform.translate(2.5, 1.5, 1.5);
		transform.rotate(new Quaternion(0, 45, 0, true));
		transform.rotate(new Quaternion(-20, 0, 0, true));
		transform.scale(5.5F, 5.5F, 5.5F);

		ClientUtils.mc().getItemRenderer().renderItem(
				renderStack,
				TransformType.GUI,
				0xf000f0,
				OverlayTexture.NO_OVERLAY,
				transform, buffer
		);
    }

    @Override
    public float getManualScale() {
        return 16;
    }

    @Override
    public boolean canBeMirrored() {
        return false;
    }

    @Override
    public Direction transformDirection(Direction original) {
        return original.getOpposite();
    }

    @Override
    public Direction untransformDirection(Direction transformed) {
        return transformed.getOpposite();
    }
}
