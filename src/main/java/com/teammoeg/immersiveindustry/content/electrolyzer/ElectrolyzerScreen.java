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

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
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

public class ElectrolyzerScreen extends IEContainerScreen<ElectrolyzerContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/electrolyzer.png");

    public ElectrolyzerScreen(ElectrolyzerContainer container, Inventory inventoryPlayer, Component title) {
        super(container, inventoryPlayer, title, TEXTURE);
    }

    @Nonnull
    @Override
    protected List<InfoArea> makeInfoAreas()
    {
        return ImmutableList.of(
                new FluidInfoArea(menu.tank, new Rect2i(leftPos+21, topPos+18, 16, 47), 195, 0, 20, 51, TEXTURE),
                new EnergyInfoArea(leftPos+158, topPos+22, menu.energyStorage)
        );
    }

    @Override
    protected void drawContainerBackgroundPre(GuiGraphics transform, float partial, int x, int y) {

        float process = menu.guiProgress.get();
        int h = (int) (21 * process);
        transform.blit(TEXTURE, leftPos + 76, topPos + 35, 178, 57, h, 15);

    }


}
