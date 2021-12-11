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

package com.teammoeg.immersiveindustry.content.crucible;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrucibleMultiblock extends IETemplateMultiblock {
    public CrucibleMultiblock() {
        super(new ResourceLocation(IIMain.MODID, "multiblocks/crucible"),
                new BlockPos(1, 1, 1), new BlockPos(1, 1, 2), new BlockPos(3, 4, 3),
                () -> IIContent.IIMultiblocks.crucible.getDefaultState());
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderFormedStructure() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private static ItemStack renderStack;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderFormedStructure(MatrixStack transform, IRenderTypeBuffer buffer) {
        if (renderStack == null)
            renderStack = new ItemStack(IIContent.IIMultiblocks.crucible);
        transform.translate(1.5D, 1.5D, 1.5D);
        ClientUtils.mc().getItemRenderer().renderItem(
                renderStack,
                ItemCameraTransforms.TransformType.NONE,
                0xf000f0,
                OverlayTexture.NO_OVERLAY,
                transform, buffer);

    }

    @Override
    public boolean canBeMirrored() {
        return false;
    }

    @Override
    public float getManualScale() {
        return 16;
    }


}
