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

import java.util.function.Consumer;

import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.AccessableFluidInfoArea;
import com.teammoeg.immersiveindustry.util.IIContainerScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrucibleScreen extends IIContainerScreen<CrucibleContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IIMain.MODID, "textures/gui/crucible.png");

    public CrucibleScreen(CrucibleContainer container, Inventory inv, Component title) {
        super(container, inv, title,TEXTURE);
    }

    @Override
    protected void makeInfoAreas()
    {
        addInfoArea(
                new AccessableFluidInfoArea(menu.tank, new Rect2i(leftPos+145, topPos+12, 16, 47), 236, 32, 20, 51, TEXTURE)
        );
    }
    
    @Override
	protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray) {
		super.gatherAdditionalTooltips(mouseX, mouseY, addLine, addGray);
        if (mouseX >= this.leftPos + 10 && mouseX < this.leftPos + 19 && mouseY > this.topPos + 10 && mouseY < this.topPos + 67) {
            //Temperature in celsius
            int k = menu.temperature.getValue();
            addLine.accept(Component.translatable("gui.immersiveindustry.crucible.temperature_in_celsius", k));
        }
	}


    @Override
	protected void drawContainerBackgroundPre(GuiGraphics graphics, float partialTicks, int x, int y) {
    	int temp=menu.temperature.getValue();
    	 if (temp > 0) {
             int bar = temp / 30;
             graphics.blit(TEXTURE, leftPos + 12, topPos + 67 - bar, 177, 83 - bar, 5, bar);
         }
    	 float burnTime=menu.fuelProcess.getValue();
         if (burnTime > 0) {
             int h = (int) (burnTime*12);
             graphics.blit(TEXTURE, leftPos + 84, topPos + 47 - h, 179, 1 + 12 - h, 9, h);
         }
         float process=menu.process.getValue();
         if (process > 0) {
             int h = (int) (21 * process);
             graphics.blit(TEXTURE, leftPos + 76, topPos + 14, 204, 15, h, 15);
         }
         
         if (menu.hasPreheater.getValue()) {
        	 graphics.blit(TEXTURE, leftPos + 28, topPos + 54, 199, 32, 12, 11);
         }
	}


}
