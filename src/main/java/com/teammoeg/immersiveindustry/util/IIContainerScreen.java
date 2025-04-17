package com.teammoeg.immersiveindustry.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.client.TextUtils;
import blusunrize.immersiveengineering.client.gui.elements.ITooltipWidget;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class IIContainerScreen<C extends AbstractContainerMenu> extends AbstractContainerScreen<C> {
	protected final List<InfoArea> infoAreas = new ArrayList<>();
	protected final ResourceLocation background;

	public IIContainerScreen(C inventorySlotsIn, Inventory inv, Component title, ResourceLocation background) {
		super(inventorySlotsIn, inv, title);
		this.background = background;
	}

	@Override
	protected void init() {
		super.init();
		this.infoAreas.clear();
		makeInfoAreas();
		this.inventoryLabelY = this.imageHeight - 91;
	}
	protected void addInfoArea(InfoArea area) {
		infoAreas.add(area);
	}
	protected void makeInfoAreas() {
	}
	public AccessableInfoArea getHoveredStack(int mouseX,int mouseY) {
		for(InfoArea ia:infoAreas) {
			if(ia instanceof AccessableInfoArea aia) {
				if(aia.getArea().contains(mouseX, mouseY))
					return aia;
			}
		}
		return null;
	}
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, title, titleLabelX, titleLabelY, Lib.COLOUR_I_ImmersiveOrange, true);
		graphics.drawString(this.font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, Lib.COLOUR_I_ImmersiveOrange, true);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, partialTicks);
		List<Component> tooltip = new ArrayList<>();
		for (InfoArea area : infoAreas)
			area.fillTooltip(mouseX, mouseY, tooltip);
		for (GuiEventListener w : children())
			if (w.isMouseOver(mouseX, mouseY) && w instanceof ITooltipWidget ttw)
				ttw.gatherTooltip(mouseX, mouseY, tooltip);
		gatherAdditionalTooltips(
			mouseX, mouseY, tooltip::add, t -> tooltip.add(TextUtils.applyFormat(t, ChatFormatting.GRAY)));
		if (!tooltip.isEmpty())
			graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
		else
			this.renderTooltip(graphics, mouseX, mouseY);
	}

	protected boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y
			&& mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

	@Override
	protected final void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
		drawBackgroundTexture(graphics);
		drawContainerBackgroundPre(graphics, partialTicks, x, y);
		for (InfoArea area : infoAreas)
			area.draw(graphics);
	}

	protected void drawBackgroundTexture(GuiGraphics graphics) {
		graphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	protected void drawContainerBackgroundPre(GuiGraphics graphics, float partialTicks, int x, int y) {
	}

	protected void gatherAdditionalTooltips(
		int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray) {
	}
}