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

import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.AccessableFluidInfoArea;
import com.teammoeg.immersiveindustry.util.IIContainerScreen;

import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IndustrialElectrolyzerScreen extends IIContainerScreen<IndustrialElectrolyzerContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png");

	public IndustrialElectrolyzerScreen(IndustrialElectrolyzerContainer container, Inventory inv, Component title) {
		super(container, inv, title, TEXTURE);

	}

	@Override
	protected void makeInfoAreas() {
		addInfoArea(new AccessableFluidInfoArea(menu.tank[0], new Rect2i(leftPos + 10, topPos + 25, 16, 47), 196, 0, 20, 51, background));
		addInfoArea(new AccessableFluidInfoArea(menu.tank[1], new Rect2i(leftPos + 132, topPos + 25, 16, 47), 196, 0, 20, 51, background));
		addInfoArea(new EnergyInfoArea(leftPos + 156, topPos + 25, menu.energy));
	}

	@Override
	protected void drawContainerBackgroundPre(GuiGraphics transform, float partial, int x, int y) {
		int w = (int) (21 * menu.process.getValue());
		if (w > 0) {
			transform.blit(TEXTURE, leftPos + 77, topPos + 40, 178, 57, w, 15);
		}
	}

}
