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

package com.teammoeg.immersiveindustry.content.rotarykiln;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class RotaryKilnCategory implements IRecipeCategory<RotaryKilnRecipe> {
    public static ResourceLocation UID = new ResourceLocation(IIMain.MODID, "rotary_kiln");
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;

    public RotaryKilnCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableIngredient(new ItemStack(IIContent.IIMultiblocks.rotary_kiln));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png"), 9, 22, 143, 58);
        this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png"), 197, 1, 18, 48);
        IDrawableStatic arrow = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png"), 178, 59, 38, 16);
        ARROW = guiHelper.createAnimatedDrawable(arrow, 40, StartDirection.LEFT, false);
    }

    @Override
    public void draw(RotaryKilnRecipe recipe, MatrixStack transform, double mouseX, double mouseY) {
        ARROW.draw(transform, 79, 22);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends RotaryKilnRecipe> getRecipeClass() {
        return RotaryKilnRecipe.class;
    }


    public String getTitle() {
        return (new TranslationTextComponent("gui.jei.category." + IIMain.MODID + ".rotary_kiln").getString());
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
    public void setIngredients(RotaryKilnRecipe recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.input.getMatchingStacks()[0]);

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        ingredients.setOutput(VanillaTypes.FLUID, recipe.output_fluid);
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RotaryKilnRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiItemStacks.init(0, true, 2, 17);

        guiItemStacks.init(1, false, 94, 40);
        guiFluidStacks.init(2, false, 124, 4, 16, 47, 3200, false, TANK);

        guiFluidStacks.set(ingredients);
        guiItemStacks.set(ingredients);
    }
}
