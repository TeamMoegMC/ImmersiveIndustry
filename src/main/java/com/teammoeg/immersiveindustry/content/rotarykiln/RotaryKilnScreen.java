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

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.teammoeg.immersiveindustry.IIMain;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RotaryKilnScreen extends IEContainerScreen<RotaryKilnContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png");

    public RotaryKilnScreen(RotaryKilnContainer container, Inventory inv, Component title) {
        super(container, inv, title,TEXTURE);
        this.height=178;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
	protected List<InfoArea> makeInfoAreas() {
		return super.makeInfoAreas();
	}

	@Override
	protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray) {
		// TODO Auto-generated method stub
		super.gatherAdditionalTooltips(mouseX, mouseY, addLine, addGray);
	}

	@Override
    public void render(GuiMatrix transform, int mouseX, int mouseY, float partial) {
        super.render(transform, mouseX, mouseY, partial);
        List<ITextComponent> tooltip = new ArrayList<>();

        GuiHelper.handleGuiTank(transform, tile.tankout[0], guiLeft + 133, guiTop + 26, 16, 47, 196, 0, 20, 51, mouseX, mouseY, TEXTURE, tooltip);
        if (mouseX >= this.guiLeft + 157 && mouseX < this.guiLeft + 163 && mouseY > this.guiTop + 26 && mouseY < this.guiTop + 71) {
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

        if (tile.processMax > 0) {
            int w = (int) (38 * (tile.process / (float) tile.processMax));
            this.blit(transform, guiLeft + 88, guiTop + 44, 178, 59, 38 - w, 16);
        }
        GuiHelper.handleGuiTank(transform, tile.tankout[0], guiLeft + 133, guiTop + 26, 16, 47, 196, 0, 20, 51, x, y, TEXTURE, null);
        int stored = (int) (46.0F * ((float) this.tile.getEnergyStored(null) / (float) this.tile.getMaxEnergyStored(null)));
        this.fillGradient(transform, this.guiLeft + 157, this.guiTop + 26 + (46 - stored), this.guiLeft + 164, this.guiTop + 72, -4909824, -10482944);

    }


}
