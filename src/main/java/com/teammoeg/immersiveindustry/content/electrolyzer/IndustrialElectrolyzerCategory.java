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

public class IndustrialElectrolyzerCategory<T extends ElectrolyzerRecipe> implements IRecipeCategory<ElectrolyzerRecipe> {
    public static ResourceLocation UID = new ResourceLocation(IIMain.MODID, "industrial_electrolyzer");
    private IDrawable BACKGROUND;
    private IDrawable ICON;

    public IndustrialElectrolyzerCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableIngredient(new ItemStack(IIContent.IIMultiblocks.industrial_electrolyzer));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png"), 6, 8, 146, 137);
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
        return (new TranslationTextComponent("gui.jei.category." + IIMain.MODID + ".industrial_electrolyzer").getString());
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
        ingredients.setInputLists(VanillaTypes.ITEM, JEIIngredientStackListBuilder.make(recipe.input).add(recipe.input2).build());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        ingredients.setOutput(VanillaTypes.FLUID, recipe.output_fluid);
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ElectrolyzerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        if (recipe.input_fluid != null) {
            guiFluidStacks.init(3, true, 4, 17, 16, 47, FluidAttributes.BUCKET_VOLUME / 20, false, null);
            guiFluidStacks.set(3, recipe.input_fluid.getMatchingFluidStacks());
        }
        if (recipe.output_fluid != null) {
            guiFluidStacks.init(4, false, 126, 17, 16, 47, FluidAttributes.BUCKET_VOLUME / 20, false, null);
            guiFluidStacks.set(4, recipe.output_fluid);
        }
        guiItemStacks.init(0, true, 28, 27);
        guiItemStacks.init(1, true, 46, 27);
        guiItemStacks.init(2, false, 102, 27);

        guiItemStacks.set(ingredients);
    }
}
