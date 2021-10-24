/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.content.electrolyzer;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIIngredientStackListBuilder;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;

public class ElectrolyzerCategory<T extends ElectrolyzerRecipe> implements IRecipeCategory<ElectrolyzerRecipe> {
    public static ResourceLocation UID = new ResourceLocation(IIMain.MODID, "electrolyzer");
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable overlay;

    public ElectrolyzerCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableIngredient(new ItemStack(IIContent.IIBlocks.burning_chamber));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/electrolyzer.png"), 19, 0, 130, 40);
        this.overlay = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/container/electrolyzer.png"), 176, 0, 16, 71);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends ElectrolyzerRecipe> getRecipeClass() {
        return ElectrolyzerRecipe.class;
    }


    public String getTitle() {
        return (new TranslationTextComponent("gui.jei.category." + IIMain.MODID + ".electrolyzer").getString());
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
    public void setIngredients(ElectrolyzerRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.FLUID, recipe.input_fluid.getMatchingFluidStacks());
        ingredients.setInputLists(VanillaTypes.ITEM, JEIIngredientStackListBuilder.make(recipe.input).build());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ElectrolyzerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        if (recipe.input_fluid != null) {
            guiFluidStacks.init(0, true, 7, 10, 16, 47, FluidAttributes.BUCKET_VOLUME / 20, false, overlay);
            guiFluidStacks.set(0, recipe.input_fluid.getMatchingFluidStacks());
        }
        guiItemStacks.init(0, true, 10, 10);

        guiItemStacks.init(1, false, 86, 10);

        guiItemStacks.set(ingredients);
    }
}
