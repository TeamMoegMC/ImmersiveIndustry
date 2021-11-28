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

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.common.blocks.metal.BlastFurnacePreheaterTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Function;

public class CrucibleScreen extends IEContainerScreen<CrucibleContainer> {
    private static final Function<BlastFurnacePreheaterTileEntity, Boolean> PREHEATER_ACTIVE = (tile) -> tile.active;
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/crucible.png");
    private CrucibleTileEntity tile;

    public CrucibleScreen(CrucibleContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.tile = container.tile;
        clearIntArray(tile.guiData);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partial) {
        super.render(transform, mouseX, mouseY, partial);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partial, int x, int y) {
        ClientUtils.bindTexture(TEXTURE);
        this.blit(transform, guiLeft, guiTop, 0, 0, xSize, ySize);
        if (tile.temperature > 0) {
            int temp = tile.temperature;
            int bar = temp / 30;
            this.blit(transform, guiLeft + 12, guiTop + 67 - bar, 177, 83 - bar, 5, bar);
        }
        if (tile.burnTime > 0) {
            int h = (int) (tile.burnTime / 46.0f);
            this.blit(transform, guiLeft + 84, guiTop + 47 - h, 179, 1 + 12 - h, 9, h);
        }
        if (tile.processMax > 0 && tile.process > 0) {
            int h = (int) (21 * (tile.process / (float) tile.processMax));
            this.blit(transform, guiLeft + 76, guiTop + 14, 204, 15, 21 - h, 15);
        }
        if ((Boolean) tile.getFromPreheater(PREHEATER_ACTIVE, false)) {
            this.blit(transform, this.guiLeft + 28, this.guiTop + 57, 199, 32, 12, 11);
        }
    }


}
