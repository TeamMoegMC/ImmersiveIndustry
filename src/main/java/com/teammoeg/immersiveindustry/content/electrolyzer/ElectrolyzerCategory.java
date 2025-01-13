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

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.LangUtil;

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
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ElectrolyzerCategory implements IRecipeCategory<ElectrolyzerRecipe> {
    public static RecipeType<ElectrolyzerRecipe> UID = new RecipeType<>(new ResourceLocation(IIMain.MODID, "electrolyzer"),ElectrolyzerRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;
    public ElectrolyzerCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(IIContent.IIBlocks.electrolyzer.get()));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/electrolyzer.png"), 17, 14, 115, 60);
        this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/electrolyzer.png"),197,1,18, 48);
        IDrawableStatic arrow=guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/electrolyzer.png"),178,57,21,15);
        ARROW=guiHelper.createAnimatedDrawable(arrow,20,StartDirection.LEFT,false);
    }

    @Override
	public RecipeType<ElectrolyzerRecipe> getRecipeType() {
		return UID;
	}
	@Override
	public void draw(ElectrolyzerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		ARROW.draw(guiGraphics,59, 21);
	}
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ElectrolyzerRecipe recipe, IFocusGroup focuses) {
		
		IRecipeSlotBuilder fluidSlot=builder.addSlot(RecipeIngredientRole.INPUT,4,4).setFluidRenderer(50, false, 16, 47).setOverlay(TANK, 0, 0);
		if (recipe.inputs.length > 0)
			builder.addSlot(RecipeIngredientRole.INPUT, 33, 19).addItemStacks(Arrays.asList(recipe.inputs[0].getMatchingStacks()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 19).addItemStack(RecipeUtil.getResultItem(recipe));
		
		if(recipe.input_fluid!=null)
			fluidSlot.addIngredients(ForgeTypes.FLUID_STACK, recipe.input_fluid.getMatchingFluidStacks());

		
	}
	public Component getTitle() {
        return (LangUtil.translate("gui.jei.category." + IIMain.MODID + ".electrolyzer"));
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
