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

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.FluidRecipeSimulator;
import com.teammoeg.immersiveindustry.util.JEISlotBuilder;
import com.teammoeg.immersiveindustry.util.LangUtil;
import com.teammoeg.immersiveindustry.util.RecipeSimulateHelper;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ChemicalCategory implements IRecipeCategory<ChemicalRecipe> {

    public static RecipeType<ChemicalRecipe> UID = new RecipeType<>(new ResourceLocation(IIMain.MODID, "chemical"),ChemicalRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawableAnimated ARROW;
    public ChemicalCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(IIContent.IIMultiblocks.CHEMICAL_REACTOR.blockItem().get()));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/chemical_reactor_jei.png"), 0, 0, 176, 82);
        IDrawableStatic arrow=guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/chemical_reactor_jei.png"),55,82,65,7);
        ARROW=guiHelper.createAnimatedDrawable(arrow,40, IDrawableAnimated.StartDirection.LEFT,false);
    }

    @Override
	public RecipeType<ChemicalRecipe> getRecipeType() {
		return UID;
	}

	public Component getTitle() {
        return (LangUtil.translate("gui.jei.category." + IIMain.MODID + ".chemical_reactor"));
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }

    @Override
    public void draw(ChemicalRecipe recipe,IRecipeSlotsView view,GuiGraphics transform, double mouseX, double mouseY) {
        ARROW.draw(transform,55, 66);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, ChemicalRecipe recipe, IFocusGroup ingredients) {
        
        JEISlotBuilder<ItemStack> itemInput=JEISlotBuilder.itemStack(recipeLayout, RecipeSimulateHelper.expand(recipe.inputs)).asInput();
        itemInput.addSlot(33, 10);
        itemInput.addSlot(33, 33);
        itemInput.addSlot(33, 56);
        JEISlotBuilder<FluidStack> fluidInput=JEISlotBuilder.fluidStack(recipeLayout, FluidRecipeSimulator.expand(recipe.input_fluids)).asInput();
        fluidInput.addSlot(10, 10).setFluidRenderer(2000, false, 16, 16);
        fluidInput.addSlot(10, 33).setFluidRenderer(2000, false, 16, 16);
        fluidInput.addSlot(10, 56).setFluidRenderer(2000, false, 16, 16);
        JEISlotBuilder<ItemStack> itemOutput=JEISlotBuilder.itemStack(recipeLayout, recipe.outputs).asOutput();
        itemOutput.addSlot(150, 10);
        itemOutput.addSlot(150, 33);
        itemOutput.addSlot(150, 56);
        JEISlotBuilder<FluidStack> fluidOutput=JEISlotBuilder.fluidStack(recipeLayout, recipe.output_fluids).asOutput();
        fluidOutput.addSlot(127, 10).setFluidRenderer(2000, false, 16, 16);
        fluidOutput.addSlot(127, 33).setFluidRenderer(2000, false, 16, 16);
        fluidOutput.addSlot(127, 56).setFluidRenderer(2000, false, 16, 16);
        

    }
}
