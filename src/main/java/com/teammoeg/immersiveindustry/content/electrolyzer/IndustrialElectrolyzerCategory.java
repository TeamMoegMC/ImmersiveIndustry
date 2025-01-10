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
import com.teammoeg.immersiveindustry.content.crucible.CrucibleRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IndustrialElectrolyzerCategory implements IRecipeCategory<ElectrolyzerRecipe> {
    public static RecipeType<ElectrolyzerRecipe> UID = new RecipeType<>(new ResourceLocation(IIMain.MODID, "industrial_electrolyzer"),ElectrolyzerRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;
    public static final TagKey<Item> Electrode_Tag = ItemTags.create(new ResourceLocation(IIMain.MODID, "electrodes"));

    public IndustrialElectrolyzerCategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableIngredient(new ItemStack(IIContent.IIMultiblocks.industrial_electrolyzer));
        this.BACKGROUND = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png"), 6,6,145, 68);
        this.TANK = guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png"),197,1,18, 48);
        IDrawableStatic arrow=guiHelper.createDrawable(new ResourceLocation(IIMain.MODID, "textures/gui/industrial_electrolyzer.png"),178,57,21,15);
        ARROW=guiHelper.createAnimatedDrawable(arrow,20,StartDirection.LEFT,false);
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
        return (LangUtil.translate("gui.jei.category." + IIMain.MODID + ".industrial_electrolyzer").getString());
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
	public void draw(ElectrolyzerRecipe recipe, MatrixStack transform, double mouseX, double mouseY)
	{
		
		ARROW.draw(transform,71, 34);
	}
    @Override
    public void setIngredients(ElectrolyzerRecipe recipe, IIngredients ingredients) {
        if(recipe.inputs.length==2)
        	ingredients.setInputLists(VanillaTypes.ITEM, JEIIngredientStackListBuilder.make(recipe.inputs[0]).add(recipe.inputs[1]).build());
        else if(recipe.inputs.length==1)
        	ingredients.setInputLists(VanillaTypes.ITEM, JEIIngredientStackListBuilder.make(recipe.inputs[0]).build());

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
        if (recipe.output_fluid != null)
            ingredients.setOutput(VanillaTypes.FLUID, recipe.output_fluid);
        if (recipe.input_fluid != null)
            ingredients.setInputLists(VanillaTypes.FLUID, Arrays.asList(recipe.input_fluid.getMatchingFluidStacks()));
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ElectrolyzerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiFluidStacks.init(0, true, 4, 19, 16, 47, FluidAttributes.BUCKET_VOLUME, false,TANK);
        if (recipe.input_fluid != null) {
            guiFluidStacks.set(0, recipe.input_fluid.getMatchingFluidStacks());
        }
        guiFluidStacks.init(1, false, 126, 19, 16, 47, FluidAttributes.BUCKET_VOLUME, false, TANK);
        if (recipe.output_fluid != null) {
            guiFluidStacks.set(1, recipe.output_fluid);
        }
        if (recipe.inputs.length > 0) {
            guiItemStacks.init(0, true, 27, 32);
            guiItemStacks.init(1, true, 45, 32);
        }
        guiItemStacks.init(2, true, 27, 3);
        guiItemStacks.init(3, true, 45, 3);
        guiItemStacks.init(4, false, 101, 32);
        guiItemStacks.set(ingredients);
        List<ItemStack> electrode = ForgeRegistries.ITEMS.tags().getTag(IndustrialElectrolyzerCategory.Electrode_Tag).stream().map(ItemStack::new).collect(Collectors.toList());
        guiItemStacks.set(2, electrode);
        guiItemStacks.set(3, electrode);
        
    }
}
