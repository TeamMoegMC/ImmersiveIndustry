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

import java.util.Arrays;
import java.util.List;

import com.teammoeg.immersiveindustry.IIMain;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RotaryKilnScreen extends IEContainerScreen<RotaryKilnContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png");

    public RotaryKilnScreen(RotaryKilnContainer container, Inventory inv, Component title) {
        super(container, inv, title,TEXTURE);
        this.imageHeight=178;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
	protected List<InfoArea> makeInfoAreas() {
		return Arrays.asList(new FluidInfoArea(menu.tank, new Rect2i(leftPos+133,topPos+26,16,47), 196, 0, 20, 51, background),
							new EnergyInfoArea(leftPos+157,topPos+26,menu.energy));
	}

    @Override
    protected void drawContainerBackgroundPre(GuiGraphics transform, float partial, int x, int y) {
    	int w = (int) (38 * menu.process.getValue());
        if (w > 0) {
        	transform.blit(TEXTURE, leftPos + 88,topPos + 44, 178, 59, 38 - w, 16);
        }
    }


}
