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

import java.util.List;
import java.util.stream.Collectors;

import com.teammoeg.immersiveindustry.IIContent;
import com.teammoeg.immersiveindustry.IIMain;
import com.teammoeg.immersiveindustry.util.JEISlotBuilder;
import com.teammoeg.immersiveindustry.util.LangUtil;

import blusunrize.immersiveengineering.common.util.compat.jei.JEIIngredientStackListBuilder;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class IndustrialElectrolyzerCategory implements IRecipeCategory<ElectrolyzerRecipe> {
    public static RecipeType<ElectrolyzerRecipe> UID = new RecipeType<>(new ResourceLocation(IIMain.MODID, "industrial_electrolyzer"),ElectrolyzerRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;
    public static final TagKey<Item> Electrode_Tag = ItemTags.create(new ResourceLocation(IIMain.MODID, "electrodes"));

    public IndustrialElectrolyzerCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(IIContent.IIMultiblocks.INDUSTRIAL_ELECTROLYZER.blockItem().get()));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png"), 6,6,145, 68);
        this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png"),197,1,18, 48);
        IDrawableStatic arrow=guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png"),178,57,21,15);
        ARROW=guiHelper.createAnimatedDrawable(arrow,20,StartDirection.LEFT,false);
    }


    @Override
	public RecipeType<ElectrolyzerRecipe> getRecipeType() {
		return UID;
	}


	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ElectrolyzerRecipe recipe, IFocusGroup focuses) {
		JEISlotBuilder<ItemStack> itemInput=JEISlotBuilder.itemStack(builder, JEIIngredientStackListBuilder.make(recipe.inputs).build()).asInput();
		itemInput.addSlot(28, 33);
		itemInput.addSlot(46, 33);
		List<ItemStack> electrode = ForgeRegistries.ITEMS.tags().getTag(IndustrialElectrolyzerCategory.Electrode_Tag).stream().map(ItemStack::new).collect(Collectors.toList());
		builder.addSlot(RecipeIngredientRole.CATALYST, 28, 4).addItemStacks(electrode);
		builder.addSlot(RecipeIngredientRole.CATALYST, 46, 4).addItemStacks(electrode);

		
		builder.addSlot(RecipeIngredientRole.OUTPUT,102, 33).addItemStack(RecipeUtil.getResultItem(recipe));
		
		IRecipeSlotBuilder fluidIn=builder.addSlot(RecipeIngredientRole.INPUT, 4, 19).setFluidRenderer(1000, false, 16, 47).setOverlay(TANK, 0, 0);
		IRecipeSlotBuilder fluidOut=builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 19).setFluidRenderer(1000, false, 16, 47).setOverlay(TANK, 0, 0);
		if (recipe.input_fluid != null) {
        	fluidIn.addIngredients(ForgeTypes.FLUID_STACK, recipe.input_fluid.getMatchingFluidStacks());
        }
        if (recipe.output_fluid != null) {
        	fluidOut.addIngredient(ForgeTypes.FLUID_STACK, recipe.output_fluid);
        }

		
	}


	@Override
	public void draw(ElectrolyzerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		ARROW.draw(guiGraphics,71, 34);
	}


	public Component getTitle() {
        return (LangUtil.translate("gui.jei.category." + IIMain.MODID + ".industrial_electrolyzer"));
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
