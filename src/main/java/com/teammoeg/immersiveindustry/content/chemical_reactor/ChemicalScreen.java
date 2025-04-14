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

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.LangUtil;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChemicalScreen extends IEContainerScreen<ChemicalContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/crucible.png");

    public ChemicalScreen(ChemicalContainer container, Inventory inv, Component title) {
        super(container, inv, title,TEXTURE);
    }

    @Nonnull
    @Override
    protected List<InfoArea> makeInfoAreas()
    {
        return ImmutableList.of(
                new FluidInfoArea(menu.tank[0], new Rect2i(leftPos+20, topPos+12, 16, 47), 236, 33, 20, 51, TEXTURE),
                new FluidInfoArea(menu.tank[1], new Rect2i(leftPos+40, topPos+12, 16, 47), 236, 32, 20, 51, TEXTURE),
                new FluidInfoArea(menu.tank[2], new Rect2i(leftPos+60, topPos+12, 16, 47), 236, 32, 20, 51, TEXTURE),
                new FluidInfoArea(menu.tank[3], new Rect2i(leftPos+80, topPos+12, 16, 47), 236, 32, 20, 51, TEXTURE),
                new FluidInfoArea(menu.tank[4], new Rect2i(leftPos+100, topPos+12, 16, 47), 236, 32, 20, 51, TEXTURE),
                new FluidInfoArea(menu.tank[5], new Rect2i(leftPos+120, topPos+12, 16, 47), 236, 32, 20, 51, TEXTURE),
                new EnergyInfoArea(leftPos+140, topPos+12, menu.energy)
        );
    }
    
    @Override
	protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray) {
		super.gatherAdditionalTooltips(mouseX, mouseY, addLine, addGray);
	}


    @Override
	protected void drawContainerBackgroundPre(GuiGraphics graphics, float partialTicks, int x, int y) {
         float process=menu.process.getValue();
         if (process > 0) {
             int h = (int) (21 * process);
             graphics.blit(TEXTURE, leftPos + 76, topPos + 14, 204, 15, h, 15);
         }
	}


}
