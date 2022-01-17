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

package com.teammoeg.immersiveindustry.content.klin;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RotaryKilnMultiblock extends IETemplateMultiblock {
    public RotaryKilnMultiblock() {
        super(new ResourceLocation(IIMain.MODID, "multiblocks/rotary_kiln"),
                new BlockPos(1, 1, 3), new BlockPos(1, 1, 6), new BlockPos(3, 3, 7),
                () -> IIContent.IIMultiblocks.rotary_kiln.getDefaultState());

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
            renderStack = new ItemStack(IIContent.IIMultiblocks.rotary_kiln);
        transform.translate(1.5D, 1.5D, 2.5D);
        ClientUtils.mc().getItemRenderer().renderItem(
                renderStack,
                ItemCameraTransforms.TransformType.NONE,
                0xf000f0,
                OverlayTexture.NO_OVERLAY,
                transform, buffer);
    }




	@Override
    public float getManualScale() {
        return 16;
    }

    @Override
    public boolean canBeMirrored() {
        return false;
    }
}
