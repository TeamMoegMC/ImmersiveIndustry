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

package com.teammoeg.immersiveindustry.content.chemical_reactor;

import java.util.function.Consumer;

import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.AccessableFluidInfoArea;
import com.teammoeg.immersiveindustry.util.IIContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class ChemicalScreen extends IIContainerScreen<ChemicalContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/chemical_reactor.png");

	public ChemicalScreen(ChemicalContainer container, Inventory inv, Component title) {
		super(container, inv, title, TEXTURE);
		this.imageHeight = 193;
		this.imageWidth = 256;
		this.inventoryLabelX += 40;
	}

	@Override
	protected void makeInfoAreas() {
		addInfoArea(new AccessableFluidInfoArea(menu.tank[0], new Rect2i(leftPos + 11, topPos + 23, 16, 47), 176, 104, 20, 51, TEXTURE));
		addInfoArea(new AccessableFluidInfoArea(menu.tank[1], new Rect2i(leftPos + 34, topPos + 23, 16, 47), 176, 104, 20, 51, TEXTURE));
		addInfoArea(new AccessableFluidInfoArea(menu.tank[2], new Rect2i(leftPos + 57, topPos + 23, 16, 47), 176, 104, 20, 51, TEXTURE));
		addInfoArea(new AccessableFluidInfoArea(menu.tank[3], new Rect2i(leftPos + 181, topPos + 23, 16, 47), 176, 104, 20, 51, TEXTURE));
		addInfoArea(new AccessableFluidInfoArea(menu.tank[4], new Rect2i(leftPos + 205, topPos + 23, 16, 47), 176, 104, 20, 51, TEXTURE));
		addInfoArea(new AccessableFluidInfoArea(menu.tank[5], new Rect2i(leftPos + 229, topPos + 23, 16, 47), 176, 104, 20, 51, TEXTURE));
		addInfoArea(new EnergyInfoArea(leftPos + 80, topPos + 47, menu.energy));
	}

	@Override
	protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray) {
		super.gatherAdditionalTooltips(mouseX, mouseY, addLine, addGray);
	}

	@Override
	protected void drawBackgroundTexture(GuiGraphics graphics) {
		graphics.blit(background, leftPos, topPos, 0, 0, 256, 104);
		graphics.blit(background, leftPos + 40, topPos + 104, 0, 104, 176, 89);
	}

	@Override
	protected void drawContainerBackgroundPre(GuiGraphics graphics, float partialTicks, int x, int y) {

		float process = menu.process.getValue();
		if (process > 0) {
			int h1 = 0;
			int dh1 = 0;
			int h2 = 0;
			if (process < 0.5) {
				if (process < 0.05) {
					h1 = Mth.ceil(44 * process * 20);
					h2 = 21;
				} else {
					h1 = 44;
					h2 = 21 - Mth.floor(21 * (process - 0.05) / 0.45);
				}
			} else {
				if (process < 0.55) {
					dh1 = Mth.floor(44 * (process - 0.5) * 20);
					h1 = 44 - dh1;
					h2 = 0;
				} else {
					h2 = Mth.ceil(21 * (process - 0.55) / 0.45);
				}

			}
			// graphics.blit(TEXTURE, leftPos + 18, topPos + 9, 5, 192, 102, 63);//first
			graphics.blit(TEXTURE, leftPos + 112, topPos + 33 + dh1, 95, 210 + dh1, 8, h1);// flow
			graphics.blit(TEXTURE, leftPos + 109, topPos + 57 + h2, 104, 206 + h2, 40, 21 - h2);// pot
			graphics.blit(TEXTURE, leftPos + 127, topPos + 54 , 127, 54, 2, 10);
			// graphics.blit(TEXTURE, leftPos + 109, topPos + 57, 108, 204, 40, 21);//pot
		}
		
		graphics.blit(TEXTURE, leftPos + 120, topPos + 64, 197, 105+11*((menu.process_num.getValue() >> 1)&3), 16, 10);
		
		if (menu.energy.getEnergyStored() > 0) {
			if (process > 0 && process < 1)
				graphics.blit(TEXTURE, leftPos + 80, topPos + 21, 176 + 7, 155, 7, 21);
			else
				graphics.blit(TEXTURE, leftPos + 80, topPos + 21, 176 + 7 * 2, 155, 7, 21);
		} else {
			graphics.blit(TEXTURE, leftPos + 80, topPos + 21, 176, 155, 7, 21);
		}
	}

}
