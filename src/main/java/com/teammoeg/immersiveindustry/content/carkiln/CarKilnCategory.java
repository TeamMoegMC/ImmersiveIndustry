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

import blusunrize.immersiveengineering.common.util.compat.jei.JEIIngredientStackListBuilder;

import java.util.Arrays;

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleRecipe;
import com.teammoeg.immersiveindustry.util.JEISlotBuilder;
import com.teammoeg.immersiveindustry.util.JEISlotBuilder.JEISlotBuilderBuilder;
import com.teammoeg.immersiveindustry.util.LangUtil;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CarKilnCategory implements IRecipeCategory<CarKilnRecipe> {
	public static RecipeType<CarKilnRecipe> UID = new RecipeType<>(new ResourceLocation(IIMain.MODID, "car_kiln"), CarKilnRecipe.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;
	private IDrawable TANK;
	private IDrawableAnimated ARROW;

	public CarKilnCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableItemStack(new ItemStack(IIContent.IIMultiblocks.CAR_KILN.blockItem().get()));
		this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/car_kiln.png"), 6, 16, 143, 71);
		this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/car_kiln.png"), 197, 1, 18, 48);
		IDrawableStatic arrow = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/car_kiln.png"), 177, 57, 37, 17);
		ARROW = guiHelper.createAnimatedDrawable(arrow, 40, StartDirection.LEFT, false);
	}

	@Override
	public RecipeType<CarKilnRecipe> getRecipeType() {
		return UID;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CarKilnRecipe recipe, IFocusGroup focuses) {
		JEISlotBuilder<ItemStack> itemInput = JEISlotBuilder.itemStack(builder, JEIIngredientStackListBuilder.make(recipe.inputs).build()).asInput();
		for (int i = 0; i < 4; ++i) {
			itemInput.addSlot(29 + i % 2 * 18, 5 + i / 2 * 18);
		}
		JEISlotBuilder<ItemStack> itemOutput = JEISlotBuilder.itemStack(builder, recipe.output).asInput();
		for (int i = 0; i < 5; ++i) {
			itemOutput.addSlot(90 + i % 3 * 18, 34 + i / 3 * 18);
		}
		IRecipeSlotBuilder fluidSlot = builder.addSlot(RecipeIngredientRole.INPUT, 4, 10).setFluidRenderer(3200, false, 16, 47).setOverlay(TANK, 0, 0);
		if (recipe.input_fluid!=null) {
			fluidSlot.addIngredients(ForgeTypes.FLUID_STACK, recipe.input_fluid.getMatchingFluidStacks());
			//if (recipe.start_fluid_cost != 0)
			//	fluidSlot.addTooltipCallback((l, t) -> t.add(LangUtil.translate("gui.jei.tooltip.immersiveindustry.start_cost", recipe.start_fluid_cost)));
		}
	}

	@Override
	public void draw(CarKilnRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		ARROW.draw(guiGraphics, 77, 13);
	}

	public Component getTitle() {
		return (LangUtil.translate("gui.jei.category." + IIMain.MODID + ".car_kiln"));
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

}
