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

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIMain;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class IndustrialElectrolyzerScreen extends IEContainerScreen<IndustrialElectrolyzerContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png");
    private IndustrialElectrolyzerBlockEntity tile;

    public IndustrialElectrolyzerScreen(IndustrialElectrolyzerContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.tile = container.tile;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(MatrixStack transform, int mouseX, int mouseY, float partial) {
        super.render(transform, mouseX, mouseY, partial);
        List<ITextComponent> tooltip = new ArrayList<>();
        GuiHelper.handleGuiTank(transform, tile.tank[0], guiLeft + 10, guiTop + 25, 16, 47, 196, 0, 20, 51, mouseX, mouseY, TEXTURE, tooltip);
        GuiHelper.handleGuiTank(transform, tile.tank[1], guiLeft + 132, guiTop + 25, 16, 47, 196, 0, 20, 51, mouseX, mouseY, TEXTURE, tooltip);
        if (mouseX >= this.guiLeft + 156 && mouseX < this.guiLeft + 162 && mouseY > this.guiTop + 25 && mouseY < this.guiTop + 70) {
            tooltip.add(new StringTextComponent(this.tile.getEnergyStored((Direction) null) + "/" + this.tile.getMaxEnergyStored((Direction) null) + " IF"));
        }
        if (!tooltip.isEmpty()) {
            GuiUtils.drawHoveringText(transform, tooltip, mouseX, mouseY, width, height, -1, font);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partial, int x, int y) {
        ClientUtils.bindTexture(TEXTURE);
        this.blit(transform, guiLeft, guiTop, 0, 0, xSize, ySize);
        GuiHelper.handleGuiTank(transform, tile.tank[0], guiLeft + 10, guiTop + 25, 16, 47, 196, 0, 20, 51, x, y, TEXTURE, null);
        GuiHelper.handleGuiTank(transform, tile.tank[1], guiLeft + 132, guiTop + 25, 16, 47, 196, 0, 20, 51, x, y, TEXTURE, null);
        if (tile.processMax > 0 && tile.process > 0) {
            int h = (int) (21 * (tile.process / (float) tile.processMax));
            this.blit(transform, guiLeft + 77, guiTop + 40, 178, 57, 21 - h, 15);
        }
        int stored = (int) (46.0F * ((float) this.tile.getEnergyStored(null) / (float) this.tile.getMaxEnergyStored(null)));
        this.fillGradient(transform, this.guiLeft + 156, this.guiTop + 25 + (46 - stored), this.guiLeft + 163, this.guiTop + 71, -4909824, -10482944);
    }


}
