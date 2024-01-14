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

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.compat.jei.JEIIngredientStackListBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class CrucibleCategory implements IRecipeCategory<CrucibleRecipe> {

    public static ResourceLocation UID = new ResourceLocation(IIMain.MODID, "crucible");
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;
    public CrucibleCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableIngredient(new ItemStack(IIContent.IIMultiblocks.crucible));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/crucible_jei.png"), 19, 3, 150, 65);
        this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/crucible.png"),238,34,18, 48);
        IDrawableStatic arrow=guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/crucible.png"),204,15,21,15);
        ARROW=guiHelper.createAnimatedDrawable(arrow,40, IDrawableAnimated.StartDirection.LEFT,false);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends CrucibleRecipe> getRecipeClass() {
        return CrucibleRecipe.class;
    }


    public String getTitle() {
        return (new TranslationTextComponent("gui.jei.category." + IIMain.MODID + ".crucible").getString());
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
    public void draw(CrucibleRecipe recipe, MatrixStack transform, double mouseX, double mouseY) {
        ARROW.draw(transform,57, 11);
        int k = recipe.temperature - recipe.temperature % 100 + 300;
        String temperature = new TranslationTextComponent("gui.immersiveindustry.crucible.tooltip.temperature_in_kelvin", k).getString();
        ClientUtils.font().drawString(transform, temperature, 45.0F, 52.0F, 14833698);
    }
    @Override
    public void setIngredients(CrucibleRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, JEIIngredientStackListBuilder.make(recipe.inputs).build());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        if (recipe.output_fluid != FluidStack.EMPTY)
            ingredients.setOutput(VanillaTypes.FLUID, recipe.output_fluid);
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrucibleRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiFluidStacks.init(0, false, 126, 9, 16, 47, 14400, false,TANK);
        if (recipe.output_fluid != FluidStack.EMPTY) {
            guiFluidStacks.set(0, recipe.output_fluid);
        }
        guiItemStacks.init(0, true, 10, 8);
        guiItemStacks.init(1, true, 31, 8);
        guiItemStacks.init(2, true, 10, 29);
        guiItemStacks.init(3, true, 31, 29);
        guiItemStacks.init(4, false, 89, 8);

        guiItemStacks.set(ingredients);
    }
}
