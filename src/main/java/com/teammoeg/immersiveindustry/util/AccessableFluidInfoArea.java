package com.teammoeg.immersiveindustry.util;

import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

public class AccessableFluidInfoArea extends FluidInfoArea implements AccessableInfoArea<FluidStack> {
	Rect2i area;
	IFluidTank tank;
	public AccessableFluidInfoArea(IFluidTank tank, Rect2i area, int overlayUMin, int overlayVMin, int overlayWidth, int overlayHeight, ResourceLocation overlayTexture) {
		// IE 12 infers the overlay size from the area; only the UV origin is passed on.
		super(tank, area, overlayUMin, overlayVMin, overlayTexture);
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
