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

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.JEISlotBuilder;
import com.teammoeg.immersiveindustry.util.LangUtil;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIIngredientStackListBuilder;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
import net.minecraftforge.fluids.FluidStack;

public class CrucibleCategory implements IRecipeCategory<CrucibleRecipe> {

    public static RecipeType<CrucibleRecipe> UID = new RecipeType<>(new ResourceLocation(IIMain.MODID, "crucible"),CrucibleRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;
    public CrucibleCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(IIContent.IIMultiblocks.CRUCIBLE.blockItem().get()));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/crucible_jei.png"), 19, 3, 150, 65);
        this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/crucible.png"),238,34,18, 48);
        IDrawableStatic arrow=guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/crucible.png"),204,15,21,15);
        ARROW=guiHelper.createAnimatedDrawable(arrow,40, IDrawableAnimated.StartDirection.LEFT,false);
    }

    @Override
	public RecipeType<CrucibleRecipe> getRecipeType() {
		return UID;
	}

	public Component getTitle() {
        return (LangUtil.translate("gui.jei.category." + IIMain.MODID + ".crucible"));
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
    public void draw(CrucibleRecipe recipe,IRecipeSlotsView view,GuiGraphics transform, double mouseX, double mouseY) {
        ARROW.draw(transform,57, 11);
        int k = recipe.temperature - recipe.temperature % 100 + 300;
        String temperature = LangUtil.translate("gui.immersiveindustry.crucible.tooltip.temperature_in_kelvin", k).getString();
        transform.drawString(ClientUtils.font(), temperature, 45, 52, 14833698);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, CrucibleRecipe recipe, IFocusGroup ingredients) {
        recipeLayout.addSlot(RecipeIngredientRole.INPUT, 0, 0);
        IRecipeSlotBuilder fluidOut=recipeLayout.addSlot(RecipeIngredientRole.INPUT, 126, 9).setFluidRenderer(14400, false, 16, 47).setOverlay(TANK, 0, 0);
        if (recipe.output_fluid != FluidStack.EMPTY) {
        	fluidOut.addIngredient(ForgeTypes.FLUID_STACK, recipe.output_fluid);
        }
        JEISlotBuilder<ItemStack> itemInput=JEISlotBuilder.itemStack(recipeLayout, JEIIngredientStackListBuilder.make(recipe.inputs).build()).asInput();
        itemInput.addSlot(11,  9);
        itemInput.addSlot(32,  9);
        itemInput.addSlot(11, 30);
        itemInput.addSlot(32, 30);

        
        recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 89, 8).addItemStack(RecipeUtil.getResultItem(recipe));

    }
}
