package com.teammoeg.immersiveindustry.util;

import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class AccessableFluidInfoArea extends FluidInfoArea implements AccessableInfoArea<FluidStack> {
	Rect2i area;
	IFluidTank tank;
	public AccessableFluidInfoArea(IFluidTank tank, Rect2i area, int overlayUMin, int overlayVMin, int overlayWidth, int overlayHeight, ResourceLocation overlayTexture) {
		super(tank, area, overlayUMin, overlayVMin, overlayWidth, overlayHeight, overlayTexture);
		this.area=area;
		this.tank=tank;
	}
	@Override
	public Rect2i getArea() {
		return area;
	}
	@Override
	public FluidStack getStack() {
		return tank.getFluid();
	}
}
