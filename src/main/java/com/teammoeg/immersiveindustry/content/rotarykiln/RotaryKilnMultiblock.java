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

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.utils.BasicClientProperties;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RotaryKilnMultiblock extends IETemplateMultiblock {
    public RotaryKilnMultiblock() {
        super(new ResourceLocation(IIMain.MODID, "multiblocks/rotary_kiln"),
                new BlockPos(1, 1, 3), new BlockPos(1, 1, 6), new BlockPos(3, 3, 7),
                IIContent.IIMultiblocks.ROTARY_KILN);

    }




    @OnlyIn(Dist.CLIENT)
    private static ItemStack renderStack;

    @Override
	public void initializeClient(Consumer<MultiblockManualData> consumer) {
		consumer.accept(new BasicClientProperties(this) {


			@Override
		    @OnlyIn(Dist.CLIENT)
		    public boolean canRenderFormedStructure() {
		        return true;
		    }

			@Override
			 public void renderFormedStructure(PoseStack transform, MultiBufferSource bufferSource) {
				transform.pushPose();
		        transform.translate(1.5D, 1.5D, 1.5D);
		        super.renderFormedStructure(transform, bufferSource);
		        transform.popPose();
		    }
			
		});
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
