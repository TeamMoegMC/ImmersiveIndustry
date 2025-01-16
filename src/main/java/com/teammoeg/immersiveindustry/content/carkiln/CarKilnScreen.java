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

package com.teammoeg.immersiveindustry.content.carkiln;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.teammoeg.immersiveindustry.IIMain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarKilnScreen extends IEContainerScreen<CarKilnContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/car_kiln.png");

    public CarKilnScreen(CarKilnContainer container, Inventory inv, Component title) {
        super(container, inv, title,TEXTURE);
        this.height = 178;
    }

    @Override
    public void init() {
        super.init();
    }
    @Override
	protected List<InfoArea> makeInfoAreas() {
		return Arrays.asList(new FluidInfoArea(menu.tank, new Rect2i(leftPos+10,topPos+26,16,47), 196, 0, 20, 51, background),
							new EnergyInfoArea(leftPos+157,topPos+26,menu.energy));
	}

    @Override
	protected void drawContainerBackgroundPre(GuiGraphics graphics, float partialTicks, int x, int y) {
		super.drawContainerBackgroundPre(graphics, partialTicks, x, y);
		if (menu.process.getValue()>0) {
            int w = (int) (37 * menu.process.getValue());
            graphics.blit(TEXTURE, leftPos + 83, topPos + 29, 177, 57, 37 - w, 17);
        }
	}


}
