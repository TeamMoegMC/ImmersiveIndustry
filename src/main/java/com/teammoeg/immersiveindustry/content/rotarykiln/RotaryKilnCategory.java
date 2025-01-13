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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RotaryKilnCategory implements IRecipeCategory<RotaryKilnRecipe> {
    public static RecipeType<RotaryKilnRecipe> UID = new RecipeType<>(new ResourceLocation(IIMain.MODID, "rotary_kiln"),RotaryKilnRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;

    public RotaryKilnCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(IIContent.IIMultiblocks.ROTARY_KILN.blockItem().get()));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png"), 9, 22, 143, 59);
        this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png"), 197, 1, 18, 48);
        IDrawableStatic arrow = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/rotary_kiln.png"), 178, 59, 38, 16);
        ARROW = guiHelper.createAnimatedDrawable(arrow, 40, StartDirection.LEFT, false);
    }

    @Override
	public RecipeType<RotaryKilnRecipe> getRecipeType() {
		return UID;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RotaryKilnRecipe recipe, IFocusGroup focuses) {
		
		builder.addSlot(RecipeIngredientRole.INPUT,2, 17).addItemStacks(recipe.input.getMatchingStackList());
		IRecipeSlotBuilder itemOut=builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 40);
        if(!recipe.output.isEmpty()) {
        	itemOut.addItemStack(recipe.output);
        }
        IRecipeSlotBuilder itemSecOut=builder.addSlot(RecipeIngredientRole.OUTPUT, 102, 40);
        if(recipe.secoutput!=null){
        	itemSecOut.addItemStack(recipe.secoutput.stack().get()).addTooltipCallback((l,t)->{
            		t.add(LangUtil.translate("gui.jei.category." + IIMain.MODID + ".rotary_kiln.chance",((int)(recipe.secoutput.chance()*10000))/100).withStyle(ChatFormatting.BLUE));
            });
        }
        IRecipeSlotBuilder fluidOut=builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 4).setFluidRenderer(3200, false, 16, 47).setOverlay(TANK, 0, 0);
        if (!recipe.output_fluid.isEmpty()) {
        	fluidOut.addIngredient(ForgeTypes.FLUID_STACK, recipe.output_fluid);

        }
	}

	@Override
	public void draw(RotaryKilnRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		ARROW.draw(guiGraphics, 79, 22);
	}


    public Component getTitle() {
        return (LangUtil.translate("gui.jei.category." + IIMain.MODID + ".rotary_kiln"));
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
